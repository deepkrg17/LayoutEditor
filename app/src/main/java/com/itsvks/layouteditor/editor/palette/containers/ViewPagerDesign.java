package com.itsvks.layouteditor.editor.palette.containers;

import androidx.viewpager.widget.ViewPager;
import android.content.Context;
import android.graphics.Canvas;
import com.itsvks.layouteditor.utils.Utils;

public class ViewPagerDesign extends ViewPager {
  
  private boolean drawStrokeEnabled;

  public ViewPagerDesign(Context context) {
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
