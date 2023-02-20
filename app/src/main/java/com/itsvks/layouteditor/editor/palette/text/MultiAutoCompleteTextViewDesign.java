package com.itsvks.layouteditor.editor.palette.text;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.MultiAutoCompleteTextView;
import com.itsvks.layouteditor.utils.Utils;

public class MultiAutoCompleteTextViewDesign extends MultiAutoCompleteTextView {
 
  private boolean drawStrokeEnabled;

  public MultiAutoCompleteTextViewDesign(Context context) {
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
