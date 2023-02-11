package com.itsvks.layouteditor.editor.palette.buttons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.widget.Switch;
import com.itsvks.layouteditor.utils.Utils;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class SwitchDesign extends Switch {

  private boolean drawStrokeEnabled;

  public SwitchDesign(Context context) {
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
