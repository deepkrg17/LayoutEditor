package com.itsvks.layouteditor.editor.palette.containers;

import com.google.android.material.appbar.AppBarLayout;
import android.content.Context;
import android.graphics.Canvas;
import com.itsvks.layouteditor.utils.Utils;

public class AppBarLayoutDesign extends AppBarLayout {

  private boolean drawStrokeEnabled;

  public AppBarLayoutDesign(Context context) {
    super(context);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);

    if (drawStrokeEnabled) Utils.drawDashPathStroke(this, canvas);
  }

  public void setStrokeEnabled(boolean enabled) {
    drawStrokeEnabled = enabled;
    invalidate();
  }
}
