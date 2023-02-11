package com.itsvks.layouteditor.editor.palette.containers;

import com.google.android.material.navigation.NavigationView;
import android.content.Context;
import android.graphics.Canvas;
import com.itsvks.layouteditor.utils.Utils;

public class NavigationViewDesign extends NavigationView {
  
  private boolean drawStrokeEnabled;

  public NavigationViewDesign(Context context) {
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
