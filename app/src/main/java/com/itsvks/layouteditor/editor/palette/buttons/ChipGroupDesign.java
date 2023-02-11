package com.itsvks.layouteditor.editor.palette.buttons;

import com.google.android.material.chip.ChipGroup;
import android.content.Context;
import android.graphics.Canvas;
import com.itsvks.layouteditor.utils.Utils;

public class ChipGroupDesign extends ChipGroup {

  private boolean drawStrokeEnabled;

  public ChipGroupDesign(Context context) {
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
