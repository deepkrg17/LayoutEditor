package com.itsvks.layouteditor.editor.palette.widgets;

import android.widget.ImageView;
import android.content.Context;
import android.graphics.Canvas;
import com.itsvks.layouteditor.utils.Utils;

public class ImageViewDesign extends ImageView {
  
  private boolean drawStrokeEnabled;

  public ImageViewDesign(Context context) {
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
