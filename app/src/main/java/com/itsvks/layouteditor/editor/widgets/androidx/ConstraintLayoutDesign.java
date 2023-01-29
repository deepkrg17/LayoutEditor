package com.itsvks.layouteditor.editor.widgets.androidx;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import androidx.constraintlayout.widget.Constraints;
import androidx.core.content.res.ResourcesCompat;
import com.itsvks.layouteditor.R;
import java.util.List;

public class ConstraintLayoutDesign extends ConstraintLayout {

  private Drawable strokeDrawable;
  private boolean drawStrokeEnabled;
  private Paint linePaint;
  private Paint fillPaint;
  private final int PARENT_ID;

  public ConstraintLayoutDesign(Context context, AttributeSet attrs, int defStyleAttribute) {
    super(context, attrs, defStyleAttribute);
    this.strokeDrawable =
        ResourcesCompat.getDrawable(
            context.getResources(), R.drawable.background_stroke_dash, null);
    this.linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    this.fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    this.PARENT_ID = getId();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    if (strokeDrawable != null) {
      strokeDrawable.setBounds(0, 0, getWidth(), getHeight());
    }
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);
    if (drawStrokeEnabled) {
      drawBindings(canvas);
      strokeDrawable.draw(canvas);
    }
  }

  public void setStrokeEnabled(boolean enabled) {
    this.drawStrokeEnabled = enabled;
    invalidate();
  }

  private void drawBindings(Canvas canvas) {
    drawHorizontalArrow(canvas, PARENT_ID, ConstraintSet.START);
    drawHorizontalArrow(canvas, PARENT_ID, ConstraintSet.END);
    drawVerticalArrow(canvas, PARENT_ID, ConstraintSet.TOP);
    drawVerticalArrow(canvas, PARENT_ID, ConstraintSet.BOTTOM);
    drawBind(canvas);
  }

  private void drawHorizontalArrow(Canvas canvas, int parentId, int orientation) {
    // drawing horizontal arrows
    int[] position = new int[2];
    getLocationOnScreen(position);
    float startX = 0;
    float startY = position[1] + getHeight() / 2;
    float endX = position[0] + getWidth();
    float endY = position[1] + getHeight() / 2;
    float midX = (startX + endX) / 2;
    float midY = (startY + endY) / 2;
    float arrowLength = 20;
    float arrowAngle = (float) (Math.PI / 8);
    float arrowX = midX + arrowLength * (float) Math.cos(arrowAngle);
    float arrowY = midY + arrowLength * (float) Math.sin(arrowAngle);

    Path path = new Path();
    path.moveTo(startX, startY);
    path.lineTo(midX, midY);
    path.lineTo(arrowX, arrowY);
    path.lineTo(endX, endY);
    path.lineTo(midX, midY);
    path.lineTo(arrowX, midY - (arrowY - midY));
    canvas.drawPath(path, linePaint);
  }

  private void drawVerticalArrow(Canvas canvas, int parentId, int orientation) {
    // drawing vertical arrows
    int[] position = new int[2];
    getLocationOnScreen(position);
    float startX = position[0] + getWidth() / 2;
    float startY = 0;
    float endX = position[0] + getWidth() / 2;
    float endY = position[1] + getHeight();
    float midX = (startX + endX) / 2;
    float midY = (startY + endY) / 2;
    float arrowLength = 20;
    float arrowAngle = (float) (Math.PI / 8);
    float arrowX = midX + arrowLength * (float) Math.cos(arrowAngle);
    float arrowY = midY + arrowLength * (float) Math.sin(arrowAngle);

    Path path = new Path();
    path.moveTo(startX, startY);
    path.lineTo(midX, midY);
    path.lineTo(arrowX, arrowY);
    path.lineTo(endX, endY);
    path.lineTo(midX, midY);
    path.lineTo(midX - (arrowX - midX), arrowY);
    canvas.drawPath(path, linePaint);
  }

  private void drawBind(Canvas canvas) {
    // drawing lines and paths
    
  }
}
