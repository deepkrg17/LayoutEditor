package com.itsvks.layouteditor.editor.palette.text;

import android.widget.CheckedTextView;
import android.content.Context;
import android.graphics.Canvas;
import com.itsvks.layouteditor.utils.Utils;

public class CheckedTextViewDesign extends CheckedTextView {
 
  private boolean drawStrokeEnabled;

  public CheckedTextViewDesign(Context context) {
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
