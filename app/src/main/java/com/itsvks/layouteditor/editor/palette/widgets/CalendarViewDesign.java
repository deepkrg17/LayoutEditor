package com.itsvks.layouteditor.editor.palette.widgets;

import android.widget.CalendarView;
import android.content.Context;
import android.graphics.Canvas;
import com.itsvks.layouteditor.utils.Utils;

public class CalendarViewDesign extends CalendarView {
 
  private boolean drawStrokeEnabled;

  public CalendarViewDesign(Context context) {
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
