package com.itsvks.layouteditor;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.itsvks.layouteditor.CrashHandler;

public class LayoutEditor extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context));
    }

    public static Context getContext() {
        return context;
    }
}
