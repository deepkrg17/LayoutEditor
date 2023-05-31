package com.itsvks.layouteditor.editor;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.VibrateUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itsvks.layouteditor.adapters.AppliedAttributesAdapter;
import com.itsvks.layouteditor.databinding.ShowAttributesDialogBinding;
import com.itsvks.layouteditor.editor.dialogs.AttributeDialog;
import com.itsvks.layouteditor.editor.dialogs.BooleanDialog;
import com.itsvks.layouteditor.editor.dialogs.ColorDialog;
import com.itsvks.layouteditor.editor.dialogs.DimensionDialog;
import com.itsvks.layouteditor.editor.dialogs.EnumDialog;
import com.itsvks.layouteditor.editor.dialogs.FlagDialog;
import com.itsvks.layouteditor.editor.dialogs.IdDialog;
import com.itsvks.layouteditor.editor.dialogs.NumberDialog;
import com.itsvks.layouteditor.editor.dialogs.SizeDialog;
import com.itsvks.layouteditor.editor.dialogs.StringDialog;
import com.itsvks.layouteditor.editor.dialogs.ViewDialog;
import com.itsvks.layouteditor.editor.initializer.AttributeInitializer;
import com.itsvks.layouteditor.editor.initializer.AttributeMap;
import com.itsvks.layouteditor.interfaces.AppliedAttributeClickListener;
import com.itsvks.layouteditor.managers.IdManager;
import com.itsvks.layouteditor.managers.PreferencesManager;
import com.itsvks.layouteditor.managers.UndoRedoManager;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.tools.XmlLayoutGenerator;
import com.itsvks.layouteditor.tools.XmlLayoutParser;
import com.itsvks.layouteditor.utils.ArgumentUtil;
import com.itsvks.layouteditor.utils.Constants;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.InvokeUtil;
import com.itsvks.layouteditor.utils.Utils;
import com.itsvks.layouteditor.views.StructureView;
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
  private View shadow;
  private StructureView structureView;
  private UndoRedoManager undoRedoManager;

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
    isBlueprint = viewType == ViewType.BLUEPRINT;
    deviceConfiguration = new DeviceConfiguration(DeviceSize.LARGE);
    initAttributes();
    shadow = new View(context);
    paint = new Paint();

    shadow.setBackgroundColor(
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorOutline));
    shadow.setLayoutParams(
        new ViewGroup.LayoutParams(Utils.pxToDp(context, 50), Utils.pxToDp(context, 35)));
    paint.setStrokeWidth(Utils.pxToDp(context, 3));

    setOrientation(VERTICAL);
    setTransition(this);
    setDragListener(this);

    toggleStrokeWidgets();
    setBlueprintOnChilds();
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
        setScaleX(0.85f);
        setScaleY(0.85f);
        break;
      case LARGE:
      default:
        setScaleX(0.95f);
        setScaleY(0.95f);
        break;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return super.onTouchEvent(event);
  }

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
    setBackgroundColor(
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface));
    Utils.drawDashPathStroke(this, canvas, paint);
  }

  public void setViewType(ViewType viewType) {
    this.viewType = viewType;
    isBlueprint = viewType == ViewType.BLUEPRINT;
    setBlueprintOnChilds();
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

  private void setTransition(ViewGroup group) {
    if (group instanceof RecyclerView) return;
    LayoutTransition transition = new LayoutTransition();
    transition.disableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
    transition.enableTransitionType(LayoutTransition.CHANGING);
    transition.setDuration(150);
    group.setLayoutTransition(transition);
  }

  @SuppressWarnings("unchecked")
  private void toggleStrokeWidgets() {
    try {
      for (View view : VIEW_ATTRIBUTE_MAP.keySet()) {
        Class cls = view.getClass();
        Method method = cls.getMethod("setStrokeEnabled", boolean.class);
        method.invoke(view, PreferencesManager.isShowStroke());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
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

  @SuppressWarnings("unchecked")
  private void setDragListener(ViewGroup group) {
    group.setOnDragListener(
        new OnDragListener() {
          @Override
          public boolean onDrag(View host, DragEvent event) {
            ViewGroup parent = (ViewGroup) host;
            View draggedView =
                event.getLocalState() instanceof View ? (View) event.getLocalState() : null;

            switch (event.getAction()) {
              case DragEvent.ACTION_DRAG_STARTED:
                if (PreferencesManager.isEnableVibration()) VibrateUtils.vibrate(100);
                if (draggedView != null
                    && !(draggedView instanceof AdapterView && parent instanceof AdapterView))
                  parent.removeView(draggedView);
                break;
              case DragEvent.ACTION_DRAG_EXITED:
                removeWidget(shadow);
                updateUndoRedoHistory();
                break;
              case DragEvent.ACTION_DRAG_ENDED:
                if (!event.getResult() && draggedView != null) {
                  IdManager.removeId(draggedView, draggedView instanceof ViewGroup);
                  removeViewAttributes(draggedView);
                  VIEW_ATTRIBUTE_MAP.remove(draggedView);
                  updateStructure();
                }
                break;
              case DragEvent.ACTION_DRAG_LOCATION:
              case DragEvent.ACTION_DRAG_ENTERED:
                if (shadow.getParent() == null) addWidget(shadow, parent, event);
                else {
                  if (parent instanceof LinearLayout) {
                    int index = parent.indexOfChild(shadow);
                    int newIndex = getIndexForNewChildOfLinear((LinearLayout) parent, event);

                    if (index != newIndex) {
                      parent.removeView(shadow);
                      try {
                        parent.addView(shadow, newIndex);
                      } catch (IllegalStateException e) {

                      }
                    }
                  } else {
                    if (shadow.getParent() != parent) addWidget(shadow, parent, event);
                  }
                }
                break;
              case DragEvent.ACTION_DROP:
                removeWidget(shadow);
                if (getChildCount() >= 1) {
                  if (!(getChildAt(0) instanceof ViewGroup)) {
                    Toast.makeText(
                            getContext(),
                            "Can\'t add more than one widget in the editor.",
                            Toast.LENGTH_SHORT)
                        .show();
                    break;
                  } else {
                    if (parent instanceof DesignEditor) parent = (ViewGroup) getChildAt(0);
                  }
                }
                if (draggedView == null) {
                  final HashMap<String, Object> data = (HashMap) event.getLocalState();
                  final View newView =
                      (View)
                          InvokeUtil.createView(
                              data.get(Constants.KEY_CLASS_NAME).toString(), getContext());

                  newView.setLayoutParams(
                      new ViewGroup.LayoutParams(
                          ViewGroup.LayoutParams.WRAP_CONTENT,
                          ViewGroup.LayoutParams.WRAP_CONTENT));
                  rearrangeListeners(newView);

                  if (newView instanceof ViewGroup) {
                    setDragListener((ViewGroup) newView);
                    setTransition((ViewGroup) newView);
                  }
                  newView.setMinimumWidth(Utils.pxToDp(getContext(), 20));
                  newView.setMinimumHeight(Utils.pxToDp(getContext(), 20));

                  AttributeMap map = new AttributeMap();
                  map.putValue("android:layout_width", "wrap_content");
                  map.putValue("android:layout_height", "wrap_content");
                  VIEW_ATTRIBUTE_MAP.put(newView, map);

                  addWidget(newView, parent, event);

                  try {
                    Class cls = newView.getClass();
                    Method setStrokeEnabled = cls.getMethod("setStrokeEnabled", boolean.class);
                    Method setBlueprint = cls.getMethod("setBlueprint", boolean.class);
                    setStrokeEnabled.invoke(newView, PreferencesManager.isShowStroke());
                    setBlueprint.invoke(newView, isBlueprint);
                  } catch (Exception e) {
                    e.printStackTrace();
                  }

                  if (data.containsKey(Constants.KEY_DEFAULT_ATTRS)) {
                    INITIALIZER.applyDefaultAttributes(
                        newView, (Map) data.get(Constants.KEY_DEFAULT_ATTRS));
                  }
                } else addWidget(draggedView, parent, event);
                updateStructure();
                updateUndoRedoHistory();
                break;
            }
            return true;
          }
        });
  }

  public void loadLayoutFromParser(String xml) {
    clearAll();

    if (xml.isEmpty()) return;

    XmlLayoutParser parser = new XmlLayoutParser(getContext());
    parser.parseFromXml(xml, getContext());

    addView(parser.getRoot());
    VIEW_ATTRIBUTE_MAP = parser.getViewAttributeMap();

    for (View view : VIEW_ATTRIBUTE_MAP.keySet()) {
      rearrangeListeners(view);

      if (view instanceof ViewGroup) {
        setDragListener((ViewGroup) view);
        setTransition((ViewGroup) view);
      }
      view.setMinimumWidth(Utils.pxToDp(getContext(), 20));
      view.setMinimumHeight(Utils.pxToDp(getContext(), 20));
    }

    updateStructure();
    toggleStrokeWidgets();

    INITIALIZER =
        new AttributeInitializer(getContext(), VIEW_ATTRIBUTE_MAP, ATTRIBUTES, PARENT_ATTRIBUTES);
  }

  public void undo() {
    if (undoRedoManager == null) return;
    if (undoRedoManager.isUndoEnabled()) loadLayoutFromParser(undoRedoManager.undo());
  }

  public void redo() {
    if (undoRedoManager == null) return;
    if (undoRedoManager.isRedoEnabled()) loadLayoutFromParser(undoRedoManager.redo());
  }

  public void clearAll() {
    removeAllViews();
    structureView.clear();
    VIEW_ATTRIBUTE_MAP.clear();
  }

  public void setStructureView(StructureView view) {
    structureView = view;
  }

  public void bindUndoRedoManager(UndoRedoManager manager) {
    undoRedoManager = manager;
  }

  public void updateStructure() {
    if (getChildCount() == 0) structureView.clear();
    else structureView.setView(getChildAt(0));
  }

  public void updateUndoRedoHistory() {
    if (undoRedoManager == null) return;
    String result = new XmlLayoutGenerator().generate(this, false);
    undoRedoManager.addToHistory(result);
  }

  private void rearrangeListeners(final View view) {
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

          boolean bClick = true;
          float startX = 0;
          float startY = 0;
          float endX = 0;
          float endY = 0;
          float diffX = 0;
          float diffY = 0;

          @Override
          public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
              case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                bClick = true;
                break;

              case MotionEvent.ACTION_UP:
                endX = event.getX();
                endY = event.getY();
                diffX = Math.abs(startX - endX);
                diffY = Math.abs(startY - endY);

                if (diffX <= 5 && diffY <= 5 && bClick == true) showDefinedAttributes(v);

                bClick = false;
                break;
            }
            gestureDetector.onTouchEvent(event);
            return true;
          }
        });
  }

  private void addWidget(View view, ViewGroup newParent, DragEvent event) {
    removeWidget(view);
    if (newParent instanceof LinearLayout) {
      int index = getIndexForNewChildOfLinear((LinearLayout) newParent, event);
      newParent.addView(view, index);
    } else {
      try {
        newParent.addView(view, newParent.getChildCount());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void removeWidget(View view) {
    ViewGroup parent = (ViewGroup) view.getParent();
    if (parent != null) parent.removeView(view);
  }

  private int getIndexForNewChildOfLinear(LinearLayout layout, DragEvent event) {
    int orientation = layout.getOrientation();
    if (orientation == HORIZONTAL) {
      int index = 0;
      for (int i = 0; i < layout.getChildCount(); i++) {
        View child = layout.getChildAt(i);
        if (child == shadow) continue;
        if (child.getRight() < event.getX()) index++;
      }
      return index;
    }
    if (orientation == VERTICAL) {
      int index = 0;
      for (int i = 0; i < layout.getChildCount(); i++) {
        View child = layout.getChildAt(i);
        if (child == shadow) continue;
        if (child.getBottom() < event.getY()) index++;
      }
      return index;
    }
    return -1;
  }

  public void showDefinedAttributes(final View target) {
    final List<String> keys = VIEW_ATTRIBUTE_MAP.get(target).keySet();
    final List<String> values = VIEW_ATTRIBUTE_MAP.get(target).values();

    final List<HashMap<String, Object>> attrs = new ArrayList<>();
    final List<HashMap<String, Object>> allAttrs = INITIALIZER.getAllAttributesForView(target);

    final BottomSheetDialog dialog = new BottomSheetDialog(getContext());
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
                    updateStructure();
                    updateUndoRedoHistory();
                    dialog.dismiss();
                  })
              .show();
        });

    dialog.show();
  }

  private void showAvailableAttributes(final View target) {
    final List<HashMap<String, Object>> availableAttrs =
        INITIALIZER.getAvailableAttributesForView(target);
    final List<String> names = new ArrayList<>();

    for (HashMap<String, Object> attr : availableAttrs) {
      names.add(attr.get("name").toString());
    }

    new MaterialAlertDialogBuilder(getContext())
        .setTitle("Available attributes")
        .setAdapter(
            new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, names),
            (d, w) -> {
              showAttributeEdit(
                  target, availableAttrs.get(w).get(Constants.KEY_ATTRIBUTE_NAME).toString());
            })
        .show();
  }

  private void showAttributeEdit(final View target, final String attributeKey) {
    final List<HashMap<String, Object>> allAttrs = INITIALIZER.getAllAttributesForView(target);
    final HashMap<String, Object> currentAttr =
        INITIALIZER.getAttributeFromKey(attributeKey, allAttrs);
    final AttributeMap attributeMap = VIEW_ATTRIBUTE_MAP.get(target);

    final String[] argumentTypes =
        currentAttr.get(Constants.KEY_ARGUMENT_TYPE).toString().split("\\|");

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
              new ArrayAdapter<String>(
                  getContext(), android.R.layout.simple_list_item_1, argumentTypes),
              (d, w) -> {
                showAttributeEdit(target, attributeKey, argumentTypes[w]);
              })
          .show();

      return;
    }
    showAttributeEdit(target, attributeKey, argumentTypes[0]);
  }

  @SuppressWarnings("unchecked")
  private void showAttributeEdit(
      final View target, final String attributeKey, final String argumentType) {
    final List<HashMap<String, Object>> allAttrs = INITIALIZER.getAllAttributesForView(target);
    final HashMap<String, Object> currentAttr =
        INITIALIZER.getAttributeFromKey(attributeKey, allAttrs);
    final AttributeMap attributeMap = VIEW_ATTRIBUTE_MAP.get(target);

    String savedValue =
        attributeMap.contains(attributeKey) ? attributeMap.getValue(attributeKey) : "";
    final String defaultValue =
        currentAttr.containsKey(Constants.KEY_DEFAULT_VALUE)
            ? currentAttr.get(Constants.KEY_DEFAULT_VALUE).toString()
            : null;
    final String constant =
        currentAttr.containsKey(Constants.KEY_CONSTANT)
            ? currentAttr.get(Constants.KEY_CONSTANT).toString()
            : null;

    final Context context = getContext();

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
        if (savedValue.toString().startsWith("@drawable/")) {
          savedValue = savedValue.replace("@drawable/", "");
        }
        dialog = new StringDialog(context, savedValue, Constants.ARGUMENT_TYPE_DRAWABLE);
        break;

      case Constants.ARGUMENT_TYPE_STRING:
        if (savedValue.toString().startsWith("@string/")) {
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
        dialog = new FlagDialog(context, savedValue, (ArrayList) currentAttr.get("arguments"));
        break;

      case Constants.ARGUMENT_TYPE_ENUM:
        dialog = new EnumDialog(context, savedValue, (ArrayList) currentAttr.get("arguments"));
        break;

      case Constants.ARGUMENT_TYPE_COLOR:
        dialog = new ColorDialog(context, savedValue);
        break;
    }

    if (dialog == null) return;

    dialog.setTitle(currentAttr.get("name").toString());
    dialog.setOnSaveValueListener(
        value -> {
          if (defaultValue != null && defaultValue.equals(value)) {
            if (attributeMap.contains(attributeKey)) removeAttribute(target, attributeKey);

          } else {
            INITIALIZER.applyAttribute(target, value, currentAttr);
            showDefinedAttributes(target);
            updateUndoRedoHistory();
            updateStructure();
          }
        });

    dialog.show();
  }

  private void removeViewAttributes(View view) {
    VIEW_ATTRIBUTE_MAP.remove(view);
    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      for (int i = 0; i < group.getChildCount(); i++) {
        removeViewAttributes(group.getChildAt(i));
      }
    }
  }

  @SuppressWarnings("unchecked")
  private View removeAttribute(View target, String attributeKey) {
    final List<HashMap<String, Object>> allAttrs = INITIALIZER.getAllAttributesForView(target);
    final HashMap<String, Object> currentAttr =
        INITIALIZER.getAttributeFromKey(attributeKey, allAttrs);

    final AttributeMap attributeMap = VIEW_ATTRIBUTE_MAP.get(target);

    if (currentAttr.containsKey(Constants.KEY_CAN_DELETE)) return target;

    final String name =
        attributeMap.contains("android:id") ? attributeMap.getValue("android:id") : null;
    final int id = name != null ? IdManager.getViewId(name.replace("@+id/", "")) : -1;
    attributeMap.removeValue(attributeKey);

    if (attributeKey.equals("android:id")) {
      IdManager.removeId(target, false);
      target.setId(-1);
      target.requestLayout();

      // delete all id attributes for views
      for (View view : VIEW_ATTRIBUTE_MAP.keySet()) {
        AttributeMap map = VIEW_ATTRIBUTE_MAP.get(view);

        for (String key : map.keySet()) {
          String value = map.getValue(key);

          if (value.startsWith("@id/") && value.equals(name.replace("+", ""))) map.removeValue(key);
        }
      }
      updateStructure();
      return target;
    }

    VIEW_ATTRIBUTE_MAP.remove(target);

    final ViewGroup parent = (ViewGroup) target.getParent();
    final int indexOfView = parent.indexOfChild(target);

    parent.removeView(target);

    final List<View> childs = new ArrayList<>();

    if (target instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) target;

      if (group.getChildCount() > 0) {
        for (int i = 0; i < group.getChildCount(); i++) {
          childs.add(group.getChildAt(i));
        }
      }

      group.removeAllViews();
    }

    if (name != null) IdManager.removeId(target, false);

    target = (View) InvokeUtil.createView(target.getClass().getName(), getContext());
    rearrangeListeners(target);

    if (target instanceof ViewGroup) {
      target.setMinimumWidth(Utils.pxToDp(getContext(), 20));
      target.setMinimumHeight(Utils.pxToDp(getContext(), 20));
      final ViewGroup group = (ViewGroup) target;
      if (childs.size() > 0) {
        for (int i = 0; i < childs.size(); i++) {
          group.addView(childs.get(i));
        }
      }
      setTransition(group);
    }

    parent.addView(target, indexOfView);
    VIEW_ATTRIBUTE_MAP.put(target, attributeMap);

    if (name != null) {
      IdManager.addId(target, name, id);
      target.requestLayout();
    }

    final List<String> keys = attributeMap.keySet();
    final List<String> values = attributeMap.values();
    final List<HashMap<String, Object>> attrs = new ArrayList<>();

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
      if (key.equals("android:id")) continue;
      INITIALIZER.applyAttribute(target, values.get(i), attrs.get(i));
    }

    try {
      Class cls = target.getClass();
      Method method = cls.getMethod("setStrokeEnabled", boolean.class);
      method.invoke(target, PreferencesManager.isShowStroke());
    } catch (Exception e) {
      e.printStackTrace();
    }
    updateStructure();
    updateUndoRedoHistory();
    return target;
  }

  private void initAttributes() {
    ATTRIBUTES = convertJsonToJavaObject(Constants.ATTRIBUTES_FILE);
    PARENT_ATTRIBUTES = convertJsonToJavaObject(Constants.PARENT_ATTRIBUTES_FILE);
    VIEW_ATTRIBUTE_MAP = new HashMap<>();
    INITIALIZER =
        new AttributeInitializer(getContext(), VIEW_ATTRIBUTE_MAP, ATTRIBUTES, PARENT_ATTRIBUTES);
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

  public HashMap<View, AttributeMap> getViewAttributeMap() {
    return this.VIEW_ATTRIBUTE_MAP;
  }
}
