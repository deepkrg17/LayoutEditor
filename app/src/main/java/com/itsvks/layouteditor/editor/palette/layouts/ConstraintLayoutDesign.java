package com.itsvks.layouteditor.editor.palette.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.itsvks.layouteditor.utils.Utils;
import com.itsvks.layouteditor.utils.Constants;

public class ConstraintLayoutDesign extends ConstraintLayout {
  private boolean drawStrokeEnabled;
  private boolean isBlueprint;

  private Paint linePaint;
  private Paint fillPaint;

  private final int PARENT_ID = ConstraintLayout.LayoutParams.PARENT_ID;

  public ConstraintLayoutDesign(Context context) {
    super(context);

    linePaint = new Paint();
    linePaint.setColor(Color.LTGRAY);
    linePaint.setStrokeWidth(2);
    linePaint.setAntiAlias(true);
    linePaint.setStyle(Paint.Style.STROKE);

    fillPaint = new Paint();
    fillPaint.setColor(Color.LTGRAY);
    fillPaint.setAntiAlias(true);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
//    if (drawStrokeEnabled) {
//      drawBindings(canvas);
//    }

    super.dispatchDraw(canvas);

    if (drawStrokeEnabled)
      Utils.drawDashPathStroke(
          this, canvas, isBlueprint ? Constants.BLUEPRINT_DASH_COLOR : Constants.DESIGN_DASH_COLOR);
  }

  @Override
  public void draw(Canvas canvas) {
    if (isBlueprint) Utils.drawDashPathStroke(this, canvas, Constants.BLUEPRINT_DASH_COLOR);
    else super.draw(canvas);
  }

  public void setBlueprint(boolean isBlueprint) {
    this.isBlueprint = isBlueprint;
    invalidate();
  }

  public void setStrokeEnabled(boolean enabled) {
    drawStrokeEnabled = enabled;
    invalidate();
  }
}
