package com.itsvks.editor.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.view.View;
import androidx.drawerlayout.widget.DrawerLayout;
import com.itsvks.editor.vectormaster.VectorMasterDrawable;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Utils {
  public static boolean isDarkMode(Context context) {
    int uiMode =
        context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    return uiMode == Configuration.UI_MODE_NIGHT_YES;
  }

  public static void animateNavigationDrawer(DrawerLayout drawerLayout, View contentView) {
    drawerLayout.addDrawerListener(
        new DrawerLayout.SimpleDrawerListener() {
          @Override
          public void onDrawerSlide(View drawerView, float slideOffset) {
            // Calculate the slide range based on the width of the drawer
            float slideRange = drawerView.getWidth() * slideOffset;

            // Translate the main content view horizontally based on the slide range
            contentView.setTranslationX(slideRange);

            // Scale down the main content view based on the slide offset
            float scaleFactor = 1 - slideOffset * 0.2f;
            contentView.setScaleX(scaleFactor);
            contentView.setScaleY(scaleFactor);

            // Fade out the main content view based on the slide offset
            float alpha = 1 - slideOffset;
            contentView.setAlpha(alpha);
          }
        });
  }
  
  public static VectorMasterDrawable getVectorDrawableAsync(Context context, Uri uri) {
    Callable<VectorMasterDrawable> callable =
        new Callable<VectorMasterDrawable>() {
          @Override
          public VectorMasterDrawable call() throws Exception {
            // Load the drawable from file
            InputStream is = context.getContentResolver().openInputStream(uri);
            VectorMasterDrawable drawable = new VectorMasterDrawable(context);
            drawable.setInputStream(is);
            is.close();
            return drawable;
          }
        };

    FutureTask<VectorMasterDrawable> futureTask = new FutureTask<>(callable);
    new Thread(futureTask).start();

    try {
      VectorMasterDrawable drawable = futureTask.get();
      return drawable;
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
      return null;
    }
  }
}
