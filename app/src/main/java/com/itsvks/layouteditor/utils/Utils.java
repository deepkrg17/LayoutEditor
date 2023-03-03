package com.itsvks.layouteditor.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.color.MaterialColors;
import com.itsvks.layouteditor.R.string;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;

public class Utils {
  /** This method is used to convert the input into the equivalent dip value. */
  public static int getDip(Context context, int input) {
    return (int)
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, input, context.getResources().getDisplayMetrics());
  }

  public static boolean isDarkMode(Context context) {
    int uiMode =
        context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    return uiMode == Configuration.UI_MODE_NIGHT_YES;
  }

  public static int getOnSurfaceColor(View view) {
    return MaterialColors.getColor(view, com.google.android.material.R.attr.colorOnSurface);
  }
  
  public static int getSurfaceColor(View view) {
    return MaterialColors.getColor(view, com.google.android.material.R.attr.colorSurface);
  }

  public static int getOnSurfaceColor(ViewGroup viewGroup) {
    return viewGroup instanceof View ? getOnSurfaceColor((View) viewGroup) : Color.WHITE;
  }

  public static void drawDashPathStroke(View view, Canvas canvas, Paint paint) {
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.STROKE);
    paint.setPathEffect(new DashPathEffect(new float[] {10, 7}, 0));
    canvas.drawRect(0, 0, view.getWidth(), view.getHeight(), paint);
  }

  public static void drawDashPathStroke(View view, Canvas canvas) {
    drawDashPathStroke(view, canvas, getDefaultPaint(view));
  }

  public static void drawDashPathStroke(View view, Canvas canvas, int paintColor) {
    Paint paint = getDefaultPaint(view);
    paint.setColor(paintColor);
    drawDashPathStroke(view, canvas, paint);
  }

  private static Paint getDefaultPaint(View view) {
    Paint paint = new Paint();
    paint.setColor(isDarkMode(view.getContext()) ? Color.WHITE : getOnSurfaceColor(view));
    paint.setStrokeWidth(getDip(view.getContext(), 2));
    return paint;
  }

  public static boolean saveBitmapAsImageToGallery(Context context, Bitmap bitmap, String title) {
    String savedImageURL = null;

    // Get the directory for the user's public pictures directory.
    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    // Create a new directory for your app
    File appDir = new File(path, context.getString(string.app_name));
    if (!appDir.exists()) {
      appDir.mkdir();
    }

    // Generate a unique file name for your image
    String fileName = "IMG_".concat(context.getString(string.app_name)) + " " + title + new Date().getTime() + ".jpg";
    fileName = fileName.replaceAll(" ", "_").toLowerCase(Locale.getDefault());
    File file = new File(appDir, fileName);

    // Save the image to the directory
    try {
      OutputStream fOut = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
      fOut.flush();
      fOut.close();
    } catch (IOException e) {
      e.printStackTrace();
      Log.d("MediaUtils", "Error saving image to gallery: " + e.getMessage());
      return false;
    }

    // Add the image to the Android gallery
    try {
      ContentResolver contentResolver = context.getContentResolver();
      ContentValues values = new ContentValues();
      values.put(MediaStore.Images.Media.TITLE, fileName);
      values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
      values.put(
          MediaStore.Images.Media.DESCRIPTION,
          "Image saved from ".concat(context.getString(string.app_name)));
      values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
      values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
      values.put(MediaStore.Images.Media.ORIENTATION, 0);
      values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());

      Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
      savedImageURL = uri.toString();

      Log.d("MediaUtils", "Image saved to gallery: " + savedImageURL);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("MediaUtils", "Error saving image to gallery: " + e.getMessage());
      return false;
    }
  }
}
