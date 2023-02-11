package com.itsvks.layouteditor.editor.palette.layouts;

import android.widget.TableRow;
import android.content.Context;
import android.graphics.Canvas;
import com.itsvks.layouteditor.utils.Utils;

public class TableRowDesign extends TableRow {
  
  private boolean drawStrokeEnabled;

  public TableRowDesign(Context context) {
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
