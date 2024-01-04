package com.itsvks.layouteditor

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import com.itsvks.layouteditor.managers.SharedPreferenceManager

class LayoutEditor : Application() {
  override fun onCreate() {
    super.onCreate()
    instance = this
    SharedPreferenceManager.setContext(this)
    AppCompatDelegate.setDefaultNightMode(
      SharedPreferenceManager.loadPrefInt(
        getString(R.string.choose_theme),
        -1
      )
    )
    if (SharedPreferenceManager.loadPrefBool(
        R.string.dynamic_colors,
        false
      ) && isAtLeastS && DynamicColors.isDynamicColorAvailable()
    ) {
      DynamicColors.applyToActivitiesIfAvailable(this)
    }
  }

  val context: Context
    get() = instance!!.applicationContext
  val isAtLeastS: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
  val isAtLeastTiramisu: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
  val isAtLeastQ: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

  fun updateTheme(nightMode: Int, activity: Activity) {
    AppCompatDelegate.setDefaultNightMode(nightMode)
    activity.recreate()
  }

  companion object {
    var instance: LayoutEditor? = null
      private set
  }
}
