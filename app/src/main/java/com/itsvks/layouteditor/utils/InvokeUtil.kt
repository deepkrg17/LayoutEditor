package com.itsvks.layouteditor.utils;

import android.content.Context;
import android.view.View;

import com.itsvks.layouteditor.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InvokeUtil {

  @SuppressWarnings("unchecked")
  public static Object createView(String className, Context context) {
    try {
      Class clazz = Class.forName(className);
      Constructor constructor = clazz.getConstructor(Context.class);
      return constructor.newInstance(context);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  public static void invokeMethod(
      String methodName, String className, View target, String value, Context context) {
    try {
      Class clazz = Class.forName("com.itsvks.layouteditor.editor.callers." + className);
      Method method = clazz.getMethod(methodName, View.class, String.class, Context.class);
      method.invoke(clazz, target, value, context);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public static int getMipmapId(String name) {
    try {
      Class cls = R.mipmap.class;
      Field field = cls.getField(name);
      return field.getInt(cls);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return 0;
  }

  public static String getSuperClassName(String clazz) {
    try {
      return Class.forName(clazz).getSuperclass().getName();
    } catch (ClassNotFoundException e) {
      return null;
    }
  }
}
