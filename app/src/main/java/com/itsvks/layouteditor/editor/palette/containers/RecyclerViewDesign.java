package com.itsvks.layouteditor.editor.palette.containers;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.graphics.Canvas;
import com.itsvks.layouteditor.utils.Utils;

public class RecyclerViewDesign extends RecyclerView {
  
  private boolean drawStrokeEnabled;

  public RecyclerViewDesign(Context context) {
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
