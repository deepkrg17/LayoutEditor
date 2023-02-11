package com.itsvks.layouteditor.editor.palette.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.widget.EditText;
import com.itsvks.layouteditor.utils.Utils;

@SuppressLint("AppCompatCustomView")
public class EditTextDesign extends EditText {

  private boolean drawStrokeEnabled;

  public EditTextDesign(Context context) {
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
