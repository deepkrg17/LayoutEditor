package com.itsvks.editor.activities;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.elevation.SurfaceColors;
import com.itsvks.editor.crash.CrashHandler;

public class BaseActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    
    getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
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
