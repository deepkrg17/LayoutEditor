package com.itsvks.layouteditor.editor.palette.containers;

import com.google.android.material.bottomappbar.BottomAppBar;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.appcompat.content.res.AppCompatResources;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.utils.Utils;

public class BottomAppBarDesign extends BottomAppBar {
  
  private boolean drawStrokeEnabled;

  public BottomAppBarDesign(Context context) {
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
