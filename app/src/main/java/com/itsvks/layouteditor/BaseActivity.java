package com.itsvks.layouteditor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.elevation.SurfaceColors;
import java.lang.ref.WeakReference;

// import com.itsaky.androidide.logsender.LogSender;

public class BaseActivity extends AppCompatActivity {

  public LayoutEditor app;
  private static BaseActivity sIstance;
  private WeakReference<Context> ctx;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // LogSender.startLogging(this);
    super.onCreate(savedInstanceState);
    sIstance = this;
    ctx = new WeakReference<>(this);
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(ctx));
    app = LayoutEditor.getInstance();
    getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
  }
    
  public static BaseActivity getInstance() {
      return sIstance;
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

  @Override
  protected void onDestroy() {
    ctx.clear();
    super.onDestroy();
  }
}
