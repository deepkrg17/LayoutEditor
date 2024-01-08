package com.itsvks.layouteditor.managers

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.itsvks.layouteditor.LayoutEditor.Companion.instance

object PreferencesManager {
  @JvmStatic
  val isEnableVibration: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_VIBRATION, false)
  @JvmStatic
  val isShowStroke: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_TOGGLE_STROKE, true)
  @JvmStatic
  val isApplyDynamicColors: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_DYNAMIC_COLORS, false)
  @JvmStatic
  val currentTheme: Int
    get() = prefs.getInt(SharedPreferencesKeys.KEY_CHOOSE_THEME, 0)
  @JvmStatic
  val prefs: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(instance!!.context)
}
