package com.itsvks.editor.palette.containers;

import com.google.android.material.appbar.MaterialToolbar;
import android.content.Context;
import android.graphics.Canvas;
import com.itsvks.editor.Constants;
import com.itsvks.editor.utils.DrawUtils;

public class MaterialToolbarDesign extends MaterialToolbar {
  
  private boolean drawStrokeEnabled;
  private boolean isBlueprint;

  public MaterialToolbarDesign(Context context) {
    super(context);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);

    if (drawStrokeEnabled)
      DrawUtils.drawDashPathStroke(
          this, canvas, isBlueprint ? Constants.BLUEPRINT_DASH_COLOR : Constants.DESIGN_DASH_COLOR);
  }

  public void setStrokeEnabled(boolean enabled) {
    drawStrokeEnabled = enabled;
    invalidate();
  }
  
  @Override
  public void draw(Canvas canvas) {
    if (isBlueprint) DrawUtils.drawDashPathStroke(this, canvas, Constants.BLUEPRINT_DASH_COLOR);
    else super.draw(canvas);
  }

  public void setBlueprint(boolean isBlueprint) {
    this.isBlueprint = isBlueprint;
    invalidate();
  }
}
