package com.itsvks.layouteditor.editor.palette.containers;

import androidx.core.widget.NestedScrollView;
import android.content.Context;
import android.graphics.Canvas;
import com.itsvks.layouteditor.utils.Utils;

public class NestedScrollViewDesign extends NestedScrollView {

  private boolean drawStrokeEnabled;

  public NestedScrollViewDesign(Context context) {
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
