package com.itsvks.layouteditor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.elevation.SurfaceColors;
import com.itsvks.layouteditor.LayoutEditor;
import com.itsvks.layouteditor.managers.PreferencesManager;
// import com.itsaky.androidide.logsender.LogSender;

public class BaseActivity extends AppCompatActivity {

  public LayoutEditor app;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // LogSender.startLogging(this);
    super.onCreate(savedInstanceState);
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    app = LayoutEditor.getInstance();
    getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
    /*
        switch (PreferencesManager.getAppTheme()) {
          case "1":
            app.updateTheme(AppCompatDelegate.MODE_NIGHT_NO, this);
            break;
          case "2":
            app.updateTheme(AppCompatDelegate.MODE_NIGHT_YES, this);
            break;
          case "3":
            if (LayoutEditor.isAtLeastQ()) {
              app.updateTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, this);
            } else {
              app.updateTheme(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, this);
            }
            break;
        }
    */
  }

  public void openUrl(String url) {
    try {
      Intent open = new Intent(Intent.ACTION_VIEW);
      open.setData(Uri.parse(url));
      open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(open);
    } catch (Throwable th) {
      Toast.makeText(this, th.getMessage(), Toast.LENGTH_SHORT).show();
      th.printStackTrace();
    }
  }
}
