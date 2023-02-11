package com.itsvks.layouteditor.editor.palette.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.widget.SeekBar;
import com.itsvks.layouteditor.utils.Utils;

@SuppressLint("AppCompatCustomView")
public class SeekBarDesign extends SeekBar {

  private boolean drawStrokeEnabled;

  public SeekBarDesign(Context context) {
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
