package com.itsvks.layouteditor;

import android.app.Application;
import android.content.Context;

public class LayoutEditor extends Application {
    
    private static LayoutEditor sIstance;
    
    @Override
    public void onCreate() {
        super.onCreate();
        sIstance = this;
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    }
    
    public static Context getContext() {
        return sIstance.getApplicationContext();
    }
}
