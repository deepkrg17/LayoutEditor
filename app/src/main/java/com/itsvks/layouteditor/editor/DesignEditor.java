package com.itsvks.layouteditor.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.itsvks.layouteditor.editor.DeviceConfiguration;
import com.itsvks.layouteditor.utils.BitmapUtil;
import com.itsvks.layouteditor.utils.Utils;

public class DesignEditor extends ViewGroup {
  private ViewType viewType;
  private String layoutFile;
  private DeviceConfiguration deviceConfiguration;
  private APILevel apiLevel;
  private Paint paint;

  public DesignEditor(Context context) {
    super(context);
    init(context, null);
  }

  public DesignEditor(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public DesignEditor(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    viewType = ViewType.DESIGN;
    deviceConfiguration = new DeviceConfiguration(DeviceConfiguration.DeviceSize.Medium);
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.STROKE);
    paint.setPathEffect(new DashPathEffect(new float[] {10, 7}, 0));
    paint.setStrokeWidth(Utils.getDip(getContext(), 3));
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);
    switch (viewType) {
      case BLUEPRINT:
        drawBlueprint(canvas);
        break;
      case DESIGN:
        drawDesign(canvas);
        break;
      default:
        drawDesign(canvas);
        break;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return super.onTouchEvent(event);
  }
  
  @Override
  protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {}

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    return super.onInterceptTouchEvent(event);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
  }

  private void drawBlueprint(Canvas canvas) {
    paint.setColor(Utils.isDarkMode(getContext()) ? Color.WHITE : Utils.getOnSurfaceColor(this));
    setBackgroundColor(Color.parseColor("#235C6F"));
    canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    for (int i = 0; i < getChildCount(); i++) {
      View view = getChildAt(i);
      canvas.drawRect(0, 0, view.getWidth(), view.getHeight(), paint);
    }
  }

  private void drawDesign(Canvas canvas) {
    paint.setColor(Utils.isDarkMode(getContext()) ? Color.parseColor("#1689F6") : Color.parseColor("#6495ED"));
    setBackgroundColor(Utils.isDarkMode(getContext()) ? Color.WHITE : Utils.getOnSurfaceColor(this));
    canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    for (int i = 0; i < getChildCount(); i++) {
      View view = getChildAt(i);
      canvas.drawRect(0, 0, view.getWidth(), view.getHeight(), paint);
    }
  }

  public void setViewType(ViewType viewType) {
    this.viewType = viewType;
  }

  public void setLayoutFile(String layoutFile) {
    this.layoutFile = layoutFile;
  }

  public void previewLayout(DeviceConfiguration deviceConfiguration, APILevel apiLevel) {
    this.deviceConfiguration = deviceConfiguration;
    this.apiLevel = apiLevel;
  }

  public void resizeLayout(DeviceConfiguration deviceConfiguration) {
    this.deviceConfiguration = deviceConfiguration;
  }

  public void saveLayout() {
    // Save the layout to disk
  }

  public enum ViewType {
    DESIGN,
    BLUEPRINT
  }
}
