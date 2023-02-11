package com.itsvks.layouteditor.editor;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itsvks.layouteditor.editor.initializer.AttributeInitializer;
import com.itsvks.layouteditor.editor.initializer.AttributeMap;
import com.itsvks.layouteditor.utils.BitmapUtil;
import com.itsvks.layouteditor.utils.Constants;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.Utils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesignEditor extends LinearLayout {
  private ViewType viewType;
  private String layoutFile;
  private DeviceConfiguration deviceConfiguration;
  private APILevel apiLevel;
  private Paint paint;
  private HashMap<View, AttributeMap> VIEW_ATTRIBUTE_MAP;
  private HashMap<String, List<HashMap<String, Object>>> ATTRIBUTES;
  private HashMap<String, List<HashMap<String, Object>>> PARENT_ATTRIBUTES;
  private AttributeInitializer INITIALIZER;
  private boolean isBlueprint;

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
    viewType = ViewType.BLUEPRINT;
    isBlueprint = viewType == ViewType.BLUEPRINT;
    deviceConfiguration = new DeviceConfiguration(DeviceSize.LARGE);
    VIEW_ATTRIBUTE_MAP = new HashMap<>();

    paint = new Paint();
    paint.setStrokeWidth(Utils.getDip(getContext(), 3));

    setOrientation(VERTICAL);
    setTransition();
    initAttributes();

    INITIALIZER =
        new AttributeInitializer(context, VIEW_ATTRIBUTE_MAP, ATTRIBUTES, PARENT_ATTRIBUTES);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);
    switch (viewType) {
      case BLUEPRINT:
        drawBlueprint(canvas);
        break;
      case DESIGN:
      default:
        drawDesign(canvas);
        break;
    }
    switch (deviceConfiguration.getSize()) {
      case SMALL:
        setScaleX(0.75f);
        setScaleY(0.75f);
        break;
      case MEDIUM:
      default:
        setScaleX(0.85f);
        setScaleY(0.85f);
        break;
      case LARGE:
        setScaleX(0.95f);
        setScaleY(0.95f);
        break;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return super.onTouchEvent(event);
  }

  //  @Override
  //  protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {}

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    return super.onInterceptTouchEvent(event);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
  }

  private void drawBlueprint(Canvas canvas) {
    paint.setColor(Constants.BLUEPRINT_DASH_COLOR);
    setBackgroundColor(Constants.BLUEPRINT_BACKGROUND_COLOR);
    Utils.drawDashPathStroke(this, canvas, paint);
  }

  private void drawDesign(Canvas canvas) {
    paint.setColor(Constants.DESIGN_DASH_COLOR);
    canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
  }

  public void setViewType(ViewType viewType) {
    this.viewType = viewType;
    isBlueprint = viewType == ViewType.BLUEPRINT;
    invalidate();
  }

  public void setLayoutFile(String layoutFile) {
    this.layoutFile = layoutFile;
    invalidate();
  }

  public void previewLayout(DeviceConfiguration deviceConfiguration, APILevel apiLevel) {
    this.deviceConfiguration = deviceConfiguration;
    this.apiLevel = apiLevel;
  }

  public void resizeLayout(DeviceConfiguration deviceConfiguration) {
    this.deviceConfiguration = deviceConfiguration;
    invalidate();
  }

  public void saveLayout() {
    // Save the layout to disk
  }

  private void setTransition() {
    LayoutTransition transition = new LayoutTransition();
    transition.disableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
    transition.enableTransitionType(LayoutTransition.CHANGING);
    transition.setDuration(150);
    setLayoutTransition(transition);
  }

  @SuppressWarnings("unchecked")
  private void setBlueprintOnChilds() {
    try {
      for (View view : VIEW_ATTRIBUTE_MAP.keySet()) {
        Class cls = view.getClass();
        Method method = cls.getMethod("setBlueprint", boolean.class);
        method.invoke(view, isBlueprint);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setDragListener() {
    setOnDragListener(
        new OnDragListener() {
          @Override
          public boolean onDrag(View host, DragEvent event) {
            ViewGroup parent = (ViewGroup) host;
            View draggedView =
                event.getLocalState() instanceof View ? (View) event.getLocalState() : null;
            
            switch (event.getAction()) {
              case DragEvent.ACTION_DRAG_STARTED:
            }
            return false;
          }
        });
  }
  

  private void initAttributes() {
    ATTRIBUTES = convertJsonToJavaObject(Constants.ATTRIBUTES_FILE);
    PARENT_ATTRIBUTES = convertJsonToJavaObject(Constants.PARENT_ATTRIBUTES_FILE);
  }

  private HashMap<String, List<HashMap<String, Object>>> convertJsonToJavaObject(String filePath) {
    return new Gson()
        .fromJson(
            FileUtil.readFromAsset(filePath, getContext()),
            new TypeToken<HashMap<String, ArrayList<HashMap<String, Object>>>>() {}.getType());
  }

  public enum ViewType {
    DESIGN,
    BLUEPRINT
  }

  public ViewType getViewType() {
    return this.viewType;
  }

  public DeviceConfiguration getDeviceConfiguration() {
    return this.deviceConfiguration;
  }

  public void setDeviceConfiguration(DeviceConfiguration deviceConfiguration) {
    this.deviceConfiguration = deviceConfiguration;
  }

  public APILevel getApiLevel() {
    return this.apiLevel;
  }

  public void setApiLevel(APILevel apiLevel) {
    this.apiLevel = apiLevel;
  }
}
