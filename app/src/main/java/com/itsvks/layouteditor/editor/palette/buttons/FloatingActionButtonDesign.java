package com.itsvks.layouteditor.editor.palette.buttons;

import android.content.Context;
import android.graphics.Canvas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itsvks.layouteditor.utils.Utils;

public class FloatingActionButtonDesign extends FloatingActionButton {

  private boolean drawStrokeEnabled;

  public FloatingActionButtonDesign(Context context) {
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
