package com.itsvks.editor.designeditor;

import android.animation.LayoutTransition;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.VibrateUtils;
import com.itsvks.editor.Constants;
import com.itsvks.editor.designeditor.initializer.AttributeMap;
import com.itsvks.editor.managers.IdManager;
import com.itsvks.editor.utils.InvokeUtil;
import com.itsvks.editor.utils.PreferencesUtils;
import com.itsvks.editor.utils.Utils;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DesignEditorHandler {
  private DesignEditor editor;

  public DesignEditorHandler(DesignEditor editor) {
    this.editor = editor;
  }

  public void setDragListener(ViewGroup group) {
    group.setOnDragListener(
        new View.OnDragListener() {
          @Override
          public boolean onDrag(View host, DragEvent event) {
            ViewGroup parent = (ViewGroup) host;
            View draggedView =
                event.getLocalState() instanceof View ? (View) event.getLocalState() : null;

            switch (event.getAction()) {
              case DragEvent.ACTION_DRAG_STARTED:
                handleDragStarted(parent, draggedView);
                break;
              case DragEvent.ACTION_DRAG_EXITED:
                handleDragExited();
                break;
              case DragEvent.ACTION_DRAG_ENDED:
                handleDragEnded(event.getResult(), draggedView);
                break;
              case DragEvent.ACTION_DRAG_LOCATION:
              case DragEvent.ACTION_DRAG_ENTERED:
                handleDragLocation(parent, event);
                break;
              case DragEvent.ACTION_DROP:
                handleDragDrop(parent, draggedView, event);
                break;
            }
            return true;
          }
        });
  }

  public void handleDragStarted(ViewGroup parent, View draggedView) {
    if (PreferencesUtils.isEnableVibration()) VibrateUtils.vibrate(100);
    if (draggedView != null
        && !(draggedView instanceof AdapterView && parent instanceof AdapterView))
      parent.removeView(draggedView);
  }

  public void handleDragExited() {
    editor.removeWidget(editor.getShadow());
    editor.updateUndoRedoHistory();
  }

  public void handleDragEnded(boolean eventResult, View draggedView) {
    if (!eventResult && draggedView != null) {
      IdManager.removeId(draggedView, draggedView instanceof ViewGroup);
      editor.removeViewAttributes(draggedView);
      editor.getViewAttributeMap().remove(draggedView);
      editor.updateComponentTree();
    }
  }

  public void handleDragLocation(ViewGroup parent, DragEvent event) {
    if (editor.getShadow().getParent() == null) editor.addWidget(editor.getShadow(), parent, event);
    else {
      if (parent instanceof LinearLayout) {
        int index = parent.indexOfChild(editor.getShadow());
        int newIndex = editor.getIndexForNewChildOfLinear((LinearLayout) parent, event);

        if (index != newIndex) {
          parent.removeView(editor.getShadow());
          try {
            parent.addView(editor.getShadow(), newIndex);
          } catch (IllegalStateException e) {
            // Handle exception if necessary
          }
        }
      } else {
        if (editor.getShadow().getParent() != parent) editor.addWidget(editor.getShadow(), parent, event);
      }
    }
  }

  public void handleDragDrop(ViewGroup parent, View draggedView, DragEvent event) {
    editor.removeWidget(editor.getShadow());
    if (editor.getChildCount() >= 1) {
      if (!(editor.getChildAt(0) instanceof ViewGroup)) {
        Toast.makeText(
                editor.getContext(), "Can't add more than one widget in the editor.", Toast.LENGTH_SHORT)
            .show();
        return;
      } else {
        if (parent instanceof DesignEditor) parent = (ViewGroup) editor.getChildAt(0);
      }
    }
    if (draggedView == null) {
      handleNullDraggedView(parent, event);
    } else {
      editor.addWidget(draggedView, parent, event);
    }
    editor.updateComponentTree();
    editor.updateUndoRedoHistory();
  }

  public void handleNullDraggedView(ViewGroup parent, DragEvent event) {
    final HashMap<String, Object> data = (HashMap) event.getLocalState();
    final View newView =
        (View) InvokeUtil.createView(data.get(Constants.KEY_CLASS_NAME).toString(), editor.getContext());

    newView.setLayoutParams(
        new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    editor.rearrangeListeners(newView);

    if (newView instanceof ViewGroup) {
      setDragListener((ViewGroup) newView);
      setTransition((ViewGroup) newView);
    }
    newView.setMinimumWidth(SizeUtils.dp2px(20));
    newView.setMinimumHeight(SizeUtils.dp2px(20));

    AttributeMap map = new AttributeMap();
    map.putValue("android:layout_width", "wrap_content");
    map.putValue("android:layout_height", "wrap_content");
    editor.getViewAttributeMap().put(newView, map);

    editor.addWidget(newView, parent, event);

    try {
      Class cls = newView.getClass();
      Method setStrokeEnabled = cls.getMethod("setStrokeEnabled", boolean.class);
      Method setBlueprint = cls.getMethod("setBlueprint", boolean.class);
      setStrokeEnabled.invoke(newView, PreferencesUtils.isShowStroke());
      setBlueprint.invoke(newView, editor.isBlueprint());
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (data.containsKey(Constants.KEY_DEFAULT_ATTRS)) {
      editor.getInitializer().applyDefaultAttributes(newView, (Map) data.get(Constants.KEY_DEFAULT_ATTRS));
    }
  }
  
  public void setTransition(ViewGroup group) {
    if (!(group instanceof RecyclerView)) {
      LayoutTransition transition = new LayoutTransition();
      transition.disableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
      transition.enableTransitionType(LayoutTransition.CHANGING);
      transition.setDuration(150);
      group.setLayoutTransition(transition);
    }
  }
}
