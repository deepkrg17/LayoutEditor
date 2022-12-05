package com.itsvks.layouteditor;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.color.DynamicColors;

public class LayoutEditor extends Application {

  private static LayoutEditor sIstance;

  @Override
  public void onCreate() {
    super.onCreate();
    sIstance = this;
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    if (isAtLeastS() && DynamicColors.isDynamicColorAvailable()) {
      DynamicColors.applyToActivitiesIfAvailable(this);
    }
  }

  public static Context getContext() {
    return sIstance.getApplicationContext();
  }

  public static LayoutEditor getInstance() {
    return sIstance;
  }

  public static boolean isAtLeastS() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
  }

  public static boolean isAtLeastQ() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
  }

  public void updateTheme(int nightMode, Activity activity) {
    AppCompatDelegate.setDefaultNightMode(nightMode);
    activity.recreate();
  }
}
