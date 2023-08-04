package com.itsvks.editor.utils;

import android.content.Context;
import android.view.View;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class InvokeUtil {
  @SuppressWarnings("unchecked")
  public static Object createView(String className, Context context) {
    try {
      Class clazz = Class.forName(className);
      Constructor constructor = clazz.getConstructor(Context.class);
      return constructor.newInstance(context);
    } catch (ClassNotFoundException
        | NoSuchMethodException
        | InstantiationException
        | InvocationTargetException
        | IllegalAccessException e) {
      e.printStackTrace();
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  public static void invokeMethod(String methodName, String className, View target, String value) {
    try {
      Class clazz = Class.forName("com.itsvks.editor.callers." + className);
      Method method = clazz.getMethod(methodName, View.class, String.class);
      method.invoke(clazz, target, value);
    } catch (ClassNotFoundException
        | NoSuchMethodException
        | InvocationTargetException
        | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public static int getMipmapId(String name) {
    try {
      Class cls = com.itsvks.editor.R.mipmap.class;
      Field field = cls.getField(name);
      return field.getInt(cls);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }

    return 0;
  }
}
