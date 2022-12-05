package com.itsvks.layouteditor.managers;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.itsvks.layouteditor.LayoutEditor;

public class PreferencesManager {

  public static boolean isEnableVibration() {
    return getPrefs().getBoolean("vibration", false);
  }

  public static boolean isShowStroke() {
    return getPrefs().getBoolean("toggle_stroke", true);
  }

  public static String getAppTheme() {
    return getPrefs().getString("app_theme", "3");
  }

  public static SharedPreferences getPrefs() {
    return PreferenceManager.getDefaultSharedPreferences(LayoutEditor.getContext());
  }
}
