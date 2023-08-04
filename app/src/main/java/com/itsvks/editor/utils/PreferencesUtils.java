package com.itsvks.editor.utils;

import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.itsvks.editor.app.LayoutEditor;
import com.itsvks.editor.managers.SettingsManager;

public class PreferencesUtils {
  public static SharedPreferences getDefaultPrefs() {
    return PreferenceManager.getDefaultSharedPreferences(LayoutEditor.getInstance());
  }

  public static boolean isEnableVibration() {
    return getDefaultPrefs().getBoolean(SettingsManager.KEY_ENABLE_VIBRATION, false);
  }

  public static boolean isShowStroke() {
    return getDefaultPrefs().getBoolean(SettingsManager.KEY_SHOW_STROKE, true);
  }
}
