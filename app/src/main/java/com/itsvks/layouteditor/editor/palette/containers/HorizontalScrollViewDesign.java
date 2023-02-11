package com.itsvks.layouteditor.editor.palette.containers;

import android.widget.HorizontalScrollView;
import android.content.Context;
import android.graphics.Canvas;
import com.itsvks.layouteditor.utils.Utils;

public class HorizontalScrollViewDesign extends HorizontalScrollView {
  
  private boolean drawStrokeEnabled;

  public HorizontalScrollViewDesign(Context context) {
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
