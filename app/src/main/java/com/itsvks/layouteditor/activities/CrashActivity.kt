package com.itsvks.layouteditor.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.DeviceUtils
import com.google.android.material.snackbar.Snackbar
import com.itsvks.layouteditor.BaseActivity
import com.itsvks.layouteditor.BuildConfig
import com.itsvks.layouteditor.R
import com.itsvks.layouteditor.databinding.ActivityCrashBinding

class CrashActivity : BaseActivity() {
  private var binding: ActivityCrashBinding? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityCrashBinding.inflate(layoutInflater)

    setContentView(binding!!.getRoot())
    setSupportActionBar(binding!!.topAppBar)
    supportActionBar!!.setTitle(R.string.app_crashed)

    val error = """
        Manufacturer: ${DeviceUtils.getManufacturer()}
        Device: ${DeviceUtils.getModel()}
        ${intent.getStringExtra("Software")}
        App version: ${BuildConfig.VERSION_NAME}
        
        ${intent.getStringExtra("Error")}
        
        ${intent.getStringExtra("Date")}
        """.trimIndent()

    binding!!.result.text = error

    binding!!.fab.setOnClickListener { v: View? ->
      ClipboardUtils.copyText(binding!!.result.text)
      Snackbar.make(binding!!.getRoot(), getString(R.string.copied), Snackbar.LENGTH_SHORT)
        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
        .setAnchorView(binding!!.fab)
        .show()
    }
  }

  override fun onBackPressed() {
    finishAffinity()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val close = menu.add(getString(R.string.close))
    close.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
    close.setIcon(AppCompatResources.getDrawable(this, R.drawable.close))
    close.setContentDescription(getString(R.string.close_app))

    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.title == getString(R.string.close)) {
      finishAffinity()
      return true
    }
    return false
  }

  override fun onDestroy() {
    super.onDestroy()
    binding = null
  }
}
