package com.itsvks.layouteditor.editor;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itsvks.layouteditor.activities.EditorActivity;
import com.itsvks.layouteditor.databinding.ShowAttributeItemBinding;
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
import com.itsvks.layouteditor.managers.IdManager;
import com.itsvks.layouteditor.managers.PreferencesManager;
import com.itsvks.layouteditor.managers.UndoRedoManager;
import com.itsvks.layouteditor.tools.StructureView;
import com.itsvks.layouteditor.tools.XmlLayoutGenerator;
import com.itsvks.layouteditor.tools.XmlLayoutParser;
import com.itsvks.layouteditor.utils.ArgumentUtil;
import com.itsvks.layouteditor.utils.Constants;
import com.itsvks.layouteditor.utils.DimensionUtil;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.InvokeUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "deprecation", "unused"})
public class EditorLayout extends LinearLayoutCompat {
    private static final String TAG = "EditorLayout";

    private LayoutInflater inflater;

    private View shadow;

    private StructureView structureView;

    private AttributeInitializer initializer;

    private UndoRedoManager undoRedo;

    private HashMap<View, AttributeMap> viewAttributeMap = new HashMap<>();

    private HashMap<String, ArrayList<HashMap<String, Object>>> attributes;
    private HashMap<String, ArrayList<HashMap<String, Object>>> parentAttributes;

    private Vibrator vibrator;
    private VibratorManager vibratorManager;

    private boolean drawStrokeEnabled = true;

    private final OnDragListener onDragListener =
            new OnDragListener() {

                @Override
                public boolean onDrag(View host, DragEvent event) {
                    ViewGroup parent = (ViewGroup) host;
                    View draggedView = null;

                    if (event.getLocalState() instanceof View) {
                        draggedView = (View) event.getLocalState();
                    }

                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            {
                                if (PreferencesManager.isEnableVibration()) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                                }
                                if (draggedView != null) {
                                    parent.removeView(draggedView);
                                }
                                return true;
                            }

                        case DragEvent.ACTION_DRAG_EXITED:
                            {
                                removeWidget(shadow);
                                return true;
                            }

                        case DragEvent.ACTION_DRAG_ENDED:
                            {
                                if (!event.getResult() && draggedView != null) {
                                    IdManager.removeId(
                                            draggedView, draggedView instanceof ViewGroup);
                                    removeViewAttributes(draggedView);
                                    viewAttributeMap.remove(draggedView);
                                    updateStructure();
                                }

                                return true;
                            }

                        case DragEvent.ACTION_DRAG_LOCATION:
                        case DragEvent.ACTION_DRAG_ENTERED:
                            {
                                if (shadow.getParent() == null) {
                                    addWidget(shadow, parent, event);
                                } else {
                                    if (parent instanceof LinearLayout) {
                                        int index = parent.indexOfChild(shadow);
                                        int newIndex =
                                                getIndexForNewChildOfLinear(
                                                        (LinearLayout) parent, event);

                                        if (index != newIndex) {
                                            parent.removeView(shadow);
                                            parent.addView(shadow, newIndex);
                                        }
                                    } else {
                                        if (shadow.getParent() != parent) {
                                            addWidget(shadow, parent, event);
                                        }
                                    }
                                }

                                return true;
                            }

                        case DragEvent.ACTION_DROP:
                            {
                                removeWidget(shadow);

                                if (draggedView == null) {
                                    final HashMap<String, Object> data =
                                            (HashMap) event.getLocalState();
                                    final View newView =
                                            (View)
                                                    InvokeUtil.createView(
                                                            data.get("className").toString(),
                                                            getContext());

                                    newView.setLayoutParams(
                                            new ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                                    rearrangeListeners(newView);

                                    if (newView instanceof ViewGroup) {
                                        newView.setOnDragListener(onDragListener);
                                        newView.setMinimumWidth(getDip(20));
                                        newView.setMinimumHeight(getDip(20));
                                        setupViewGroupAnimation((ViewGroup) newView);
                                    }

                                    AttributeMap map = new AttributeMap();
                                    map.putValue("android:layout_width", "wrap_content");
                                    map.putValue("android:layout_height", "wrap_content");
                                    viewAttributeMap.put(newView, map);

                                    addWidget(newView, parent, event);

                                    try {
                                        Class cls = newView.getClass();
                                        Method method =
                                                cls.getMethod("setStrokeEnabled", boolean.class);
                                        method.invoke(newView, drawStrokeEnabled);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if (data.containsKey("defaultAttributes")) {
                                        initializer.applyDefaultAttributes(
                                                newView, (Map) data.get("defaultAttributes"));
                                    }
                                } else {
                                    addWidget(draggedView, parent, event);
                                }

                                updateStructure();
                                if (undoRedo != null) {
                                    updateUndoRedoHistory();
                                }
                                return true;
                            }
                    }

                    return false;
                }
            };

