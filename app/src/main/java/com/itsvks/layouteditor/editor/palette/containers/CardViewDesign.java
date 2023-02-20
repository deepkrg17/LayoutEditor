package com.itsvks.layouteditor.editor.palette.containers;

import android.content.Context;
import android.graphics.Canvas;
import androidx.cardview.widget.CardView;
import com.itsvks.layouteditor.utils.Utils;

public class CardViewDesign extends CardView {

  private boolean drawStrokeEnabled;

  public CardViewDesign(Context context) {
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
