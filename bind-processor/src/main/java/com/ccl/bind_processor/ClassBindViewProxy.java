package com.ccl.bind_processor;

import com.ccl.bind_annotation.BindView;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * created by ccl on 2019/12/15
 **/
public class ClassBindViewProxy {
    private final Elements mElementUtils;
    private final TypeElement classElement;
    private final String mPackageName;
    private final String mBindingClassName;
    //??????
    private Map<Integer, VariableElement> mVariableElementMap = new HashMap<>();

    public ClassBindViewProxy(Elements mElementUtils, TypeElement classElement) {
        this.mElementUtils = mElementUtils;
        this.classElement = classElement;
        //?????
        PackageElement packageElement = mElementUtils.getPackageOf(classElement);
        String packageName = packageElement.getQualifiedName().toString();
        //????
        String className = classElement.getSimpleName().toString();
        this.mPackageName = packageName;
        this.mBindingClassName = className + "_ViewBinding";
    }


    public void putElement(VariableElement variableElement) {
        int id = variableElement.getAnnotation(BindView.class).value();
        mVariableElementMap.put(id, variableElement);
    }

    public String getProxyClassFullName() {
        return mPackageName + "." + mBindingClassName;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public TypeElement getTypeElement() {
        return classElement;
    }

    /**
     * 生成java类
     *
     * @return
     */
    public TypeSpec generateJavaCodejavajoet() {
        TypeSpec bindingClass = TypeSpec.classBuilder(mBindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethodsjavajoet())
                .build();
        return bindingClass;

    }

    /**
     * 加入Method
     */
    private MethodSpec generateMethodsjavajoet() {
        ClassName host = ClassName.bestGuess(classElement.getQualifiedName().toString());
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(host, "host");

        for (int id : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            methodBuilder.addCode("host." + name + " = " + "(" + type + ")host.findViewById( " + id + ");");
            methodBuilder.addCode("\n");
        }
        return methodBuilder.build();
    }

    /**
     * ??java??
     *
     * @return
     */
    public String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(mPackageName).append(";\n\n");
        builder.append("import com.ccl.bind_lib.*;\n");
        builder.append('\n');
        builder.append("public class ").append(mBindingClassName);
        builder.append(" {\n");
        //????
        generateMethods(builder);
        builder.append('\n');
        builder.append("}\n");
        return builder.toString();
    }

    private void generateMethods(StringBuilder builder) {
        //????????????
        builder.append("public void bind(" + classElement.getQualifiedName() + " host ) {\n");
        //?????????
        for (int id : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(id);
            //?????
            String name = element.getSimpleName().toString();
            //??????
            String type = element.asType().toString();
            builder.append("host." + name).append(" = ");
            //??
            builder.append("(" + type + ")host.findViewById( " + id + ");\n");
        }
        builder.append("  }\n");
    }
}
