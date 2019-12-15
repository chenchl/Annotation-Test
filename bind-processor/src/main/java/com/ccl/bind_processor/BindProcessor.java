package com.ccl.bind_processor;

import com.ccl.bind_annotation.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.ccl.bind_annotation.BindView", "com.ccl.bind_annotation.BindClick"})
@AutoService(Processor.class)
public class BindProcessor extends AbstractProcessor {

    private Messager mMessager;
    private Elements mElementUtils;
    private Map<String, ClassBindViewProxy> mProxyMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "start processing...");
        mProxyMap.clear();
        //step 1 ??BindView
        Set<? extends Element> bindElement = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : bindElement) {
            VariableElement variableElement = (VariableElement) element;
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            String fullClassName = classElement.getQualifiedName().toString();
            ClassBindViewProxy proxy = mProxyMap.get(fullClassName);
            if (proxy == null) {
                proxy = new ClassBindViewProxy(mElementUtils, classElement);
                mProxyMap.put(fullClassName, proxy);
            }
            proxy.putElement(variableElement);
        }
        //????mProxyMap??????java??
        for (String key : mProxyMap.keySet()) {
            ClassBindViewProxy proxyInfo = mProxyMap.get(key);
            try {
                mMessager.printMessage(Diagnostic.Kind.NOTE, " --> create " + proxyInfo.getProxyClassFullName());
                JavaFile javaFile = JavaFile.builder(proxyInfo.getPackageName(), proxyInfo.generateJavaCodejavajoet()).build();
                javaFile.writeTo(processingEnv.getFiler());
               /* //??java????? ???+???????????
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(proxyInfo.getProxyClassFullName(), proxyInfo.getTypeElement());
                Writer writer = jfo.openWriter();
                String s = proxyInfo.generateJavaCode();
                mMessager.printMessage(Diagnostic.Kind.NOTE, " --> create " + s);
                writer.write(s);
                writer.flush();
                writer.close();*/
            } catch (IOException e) {
                mMessager.printMessage(Diagnostic.Kind.NOTE, " --> create " + proxyInfo.getProxyClassFullName() + "error");
            }
        }
        mProxyMap.clear();

        mMessager.printMessage(Diagnostic.Kind.NOTE, "end processing...");
        return true;
    }
}
