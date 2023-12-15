package com.itsvks.layouteditor;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.color.DynamicColors;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.managers.SharedPreferenceManager;

public class LayoutEditor extends Application {

  private static LayoutEditor sIstance;

  @Override
  public void onCreate() {
    super.onCreate();
    sIstance = this;
    SharedPreferenceManager.setContext(this);
        
    AppCompatDelegate.setDefaultNightMode(SharedPreferenceManager.loadPrefInt(getString(R.string.choose_theme), -1));
    if (SharedPreferenceManager.loadPrefBool(R.string.dynamic_colors, false) && isAtLeastS() && DynamicColors.isDynamicColorAvailable()) {
      DynamicColors.applyToActivitiesIfAvailable(this);
    }
  }

  public Context getContext() {
    return sIstance.getApplicationContext();
  }

  public static LayoutEditor getInstance() {
    return sIstance;
  }

  public boolean isAtLeastS() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
  }
  
  public boolean isAtLeastTiramisu() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
  }

  public boolean isAtLeastQ() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
  }

  public void updateTheme(int nightMode, Activity activity) {
    AppCompatDelegate.setDefaultNightMode(nightMode);
    activity.recreate();
  }
}
