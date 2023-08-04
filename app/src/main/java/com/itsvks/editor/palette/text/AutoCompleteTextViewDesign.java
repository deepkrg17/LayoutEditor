package com.itsvks.editor.palette.text;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.AutoCompleteTextView;
import com.itsvks.editor.Constants;
import com.itsvks.editor.utils.DrawUtils;

public class AutoCompleteTextViewDesign extends AutoCompleteTextView {
  
  private boolean drawStrokeEnabled;
  private boolean isBlueprint;

  public AutoCompleteTextViewDesign(Context context) {
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
