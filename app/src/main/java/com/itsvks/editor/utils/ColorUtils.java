package com.itsvks.editor.utils;

import android.content.Context;
import com.google.android.material.color.MaterialColors;

public class ColorUtils {
  
  public static int getOnSurfaceColor(Context context) {
    return MaterialColors.getColor(context, com.google.android.material.R.attr.colorOnSurface, 0);
  }

  public static int getSurfaceColor(Context context) {
    return MaterialColors.getColor(context, com.google.android.material.R.attr.colorSurface, 0);
  }
  
  public static int getOnPrimaryContainerColor(Context context) {
    return MaterialColors.getColor(context, com.google.android.material.R.attr.colorOnPrimaryContainer, 0);
  }
}
