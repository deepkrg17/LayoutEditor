package com.itsvks.layouteditor.editor.palette.buttons;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.RadioGroup;
import com.itsvks.layouteditor.utils.Utils;

public class RadioGroupDesign extends RadioGroup {

  private boolean drawStrokeEnabled;

  public RadioGroupDesign(Context context) {
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
