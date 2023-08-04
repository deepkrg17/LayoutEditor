package com.itsvks.editor.designeditor;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsvks.editor.databinding.ShowAttributesDialogBinding;
import com.itsvks.editor.listeners.AppliedAttributeClickListener;
import com.itsvks.editor.adapters.AppliedAttributesAdapter;
import com.itsvks.editor.managers.IdManager;
import android.widget.ArrayAdapter;
import com.google.android.material.color.MaterialColors;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itsvks.editor.Constants;
import com.itsvks.editor.designeditor.initializer.AttributeInitializer;
import com.itsvks.editor.designeditor.initializer.AttributeMap;
import com.itsvks.editor.managers.UndoRedoManager;
import com.itsvks.editor.parser.XmlLayoutParser;
import com.itsvks.editor.parser.XmlParser;
import com.itsvks.editor.tools.XmlLayoutGenerator;
import com.itsvks.editor.utils.ArgumentUtil;
import com.itsvks.editor.dialogs.AttributeDialog;
import com.itsvks.editor.dialogs.SizeDialog;
import com.itsvks.editor.dialogs.DimensionDialog;
import com.itsvks.editor.dialogs.IdDialog;
import com.itsvks.editor.dialogs.ViewDialog;
import com.itsvks.editor.dialogs.BooleanDialog;
import com.itsvks.editor.dialogs.StringDialog;
import com.itsvks.editor.dialogs.NumberDialog;
import com.itsvks.editor.dialogs.FlagDialog;
import com.itsvks.editor.dialogs.EnumDialog;
import com.itsvks.editor.dialogs.ColorDialog;
import com.itsvks.editor.utils.ColorUtils;
import com.itsvks.editor.utils.DrawUtils;
import com.itsvks.editor.utils.InvokeUtil;
import com.itsvks.editor.utils.PreferencesUtils;
import com.itsvks.editor.view.ComponentTree;
import com.itsvks.editor.R;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DesignEditor extends LinearLayout {
  private ViewType viewType;
  private DeviceSize deviceSize;
  private boolean blueprint;

  private File layoutFile;
  private Paint paint;

  private ComponentTree componentTree;
  private UndoRedoManager undoRedoManager;

  private HashMap<View, AttributeMap> viewAttributeMap;
  private HashMap<String, List<HashMap<String, Object>>> attributes;
  private HashMap<String, List<HashMap<String, Object>>> parentAttributes;
  private AttributeInitializer initializer;
  private DesignEditorHandler handler;

  private View shadow;

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
    initAttributes();
    initShadow(context);
    handler = new DesignEditorHandler(this);
    setViewType(ViewType.DESIGN);
    setBlueprint(viewType == ViewType.BLUEPRINT);
    setSize(DeviceSize.LARGE);
    initPaint();
    toggleStrokeWidgets();
    setBlueprintOnChilds();
    handler.setTransition(this);
    handler.setDragListener(this);
  }

  private void initPaint() {
    paint = new Paint();
    paint.setStrokeWidth(SizeUtils.px2dp(10));
  }

  private void initShadow(Context context) {
    shadow = new View(context);
    shadow.setBackgroundColor(
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorOutline));
    shadow.setLayoutParams(new ViewGroup.LayoutParams(SizeUtils.px2dp(200), SizeUtils.px2dp(140)));
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);
    drawBackgroundAndPath(canvas);
    setScale();
  }

  private void drawBackgroundAndPath(Canvas canvas) {
    if (getViewType() == ViewType.BLUEPRINT) {
      paint.setColor(Constants.BLUEPRINT_DASH_COLOR);
      setBackgroundColor(Constants.BLUEPRINT_BACKGROUND_COLOR);
    } else if (getViewType() == ViewType.DESIGN) {
      paint.setColor(Constants.DESIGN_DASH_COLOR);
      setBackgroundColor(ColorUtils.getSurfaceColor(getContext()));
    }
    DrawUtils.drawDashPathStroke(this, canvas, paint);
  }

  private void setScale() {
    float scaleX, scaleY;
    switch (getSize()) {
      case SMALL:
        scaleX = 0.75f;
        scaleY = 0.75f;
        break;
      case MEDIUM:
        scaleX = 0.85f;
        scaleY = 0.85f;
        break;
      case LARGE:
      default:
        scaleX = 0.95f;
        scaleY = 0.95f;
        break;
    }
    setScaleX(scaleX);
    setScaleY(scaleY);
  }

  private void toggleStrokeWidgets() {
    invokeMethodOnViews("setStrokeEnabled", PreferencesUtils.isShowStroke());
  }

  private void setBlueprintOnChilds() {
    invokeMethodOnViews("setBlueprint", isBlueprint());
  }

  @SuppressWarnings("unchecked")
  private void invokeMethodOnViews(String methodName, Object argument) {
    try {
      for (View view : viewAttributeMap.keySet()) {
        Class<?> cls = view.getClass();
        Method method = cls.getMethod(methodName, argument.getClass());
        method.invoke(view, argument);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ViewType getViewType() {
    return this.viewType;
  }

  public void setViewType(ViewType viewType) {
    this.viewType = viewType;
    setBlueprint(viewType == ViewType.BLUEPRINT);
    setBlueprintOnChilds();
    invalidate();
  }

  public void updateUndoRedoHistory() {
    if (undoRedoManager == null) return;
    String result = new XmlLayoutGenerator().generate(this, false);
    undoRedoManager.addToHistory(result);
  }

  public void addWidget(View view, ViewGroup newParent, DragEvent event) {
    removeWidget(view);

    if (newParent instanceof LinearLayout) {
      int index = getIndexForNewChildOfLinear((LinearLayout) newParent, event);
      newParent.addView(view, index);
    } else {
      try {
        newParent.addView(view);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void removeWidget(View view) {
    ViewGroup parent = (ViewGroup) view.getParent();
    if (parent != null) {
      parent.removeView(view);
    }
  }

  public int getIndexForNewChildOfLinear(LinearLayout layout, DragEvent event) {
    int orientation = layout.getOrientation();
    int index = 0;

    for (int i = 0; i < layout.getChildCount(); i++) {
      View child = layout.getChildAt(i);
      if (child == shadow) {
        continue;
      }

      if (orientation == LinearLayout.HORIZONTAL) {
        if (child.getRight() < event.getX()) {
          index++;
        }
      } else if (orientation == LinearLayout.VERTICAL) {
        if (child.getBottom() < event.getY()) {
          index++;
        }
      }
    }

    return index;
  }

  public void showDefinedAttributes(final View target) {
    List<String> keys = new ArrayList<>(getViewAttributeMap().get(target).keySet());
    List<String> values = new ArrayList<>(getViewAttributeMap().get(target).values());

    List<HashMap<String, Object>> attrs = new ArrayList<>();
    List<HashMap<String, Object>> allAttrs = getInitializer().getAllAttributesForView(target);

    BottomSheetDialog dialog = new BottomSheetDialog(getContext());
    ShowAttributesDialogBinding binding =
        ShowAttributesDialogBinding.inflate(dialog.getLayoutInflater());
    dialog.setContentView(binding.getRoot());
    TooltipCompat.setTooltipText(binding.btnAdd, "Add attribute");
    TooltipCompat.setTooltipText(binding.btnDelete, "Delete");

    for (String key : keys) {
      for (HashMap<String, Object> map : allAttrs) {
        if (map.get(Constants.KEY_ATTRIBUTE_NAME).toString().equals(key)) {
          attrs.add(map);
          break;
        }
      }
    }

    AppliedAttributeClickListener listener =
        new AppliedAttributeClickListener() {
          @Override
          public void onRemoveButtonClick(int position) {
            dialog.dismiss();
            View view = removeAttribute(target, keys.get(position));
            showDefinedAttributes(view);
          }

          @Override
          public void onClick(int position) {
            showAttributeEdit(target, keys.get(position));
            dialog.dismiss();
          }
        };

    AppliedAttributesAdapter appliedAttributesAdapter =
        new AppliedAttributesAdapter(attrs, values, listener);
    binding.attributesList.setAdapter(appliedAttributesAdapter);
    binding.attributesList.setLayoutManager(
        new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    binding.viewName.setText(target.getClass().getSuperclass().getSimpleName());
    binding.viewFullName.setText(target.getClass().getSuperclass().getName());
    binding.btnAdd.setOnClickListener(
        v -> {
          showAvailableAttributes(target);
          dialog.dismiss();
        });

    binding.btnDelete.setOnClickListener(
        v -> {
          new MaterialAlertDialogBuilder(getContext())
              .setTitle(R.string.delete_view)
              .setMessage(R.string.msg_delete_view)
              .setNegativeButton(
                  R.string.no,
                  (d, w) -> {
                    d.dismiss();
                  })
              .setPositiveButton(
                  R.string.yes,
                  (d, w) -> {
                    IdManager.removeId(target, target instanceof ViewGroup);
                    removeViewAttributes(target);
                    removeWidget(target);
                    updateComponentTree();
                    updateUndoRedoHistory();
                    dialog.dismiss();
                  })
              .show();
        });

    dialog.show();
  }

  private void showAvailableAttributes(final View target) {
    List<HashMap<String, Object>> availableAttrs =
        initializer.getAvailableAttributesForView(target);
    List<String> names = new ArrayList<>();

    for (HashMap<String, Object> attr : availableAttrs) {
      names.add(attr.get("name").toString());
    }

    new MaterialAlertDialogBuilder(getContext())
        .setTitle("Available attributes")
        .setAdapter(
            new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, names),
            (d, w) -> {
              showAttributeEdit(
                  target, availableAttrs.get(w).get(Constants.KEY_ATTRIBUTE_NAME).toString());
            })
        .show();
  }

  private void showAttributeEdit(final View target, final String attributeKey) {
    List<HashMap<String, Object>> allAttrs = initializer.getAllAttributesForView(target);
    HashMap<String, Object> currentAttr = initializer.getAttributeFromKey(attributeKey, allAttrs);
    AttributeMap attributeMap = viewAttributeMap.get(target);

    String[] argumentTypes = currentAttr.get(Constants.KEY_ARGUMENT_TYPE).toString().split("\\|");

    if (argumentTypes.length > 1) {
      if (attributeMap.contains(attributeKey)) {
        String argumentType =
            ArgumentUtil.parseType(attributeMap.getValue(attributeKey), argumentTypes);
        showAttributeEdit(target, attributeKey, argumentType);
        return;
      }
      new MaterialAlertDialogBuilder(getContext())
          .setTitle(R.string.select_arg_type)
          .setAdapter(
              new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, argumentTypes),
              (d, w) -> showAttributeEdit(target, attributeKey, argumentTypes[w]))
          .show();

      return;
    }
    showAttributeEdit(target, attributeKey, argumentTypes[0]);
  }

  @SuppressWarnings("unchecked")
  private void showAttributeEdit(
      final View target, final String attributeKey, final String argumentType) {
    List<HashMap<String, Object>> allAttrs = initializer.getAllAttributesForView(target);
    HashMap<String, Object> currentAttr = initializer.getAttributeFromKey(attributeKey, allAttrs);
    AttributeMap attributeMap = getViewAttributeMap().get(target);

    String savedValue =
        attributeMap.contains(attributeKey) ? attributeMap.getValue(attributeKey) : "";
    String defaultValue =
        currentAttr.containsKey(Constants.KEY_DEFAULT_VALUE)
            ? currentAttr.get(Constants.KEY_DEFAULT_VALUE).toString()
            : null;
    String constant =
        currentAttr.containsKey(Constants.KEY_CONSTANT)
            ? currentAttr.get(Constants.KEY_CONSTANT).toString()
            : null;

    Context context = getContext();

    AttributeDialog dialog = null;

    switch (argumentType) {
      case Constants.ARGUMENT_TYPE_SIZE:
        dialog = new SizeDialog(context, savedValue);
        break;

      case Constants.ARGUMENT_TYPE_DIMENSION:
        dialog =
            new DimensionDialog(context, savedValue, currentAttr.get("dimensionUnit").toString());
        break;

      case Constants.ARGUMENT_TYPE_ID:
        dialog = new IdDialog(context, savedValue);
        break;

      case Constants.ARGUMENT_TYPE_VIEW:
        dialog = new ViewDialog(context, savedValue, constant);
        break;

      case Constants.ARGUMENT_TYPE_BOOLEAN:
        dialog = new BooleanDialog(context, savedValue);
        break;

      case Constants.ARGUMENT_TYPE_DRAWABLE:
        if (savedValue.startsWith("@drawable/")) {
          savedValue = savedValue.replace("@drawable/", "");
        }
        dialog = new StringDialog(context, savedValue, Constants.ARGUMENT_TYPE_DRAWABLE);
        break;

      case Constants.ARGUMENT_TYPE_STRING:
        if (savedValue.startsWith("@string/")) {
          savedValue = savedValue.replace("@string/", "");
        }
        dialog = new StringDialog(context, savedValue, Constants.ARGUMENT_TYPE_STRING);
        break;

      case Constants.ARGUMENT_TYPE_TEXT:
        dialog = new StringDialog(context, savedValue, Constants.ARGUMENT_TYPE_TEXT);
        break;

      case Constants.ARGUMENT_TYPE_INT:
        dialog = new NumberDialog(context, savedValue, Constants.ARGUMENT_TYPE_INT);
        break;

      case Constants.ARGUMENT_TYPE_FLOAT:
        dialog = new NumberDialog(context, savedValue, Constants.ARGUMENT_TYPE_FLOAT);
        break;

      case Constants.ARGUMENT_TYPE_FLAG:
        dialog = new FlagDialog(context, savedValue, (ArrayList<String>) currentAttr.get("arguments"));
        break;

      case Constants.ARGUMENT_TYPE_ENUM:
        dialog = new EnumDialog(context, savedValue, (ArrayList<String>) currentAttr.get("arguments"));
        break;

      case Constants.ARGUMENT_TYPE_COLOR:
        dialog = new ColorDialog(context, savedValue);
        break;
    }

    if (dialog == null) {
      return;
    }

    dialog.setTitle(currentAttr.get("name").toString());
    dialog.setOnSaveValueListener(
        value -> {
          if (defaultValue != null && defaultValue.equals(value)) {
            if (attributeMap.contains(attributeKey)) {
              removeAttribute(target, attributeKey);
            }
          } else {
            getInitializer().applyAttribute(target, value, currentAttr);
            showDefinedAttributes(target);
            updateUndoRedoHistory();
            updateComponentTree();
          }
        });

    dialog.show();
  }

  public void removeViewAttributes(View view) {
    getViewAttributeMap().remove(view);
    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      for (int i = 0; i < group.getChildCount(); i++) {
        removeViewAttributes(group.getChildAt(i));
      }
    }
  }

  @SuppressWarnings("unchecked")
  public View removeAttribute(View target, String attributeKey) {
    List<HashMap<String, Object>> allAttrs = initializer.getAllAttributesForView(target);
    HashMap<String, Object> currentAttr = initializer.getAttributeFromKey(attributeKey, allAttrs);

    AttributeMap attributeMap = getViewAttributeMap().get(target);

    if (currentAttr.containsKey(Constants.KEY_CAN_DELETE)) {
      return target;
    }

    String name = attributeMap.contains("android:id") ? attributeMap.getValue("android:id") : null;
    int id = name != null ? IdManager.getViewId(name.replace("@+id/", "")) : -1;
    attributeMap.removeValue(attributeKey);

    if (attributeKey.equals("android:id")) {
      IdManager.removeId(target, false);
      target.setId(-1);
      target.requestLayout();

      for (View view : getViewAttributeMap().keySet()) {
        AttributeMap map = getViewAttributeMap().get(view);

        for (String key : map.keySet()) {
          String value = map.getValue(key);

          if (value.startsWith("@id/") && value.equals(name.replace("+", ""))) {
            map.removeValue(key);
          }
        }
      }
      updateComponentTree();
      return target;
    }

    getViewAttributeMap().remove(target);

    ViewGroup parent = (ViewGroup) target.getParent();
    int indexOfView = parent.indexOfChild(target);

    parent.removeView(target);

    List<View> childs = new ArrayList<>();

    if (target instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) target;

      if (group.getChildCount() > 0) {
        for (int i = 0; i < group.getChildCount(); i++) {
          childs.add(group.getChildAt(i));
        }
      }

      group.removeAllViews();
    }

    if (name != null) {
      IdManager.removeId(target, false);
    }

    target = (View) InvokeUtil.createView(target.getClass().getName(), getContext());
    rearrangeListeners(target);

    if (target instanceof ViewGroup) {
      target.setMinimumWidth(SizeUtils.px2dp(20));
      target.setMinimumHeight(SizeUtils.px2dp(20));
      ViewGroup group = (ViewGroup) target;
      if (!childs.isEmpty()) {
        for (View child : childs) {
          group.addView(child);
        }
      }
      handler.setTransition(group);
    }

    parent.addView(target, indexOfView);
    getViewAttributeMap().put(target, attributeMap);

    if (name != null) {
      IdManager.addId(target, name, id);
      target.requestLayout();
    }

    List<String> keys = attributeMap.keySet();
    List<String> values = attributeMap.values();
    List<HashMap<String, Object>> attrs = new ArrayList<>();

    for (String key : keys) {
      for (HashMap<String, Object> map : allAttrs) {
        if (map.get(Constants.KEY_ATTRIBUTE_NAME).toString().equals(key)) {
          attrs.add(map);
          break;
        }
      }
    }

    for (int i = 0; i < keys.size(); i++) {
      String key = keys.get(i);
      if (key.equals("android:id")) {
        continue;
      }
      initializer.applyAttribute(target, values.get(i), attrs.get(i));
    }

    try {
      Class<?> cls = target.getClass();
      Method method = cls.getMethod("setStrokeEnabled", boolean.class);
      method.invoke(target, PreferencesUtils.isShowStroke());
    } catch (Exception e) {
      e.printStackTrace();
    }
    updateComponentTree();
    updateUndoRedoHistory();
    return target;
  }

  private void initAttributes() {
    attributes = convertJsonToJavaObject(Constants.ATTRIBUTES_FILE);
    parentAttributes = convertJsonToJavaObject(Constants.PARENT_ATTRIBUTES_FILE);
    viewAttributeMap = new HashMap<>();
    initializer = new AttributeInitializer(viewAttributeMap, attributes, parentAttributes);
  }

  private HashMap<String, List<HashMap<String, Object>>> convertJsonToJavaObject(String filePath) {
    String json = ResourceUtils.readAssets2String(filePath);
    return new Gson()
        .fromJson(
            json, new TypeToken<HashMap<String, List<HashMap<String, Object>>>>() {}.getType());
  }

  public boolean isBlueprint() {
    return this.blueprint;
  }

  public void setBlueprint(boolean blueprint) {
    this.blueprint = blueprint;
    invalidate();
  }

  public DeviceSize getSize() {
    return this.deviceSize;
  }

  public void setSize(DeviceSize deviceSize) {
    this.deviceSize = deviceSize;
    invalidate();
  }

  public UndoRedoManager getUndoRedoManager() {
    return this.undoRedoManager;
  }

  public void setUndoRedoManager(UndoRedoManager undoRedoManager) {
    this.undoRedoManager = undoRedoManager;
  }

  public File getLayoutFile() {
    return this.layoutFile;
  }

  public void setLayoutFile(File layoutFile) {
    this.layoutFile = layoutFile;
    String xml = FileIOUtils.readFile2String(layoutFile);
    clearAll();
    if (xml.isEmpty()) {
      return;
    }
    XmlParser parser = new XmlParser(getContext(), xml);

    addView(parser.getRoot());
    viewAttributeMap = parser.getViewAttributeMap();

    for (View view : viewAttributeMap.keySet()) {
      rearrangeListeners(view);

      if (view instanceof ViewGroup) {
        handler.setDragListener((ViewGroup) view);
        handler.setTransition((ViewGroup) view);
      }

      int minSize = SizeUtils.px2dp(20);
      view.setMinimumWidth(minSize);
      view.setMinimumHeight(minSize);
    }

    updateComponentTree();
    toggleStrokeWidgets();

    initializer = new AttributeInitializer(viewAttributeMap, attributes, parentAttributes);
  }

  public void rearrangeListeners(final View view) {
    final GestureDetector gestureDetector =
        new GestureDetector(
            getContext(),
            new GestureDetector.SimpleOnGestureListener() {
              @Override
              public void onLongPress(MotionEvent event) {
                view.startDragAndDrop(null, new View.DragShadowBuilder(view), view, 0);
              }
            });

    view.setOnTouchListener(
        new OnTouchListener() {
          float startX = 0;
          float startY = 0;
          float endX = 0;
          float endY = 0;

          @Override
          public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
              case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;

              case MotionEvent.ACTION_UP:
                endX = event.getX();
                endY = event.getY();
                float diffX = Math.abs(startX - endX);
                float diffY = Math.abs(startY - endY);

                if (diffX <= 5 && diffY <= 5) {
                  showDefinedAttributes(v);
                }
                break;
            }
            gestureDetector.onTouchEvent(event);
            return true;
          }
        });
  }

  public void updateComponentTree() {
    if (getChildCount() == 0) componentTree.clear();
    else componentTree.setView(getChildAt(0));
  }

  public void clearAll() {
    removeAllViews();
    componentTree.clear();
    viewAttributeMap.clear();
  }

  public HashMap<View, AttributeMap> getViewAttributeMap() {
    return this.viewAttributeMap;
  }

  public void setViewAttributeMap(HashMap<View, AttributeMap> viewAttributeMap) {
    this.viewAttributeMap = viewAttributeMap;
  }

  public HashMap<String, List<HashMap<String, Object>>> getAttributes() {
    return this.attributes;
  }

  public void setAttributes(HashMap<String, List<HashMap<String, Object>>> attributes) {
    this.attributes = attributes;
  }

  public HashMap<String, List<HashMap<String, Object>>> getParentAttributes() {
    return this.parentAttributes;
  }

  public void setParentAttributes(HashMap<String, List<HashMap<String, Object>>> parentAttributes) {
    this.parentAttributes = parentAttributes;
  }

  public AttributeInitializer getInitializer() {
    return this.initializer;
  }

  public void setInitializer(AttributeInitializer initializer) {
    this.initializer = initializer;
  }

  public ComponentTree getComponentTree() {
    return this.componentTree;
  }

  public void setComponentTree(ComponentTree componentTree) {
    this.componentTree = componentTree;
  }

  public View getShadow() {
    return this.shadow;
  }

  public void setShadow(View shadow) {
    this.shadow = shadow;
  }
}
