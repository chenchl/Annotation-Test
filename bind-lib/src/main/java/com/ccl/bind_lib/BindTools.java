package com.ccl.bind_lib;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.fragment.app.Fragment;

/**
 * created by ccl on 2019/12/15
 **/
public class BindTools {

    public static void inject(Object o) {
        Class clazz = o.getClass();
        if (o instanceof View
                || o instanceof Activity
                || o instanceof Fragment) {
            try {
                //加载生成类
                Class bindViewClass = Class.forName(clazz.getName() + "_ViewBinding");
                Method method = bindViewClass.getMethod("bind", o.getClass());
                method.invoke(bindViewClass.newInstance(), o);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("参数异常");
        }
    }
}
