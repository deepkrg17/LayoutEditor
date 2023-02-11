package com.itsvks.layouteditor.editor.palette.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.LinearLayout;
import com.itsvks.layouteditor.utils.Utils;

public class LinearLayoutDesign extends LinearLayout {

  private boolean drawStrokeEnabled;

  public LinearLayoutDesign(Context context) {
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
