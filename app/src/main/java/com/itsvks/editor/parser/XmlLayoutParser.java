package com.itsvks.editor.parser;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.LinearLayoutCompat;
import com.blankj.utilcode.util.ResourceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itsvks.editor.Constants;
import com.itsvks.editor.designeditor.DesignEditor;
import com.itsvks.editor.designeditor.initializer.AttributeInitializer;
import com.itsvks.editor.designeditor.initializer.AttributeMap;
import com.itsvks.editor.managers.IdManager;
import com.itsvks.editor.utils.FileUtil;
import com.itsvks.editor.utils.InvokeUtil;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XmlLayoutParser {

  private HashMap<String, List<HashMap<String, Object>>> attributes;
  private HashMap<String, List<HashMap<String, Object>>> parentAttributes;

  private HashMap<View, AttributeMap> viewAttributeMap = new HashMap<>();
  private AttributeInitializer initializer;

  private LinearLayoutCompat container;
  private Context context;

  public XmlLayoutParser(Context context) {
    this.context = context;
    initializeAttributes();
    container = new LinearLayoutCompat(context);
    container.setLayoutParams(
        new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
  }

  private void initializeAttributes() {
    attributes = readAttributesFromAssets(Constants.ATTRIBUTES_FILE);
    parentAttributes = readAttributesFromAssets(Constants.PARENT_ATTRIBUTES_FILE);
    initializer = new AttributeInitializer(attributes, parentAttributes);
  }

  private HashMap<String, List<HashMap<String, Object>>> readAttributesFromAssets(String fileName) {
    String json = ResourceUtils.readAssets2String(fileName);
    return new Gson()
        .fromJson(
            json, new TypeToken<HashMap<String, List<HashMap<String, Object>>>>() {}.getType());
  }

  public View getRoot() {
    View view = container.getChildAt(0);
    container.removeView(view);
    return view;
  }

  public HashMap<View, AttributeMap> getViewAttributeMap() {
    return viewAttributeMap;
  }

  public void parseFromXml(final String xml) {
    List<View> listViews = new ArrayList<>();
    listViews.add(container);

    try {
      final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
      final XmlPullParser parser = factory.newPullParser();
      parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
      parser.setInput(new StringReader(xml));

      while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
        switch (parser.getEventType()) {
          case XmlPullParser.START_TAG:
            {
              View view = (View) InvokeUtil.createView(parser.getName(), context);
              listViews.add(view);

              AttributeMap map = new AttributeMap();

              for (int i = 0; i < parser.getAttributeCount(); i++) {
                if (!parser.getAttributeName(i).startsWith("xmlns")) {
                  map.putValue(parser.getAttributeName(i), parser.getAttributeValue(i));
                }
              }

              viewAttributeMap.put(view, map);
              break;
            }

          case XmlPullParser.END_TAG:
            {
              int index = parser.getDepth();
              ((ViewGroup) listViews.get(index - 1)).addView(listViews.get(index));
              listViews.remove(index);
              break;
            }
        }

        parser.next();
      }
    } catch (XmlPullParserException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    IdManager.clear();

    for (View view : viewAttributeMap.keySet()) {
      AttributeMap map = viewAttributeMap.get(view);

      for (String key : map.keySet()) {
        if (key.equals("android:id")) {
          IdManager.addNewId(view, map.getValue("android:id"));
        }
      }
    }

    for (View view : viewAttributeMap.keySet()) {
      AttributeMap map = viewAttributeMap.get(view);
      applyAttributes(view, map);
    }
  }

  private void applyAttributes(View target, AttributeMap attributeMap) {
    final List<HashMap<String, Object>> allAttrs = initializer.getAllAttributesForView(target);

    final List<String> keys = attributeMap.keySet();
    final List<String> values = attributeMap.values();

    for (int i = keys.size() - 1; i >= 0; i--) {
      String key = keys.get(i);

      HashMap<String, Object> attr = initializer.getAttributeFromKey(key, allAttrs);
      if (attr == null) return;
      String methodName = attr.get(Constants.KEY_METHOD_NAME).toString();
      String className = attr.get(Constants.KEY_CLASS_NAME).toString();
      String value = attributeMap.getValue(key);

      if (key.equals("android:id")) {
        continue;
      }

      InvokeUtil.invokeMethod(methodName, className, target, value);
    }
  }
}
