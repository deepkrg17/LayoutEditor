package com.itsvks.layouteditor.editor;

import android.content.Context;
import android.view.View;

import com.itsvks.layouteditor.editor.EditorLayout;
import com.itsvks.layouteditor.utils.InvokeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttributeInitializer {
    private Context context;

    private HashMap<View, AttributeMap> viewAttributeMap = new HashMap<>();

    private HashMap<String, ArrayList<HashMap<String, Object>>> attributes;
    private HashMap<String, ArrayList<HashMap<String, Object>>> parentAttributes;

    private View rootView;

    public AttributeInitializer(
            final Context context,
            final HashMap<String, ArrayList<HashMap<String, Object>>> attributes,
            final HashMap<String, ArrayList<HashMap<String, Object>>> parentAttributes) {
        this.context = context;
        this.attributes = attributes;
        this.parentAttributes = parentAttributes;
    }

    public AttributeInitializer(
            final Context context,
            final HashMap<View, AttributeMap> viewAttributeMap,
            final HashMap<String, ArrayList<HashMap<String, Object>>> attributes,
            final HashMap<String, ArrayList<HashMap<String, Object>>> parentAttributes) {
        this.context = context;
        this.viewAttributeMap = viewAttributeMap;
        this.attributes = attributes;
        this.parentAttributes = parentAttributes;
    }

    public HashMap<View, AttributeMap> getViewAttributeMap() {
        return viewAttributeMap;
    }

    public View getRootView() {
        return rootView;
    }

    public void applyDefaultAttributes(final View target, final Map<String, String> defaultAttrs) {
        ArrayList<HashMap<String, Object>> allAttrs = getAllAttributesForView(target);

        for (String key : defaultAttrs.keySet()) {
            for (HashMap<String, Object> map : allAttrs) {
                if (map.get("attributeName").toString().equals(key)) {
                    applyAttribute(target, defaultAttrs.get(key).toString(), map);
                    break;
                }
            }
        }
    }

    public void applyAttribute(
            final View target, final String value, final HashMap<String, Object> attribute) {
        String methodName = attribute.get("methodName").toString();
        String className = attribute.get("className").toString();
        String attributeName = attribute.get("attributeName").toString();

        // update ids attributes for all views
        if (value.startsWith("@+id/") && viewAttributeMap.get(target).contains("android:id")) {
            for (View view : viewAttributeMap.keySet()) {
                AttributeMap map = viewAttributeMap.get(view);

                for (String key : map.keySet()) {
                    String val = map.getValue(key);

                    if (val.startsWith("@id/")
                            && val.equals(
                                    viewAttributeMap
                                            .get(target)
                                            .getValue("android:id")
                                            .replace("+", ""))) {
                        map.putValue(key, value.replace("+", ""));
                    }
                }
            }
        }

        viewAttributeMap.get(target).putValue(attributeName, value);
        InvokeUtil.invokeMethod(methodName, className, target, value, context);
    }

    public ArrayList<HashMap<String, Object>> getAvailableAttributesForView(final View target) {
        final ArrayList<String> keys = viewAttributeMap.get(target).keySet();
        final ArrayList<HashMap<String, Object>> allAttrs = getAllAttributesForView(target);

        for (int i = allAttrs.size() - 1; i >= 0; i--) {
            for (String key : keys) {
                if (key.equals(allAttrs.get(i).get("attributeName").toString())) {
                    allAttrs.remove(i);
                    break;
                }
            }
        }

        return allAttrs;
    }

    public ArrayList<HashMap<String, Object>> getAllAttributesForView(final View target) {
        ArrayList<HashMap<String, Object>> allAttrs = new ArrayList<>();

        Class cls = target.getClass();
        Class viewParentCls = View.class.getSuperclass();

        while (cls != viewParentCls) {
            if (attributes.containsKey(cls.getName())) {
                allAttrs.addAll(0, attributes.get(cls.getName()));
            }

            cls = cls.getSuperclass();
        }

        if (target.getParent() != null && target.getParent().getClass() != EditorLayout.class) {
            cls = target.getParent().getClass();

            while (cls != viewParentCls) {
                if (parentAttributes.containsKey(cls.getName())) {
                    allAttrs.addAll(parentAttributes.get(cls.getName()));
                }

                cls = cls.getSuperclass();
            }
        }

        return allAttrs;
    }

    public HashMap<String, Object> getAttributeFromKey(
            String key, ArrayList<HashMap<String, Object>> list) {
        for (HashMap<String, Object> map : list) {
            if (map.get("attributeName").equals(key)) {
                return map;
            }
        }

        return null;
    }
}
