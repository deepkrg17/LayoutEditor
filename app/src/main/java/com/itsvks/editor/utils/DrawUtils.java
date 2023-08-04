package com.itsvks.editor.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.view.View;
import com.blankj.utilcode.util.SizeUtils;

public class DrawUtils {
  public static void drawDashPathStroke(View view, Canvas canvas, Paint paint) {
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.STROKE);
    paint.setPathEffect(new DashPathEffect(new float[] {10, 7}, 0));
    canvas.drawRect(0, 0, view.getWidth(), view.getHeight(), paint);
  }

  public static void drawDashPathStroke(View view, Canvas canvas) {
    drawDashPathStroke(view, canvas, getDefaultPaint(view.getContext()));
  }

  public static void drawDashPathStroke(View view, Canvas canvas, int paintColor) {
    Paint paint = getDefaultPaint(view.getContext());
    paint.setColor(paintColor);
    drawDashPathStroke(view, canvas, paint);
  }

  private static Paint getDefaultPaint(Context context) {
    Paint paint = new Paint();
    paint.setColor(Utils.isDarkMode(context) ? Color.WHITE : ColorUtils.getOnSurfaceColor(context));
    paint.setStrokeWidth(SizeUtils.px2dp(2));
    return paint;
  }
}
