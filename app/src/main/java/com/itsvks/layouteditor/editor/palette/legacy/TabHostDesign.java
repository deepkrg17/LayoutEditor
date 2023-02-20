package com.itsvks.layouteditor.editor.palette.legacy;

import android.widget.TabHost;
import android.content.Context;
import android.graphics.Canvas;
import com.itsvks.layouteditor.utils.Utils;

@SuppressWarnings("deprecation")
public class TabHostDesign extends TabHost {
 
  private boolean drawStrokeEnabled;

  public TabHostDesign(Context context) {
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