    @SuppressLint("NewApi")
    @TargetApi(31)
    public EditorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflater = LayoutInflater.from(context);
        

        shadow = new View(context);
        shadow.setLayoutParams(new ViewGroup.LayoutParams(getDip(30), getDip(25)));
        shadow.setBackgroundColor(Color.DKGRAY);

        setOnDragListener(onDragListener);
        setupViewGroupAnimation(this);

        attributes =
                new Gson()
                        .fromJson(
                                FileUtil.readFromAsset(Constants.ATTRIBUTES, getContext()),
                                new TypeToken<
                                        HashMap<
                                                String,
                                                ArrayList<
                                                        HashMap<String, Object>>>>() {}.getType());
        parentAttributes =
                new Gson()
                        .fromJson(
                                FileUtil.readFromAsset(Constants.PARENT_ATTRIBUTES, getContext()),
                                new TypeToken<
                                        HashMap<
                                                String,
                                                ArrayList<
                                                        HashMap<String, Object>>>>() {}.getType());

        initializer =
                new AttributeInitializer(context, viewAttributeMap, attributes, parentAttributes);

        vibratorManager =
                (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibrator = vibratorManager.getDefaultVibrator();
        } else {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void toggleStroke() {
        drawStrokeEnabled = !drawStrokeEnabled;
        toggleStrokeWidgets();
    }

    private void toggleStrokeWidgets() {
        try {
            for (View view : viewAttributeMap.keySet()) {
                Class cls = view.getClass();
                Method method = cls.getMethod("setStrokeEnabled", boolean.class);
                method.invoke(view, drawStrokeEnabled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadLayoutFromParser(String xml) {
        clearAll();

        if (xml.equals("")) {
            return;
        }

        XmlLayoutParser parser = new XmlLayoutParser(getContext());
        parser.parseFromXml(xml);

        addView(parser.getRoot());
        viewAttributeMap = parser.getViewAttributeMap();

        for (View view : viewAttributeMap.keySet()) {
            rearrangeListeners(view);

            if (view instanceof ViewGroup) {
                view.setOnDragListener(onDragListener);
                view.setMinimumWidth(getDip(20));
                view.setMinimumHeight(getDip(20));
                setupViewGroupAnimation((ViewGroup) view);
            }
        }

        updateStructure();
        toggleStrokeWidgets();

        initializer =
                new AttributeInitializer(
                        getContext(), viewAttributeMap, attributes, parentAttributes);
    }

    public void undo() {
        if (undoRedo == null) return;
        if (undoRedo.isUndoEnabled()) {
            loadLayoutFromParser(undoRedo.undo());
        }
    }

    public void redo() {
        if (undoRedo == null) return;
        if (undoRedo.isRedoEnabled()) {
            loadLayoutFromParser(undoRedo.redo());
        }
    }

    public void clearAll() {
        removeAllViews();
        structureView.clear();
        viewAttributeMap.clear();
    }

    public void setStructureView(StructureView view) {
        structureView = view;
    }

    public void bindUndoRedoManager(UndoRedoManager manager) {
        undoRedo = manager;
    }

    public void updateStructure() {
        if (getChildCount() == 0) {
            structureView.clear();
        } else {
            structureView.setView(getChildAt(0));
        }
    }

    public void updateUndoRedoHistory() {
        if (undoRedo == null) return;
        String result = new XmlLayoutGenerator().generate(this, false);
        undoRedo.addToHistory(result);
        new EditorActivity().updateUndoRedoBtnState();
    }

    private void rearrangeListeners(final View view) {
        final GestureDetector gestureDetector =
                new GestureDetector(
                        new GestureDetector.SimpleOnGestureListener() {

                            @Override
                            public void onLongPress(MotionEvent event) {
                                view.startDragAndDrop(null, new DragShadowBuilder(view), view, 0);
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

                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(final View v, MotionEvent event) {
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

                                if (diffX <= 5 && diffY <= 5 && bClick == true) {
                                    showDefinedAttributes(v);
                                }

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
            /*
            hack check scrollview parent
            ****/
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

        if (orientation == LinearLayout.HORIZONTAL) {
            int index = 0;

            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);

                if (child == shadow) continue;

                if (child.getRight() < event.getX()) index++;
            }

            return index;
        }

        if (orientation == LinearLayout.VERTICAL) {
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

    private void setupViewGroupAnimation(ViewGroup group) {
        LayoutTransition transition = new LayoutTransition();
        transition.disableTransitionType(LayoutTransition.DISAPPEARING);
        transition.enableTransitionType(LayoutTransition.CHANGING);
        transition.setDuration(150);

        group.setLayoutTransition(transition);
    }

    public void showDefinedAttributes(final View target) {
        final BottomSheetDialog dialog = new BottomSheetDialog(getContext());

        ShowAttributesDialogBinding dialogBinding = ShowAttributesDialogBinding.inflate(inflater);
        dialog.setContentView(dialogBinding.getRoot());

        final ArrayList<String> keys = viewAttributeMap.get(target).keySet();
        final ArrayList<String> values = viewAttributeMap.get(target).values();

        final ArrayList<HashMap<String, Object>> attrs = new ArrayList<>();
        final ArrayList<HashMap<String, Object>> allAttrs =
                initializer.getAllAttributesForView(target);

        for (String key : keys) {
            for (HashMap<String, Object> map : allAttrs) {
                if (map.get("attributeName").toString().equals(key)) {
                    attrs.add(map);
                    break;
                }
            }
        }

        final BaseAdapter adapter =
                new BaseAdapter() {

                    @Override
                    public int getCount() {
                        return keys.size();
                    }

                    @Override
                    public long getItemId(int arg0) {
                        return 0;
                    }

                    @Override
                    public Object getItem(int pos) {
                        return null;
                    }
                    
                    @SuppressLint("ViewHolder")
                    @Override
                    public View getView(int pos, View buffer, ViewGroup parent) {
                        final HashMap<String, Object> item = attrs.get(pos);

                        final ShowAttributeItemBinding itemBinding =
                                ShowAttributeItemBinding.inflate(inflater);
                        itemBinding.textName.setText(item.get("name").toString());
                        itemBinding.textValue.setText(values.get(pos));

                        if (item.containsKey("canDelete")) {
                            itemBinding.btnDelete.setVisibility(View.GONE);
                        }

                        itemBinding
                                .getRoot()
                                .setOnClickListener(
                                        v -> {
                                            showAttributeEdit(target, keys.get(pos));
                                            dialog.dismiss();
                                        });

                        itemBinding.btnDelete.setOnClickListener(
                                v -> {
                                    dialog.dismiss();

                                    View view = removeAttribute(target, keys.get(pos));
                                    showDefinedAttributes(view);
                                });

                        return itemBinding.getRoot();
                    }
                };

        dialogBinding.listView.setAdapter(adapter);
        dialogBinding.widgetName.setText(target.getClass().getSuperclass().getSimpleName());
        dialogBinding.btnAdd.setOnClickListener(
                v -> {
                    showAvailableAttributes(target);
                    dialog.dismiss();
                });

        dialogBinding.btnDelete.setOnClickListener(
                v -> {
                    final MaterialAlertDialogBuilder confirm =
                            new MaterialAlertDialogBuilder(getContext());
                    confirm.setTitle("Delete view");
                    confirm.setMessage("Do you want to remove the view?");
                    confirm.setNegativeButton("No", (di, which) -> {});
                    confirm.setPositiveButton(
                            "Yes",
                            (di, which) -> {

                                // delete widget and id
                                IdManager.removeId(target, target instanceof ViewGroup);
                                removeViewAttributes(target);
                                removeWidget(target);
                                //	viewAttributeMap.remove(target);
                                updateStructure();
                                updateUndoRedoHistory();
                                dialog.dismiss();
                            });
                    confirm.create().show();
                });

        dialog.show();
    }

    private void showAvailableAttributes(final View target) {
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Available attributes");

        final ArrayList<HashMap<String, Object>> availableAttrs =
                initializer.getAvailableAttributesForView(target);
        final ArrayList<String> names = new ArrayList<>();

        for (HashMap<String, Object> attr : availableAttrs) {
            names.add(attr.get("name").toString());
        }

        builder.setAdapter(
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, names),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface di, int which) {
                        showAttributeEdit(
                                target, availableAttrs.get(which).get("attributeName").toString());
                    }
                });

        builder.create().show();
    }

    private void showAttributeEdit(final View target, final String attributeKey) {
        final ArrayList<HashMap<String, Object>> allAttrs =
                initializer.getAllAttributesForView(target);
        final HashMap<String, Object> currentAttr =
                initializer.getAttributeFromKey(attributeKey, allAttrs);
        final AttributeMap attributeMap = viewAttributeMap.get(target);

        final String[] argumentTypes = currentAttr.get("argumentType").toString().split("\\|");

        if (argumentTypes.length > 1) {
            if (attributeMap.contains(attributeKey)) {
                String argumentType =
                        ArgumentUtil.parseType(attributeMap.getValue(attributeKey), argumentTypes);
                showAttributeEdit(target, attributeKey, argumentType);
                return;
            }

            final MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getContext());
            dialog.setTitle("Select argument type");
            dialog.setAdapter(
                    new ArrayAdapter<String>(
                            getContext(), android.R.layout.simple_list_item_1, argumentTypes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface di, int which) {
                            showAttributeEdit(target, attributeKey, argumentTypes[which]);
                        }
                    });
            dialog.create().show();
            return;
        }

        showAttributeEdit(target, attributeKey, argumentTypes[0]);
    }

    private void showAttributeEdit(
            final View target, final String attributeKey, final String argumentType) {
        final ArrayList<HashMap<String, Object>> allAttrs =
                initializer.getAllAttributesForView(target);
        final HashMap<String, Object> currentAttr =
                initializer.getAttributeFromKey(attributeKey, allAttrs);
        final AttributeMap attributeMap = viewAttributeMap.get(target);

        final String savedValue =
                attributeMap.contains(attributeKey) ? attributeMap.getValue(attributeKey) : "";
        final String defaultValue =
                currentAttr.containsKey("defaultValue")
                        ? currentAttr.get("defaultValue").toString()
                        : null;
        final String constant =
                currentAttr.containsKey("constant") ? currentAttr.get("constant").toString() : null;

        final Context context = getContext();

        AttributeDialog dialog = null;

        switch (argumentType) {
            case "size":
                dialog = new SizeDialog(context, savedValue);
                break;

            case "dimension":
                dialog =
                        new DimensionDialog(
                                context, savedValue, currentAttr.get("dimensionUnit").toString());
                break;

            case "id":
                dialog = new IdDialog(context, savedValue);
                break;

            case "view":
                dialog = new ViewDialog(context, savedValue, constant);
                break;

            case "boolean":
                dialog = new BooleanDialog(context, savedValue);
                break;

            case "drawable":
            case "string":
                dialog = new StringDialog(context, savedValue);
                break;

            case "int":
                dialog = new NumberDialog(context, savedValue, "int");
                break;

            case "float":
                dialog = new NumberDialog(context, savedValue, "float");
                break;

            case "flag":
                dialog =
                        new FlagDialog(
                                context, savedValue, (ArrayList) currentAttr.get("arguments"));
                break;

            case "enum":
                dialog =
                        new EnumDialog(
                                context, savedValue, (ArrayList) currentAttr.get("arguments"));
                break;

            case "color":
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
                        initializer.applyAttribute(target, value, currentAttr);
                        showDefinedAttributes(target);
                        updateUndoRedoHistory();
                    }
                });

        dialog.show();
    }

    private void removeViewAttributes(View view) {
        viewAttributeMap.remove(view);

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); i++) {
                removeViewAttributes(group.getChildAt(i));
            }
        }
    }

    private View removeAttribute(View target, final String attributeKey) {
        final ArrayList<HashMap<String, Object>> allAttrs =
                initializer.getAllAttributesForView(target);
        final HashMap<String, Object> currentAttr =
                initializer.getAttributeFromKey(attributeKey, allAttrs);

        final AttributeMap attributeMap = viewAttributeMap.get(target);

        if (currentAttr.containsKey("canDelete")) {
            return target;
        }

        final String name =
                attributeMap.contains("android:id") ? attributeMap.getValue("android:id") : null;
        final int id = name != null ? IdManager.getViewId(name.replace("@+id/", "")) : -1;

        attributeMap.removeValue(attributeKey);

        if (attributeKey.equals("android:id")) {
            IdManager.removeId(target, false);
            target.setId(-1);
            target.requestLayout();

            // delete all id attributes for views
            for (View view : viewAttributeMap.keySet()) {
                AttributeMap map = viewAttributeMap.get(view);

                for (String key : map.keySet()) {
                    String value = map.getValue(key);

                    if (value.startsWith("@id/") && value.equals(name.replace("+", ""))) {
                        map.removeValue(key);
                    }
                }
            }

            return target;
        }

        viewAttributeMap.remove(target);

        final ViewGroup parent = (ViewGroup) target.getParent();
        final int indexOfView = parent.indexOfChild(target);

        parent.removeView(target);

        final ArrayList<View> childs = new ArrayList<>();

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
            target.setMinimumWidth(getDip(20));
            target.setMinimumHeight(getDip(20));
            target.setOnDragListener(onDragListener);

            final ViewGroup group = (ViewGroup) target;

            if (childs.size() > 0) {
                for (int i = 0; i < childs.size(); i++) {
                    group.addView(childs.get(i));
                }
            }

            setupViewGroupAnimation(group);
        }

        parent.addView(target, indexOfView);
        viewAttributeMap.put(target, attributeMap);

        if (name != null) {
            IdManager.addId(target, name, id);
            target.requestLayout();
        }

        final ArrayList<String> keys = attributeMap.keySet();
        final ArrayList<String> values = attributeMap.values();

        final ArrayList<HashMap<String, Object>> attrs = new ArrayList<>();

        for (String key : keys) {
            for (HashMap<String, Object> map : allAttrs) {
                if (map.get("attributeName").toString().equals(key)) {
                    attrs.add(map);
                    break;
                }
            }
        }

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);

            if (key.equals("android:id")) continue;

            initializer.applyAttribute(target, values.get(i), attrs.get(i));
        }

        try {
            Class cls = target.getClass();
            Method method = cls.getMethod("setStrokeEnabled", boolean.class);
            method.invoke(target, drawStrokeEnabled);
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateStructure();
        updateUndoRedoHistory();

        return target;
    }

    public HashMap<View, AttributeMap> getViewAttributeMap() {
        return viewAttributeMap;
    }

    @Override
    public void addView(View arg0, int arg1) {
        if (getChildCount() == 0) {
            super.addView(arg0, arg1);
        }
    }

    private void toast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    private int getDip(int value) {
        return (int) DimensionUtil.getDip(value, getContext());
    }

    private String getString(int id) {
        return getResources().getString(id);
    }
}
