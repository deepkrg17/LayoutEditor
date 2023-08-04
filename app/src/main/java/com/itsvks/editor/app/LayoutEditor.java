package com.itsvks.editor.app;

import android.app.Application;
import com.google.android.material.color.DynamicColors;
import com.itsvks.editor.crash.CrashHandler;

public class LayoutEditor extends Application {
  
  private static LayoutEditor sIstance;

  @Override
  public void onCreate() {
    super.onCreate();
    sIstance = this;
    
    // DynamicColors.applyToActivitiesIfAvailable(this);
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
  }
  
  public static LayoutEditor getInstance() {
    return sIstance;
  }
}
