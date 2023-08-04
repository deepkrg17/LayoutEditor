package com.itsvks.editor.utils;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import androidx.core.graphics.drawable.DrawableCompat;

public class TintingUtils {
  private TintingUtils() {
    // Private constructor to prevent instantiation
  }

  public static void tintMenuIcon(MenuItem item, int color) {
    if (item != null) {
      Drawable drawable = item.getIcon();
      if (drawable != null) {
        drawable = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        item.setIcon(drawable);
      }
    }
  }

  public static void tintDrawable(Drawable drawable, int color) {
    if (drawable != null) {
      drawable = DrawableCompat.wrap(drawable.mutate());
      DrawableCompat.setTint(drawable, color);
      DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
    }
  }

}
