package com.itsvks.layouteditor.editor.palette.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.Button;

import androidx.appcompat.content.res.AppCompatResources;

import com.itsvks.layouteditor.R;

@SuppressLint("AppCompatCustomView")
public class ButtonDesign extends Button {
  private Drawable strokeDrawable;
  private boolean drawStrokeEnabled;

  public ButtonDesign(Context context) {
    super(context);

    strokeDrawable = AppCompatResources.getDrawable(context, R.drawable.background_stroke_dash);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);

    if (drawStrokeEnabled) strokeDrawable.draw(canvas);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    strokeDrawable.setBounds(0, 0, w, h);
  }

  public void setStrokeEnabled(boolean enabled) {
    drawStrokeEnabled = enabled;
    invalidate();
  }
}
