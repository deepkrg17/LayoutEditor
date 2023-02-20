package com.itsvks.layouteditor.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.color.MaterialColors;

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
}
