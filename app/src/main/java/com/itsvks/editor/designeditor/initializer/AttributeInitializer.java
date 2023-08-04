package com.itsvks.editor.designeditor.initializer;

import android.view.View;
import com.itsvks.editor.Constants;
import com.itsvks.editor.designeditor.DesignEditor;
import com.itsvks.editor.utils.InvokeUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeInitializer {
  private HashMap<View, AttributeMap> viewAttributeMap = new HashMap<>();
  private HashMap<String, List<HashMap<String, Object>>> attributes;
  private HashMap<String, List<HashMap<String, Object>>> parentAttributes;

  public AttributeInitializer(
      HashMap<String, List<HashMap<String, Object>>> attributes,
      HashMap<String, List<HashMap<String, Object>>> parentAttributes) {
    this.attributes = attributes;
    this.parentAttributes = parentAttributes;
  }

  public AttributeInitializer(
      HashMap<View, AttributeMap> viewAttributeMap,
      HashMap<String, List<HashMap<String, Object>>> attributes,
      HashMap<String, List<HashMap<String, Object>>> parentAttributes) {
    this.viewAttributeMap = viewAttributeMap;
    this.attributes = attributes;
    this.parentAttributes = parentAttributes;
  }

  public void applyDefaultAttributes(final View target, final Map<String, String> defaultAttrs) {
    List<HashMap<String, Object>> allAttrs = getAllAttributesForView(target);

    for (String key : defaultAttrs.keySet()) {
      for (HashMap<String, Object> map : allAttrs) {
        if (map.get(Constants.KEY_ATTRIBUTE_NAME).toString().equals(key)) {
          applyAttribute(target, defaultAttrs.get(key).toString(), map);
          break;
        }
      }
    }
  }

  public void applyAttribute(
      final View target, final String value, final HashMap<String, Object> attribute) {
    String methodName = attribute.get(Constants.KEY_METHOD_NAME).toString();
    String className = attribute.get(Constants.KEY_CLASS_NAME).toString();
    String attributeName = attribute.get(Constants.KEY_ATTRIBUTE_NAME).toString();

    // update ids attributes for all views
    if (value.startsWith("@+id/") && viewAttributeMap.get(target).contains("android:id")) {
      for (View view : viewAttributeMap.keySet()) {
        AttributeMap map = viewAttributeMap.get(view);

        for (String key : map.keySet()) {
          String val = map.getValue(key);

          if (val.startsWith("@id/")
              && val.equals(viewAttributeMap.get(target).getValue("android:id").replace("+", ""))) {
            map.putValue(key, value.replace("+", ""));
          }
        }
      }
    }

    viewAttributeMap.get(target).putValue(attributeName, value);
    InvokeUtil.invokeMethod(methodName, className, target, value);
  }

  public List<HashMap<String, Object>> getAvailableAttributesForView(final View target) {
    final List<String> keys = viewAttributeMap.get(target).keySet();
    final List<HashMap<String, Object>> allAttrs = getAllAttributesForView(target);

    for (int i = allAttrs.size() - 1; i >= 0; i--) {
      for (String key : keys) {
        if (key.equals(allAttrs.get(i).get(Constants.KEY_ATTRIBUTE_NAME).toString())) {
          allAttrs.remove(i);
          break;
        }
      }
    }

    return allAttrs;
  }

  public List<HashMap<String, Object>> getAllAttributesForView(final View target) {
    List<HashMap<String, Object>> allAttrs = new ArrayList<>();

    Class cls = target.getClass();
    Class viewParentCls = View.class.getSuperclass();

    while (cls != viewParentCls) {
      if (attributes.containsKey(cls.getName())) allAttrs.addAll(0, attributes.get(cls.getName()));

      cls = cls.getSuperclass();
    }

    if (target.getParent() != null && target.getParent().getClass() != DesignEditor.class) {
      cls = target.getParent().getClass();

      while (cls != viewParentCls) {
        if (parentAttributes.containsKey(cls.getName()))
          allAttrs.addAll(parentAttributes.get(cls.getName()));

        cls = cls.getSuperclass();
      }
    }

    return allAttrs;
  }

  public HashMap<String, Object> getAttributeFromKey(
      String key, List<HashMap<String, Object>> list) {
    for (HashMap<String, Object> map : list) {
      if (map.get(Constants.KEY_ATTRIBUTE_NAME).equals(key)) return map;
    }

    return null;
  }
}
