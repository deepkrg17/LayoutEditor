package com.itsvks.editor.parser;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import com.blankj.utilcode.util.ResourceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itsvks.editor.Constants;
import com.itsvks.editor.designeditor.initializer.AttributeInitializer;
import com.itsvks.editor.designeditor.initializer.AttributeMap;
import com.itsvks.editor.managers.IdManager;
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

public class XmlParser {

  private HashMap<String, List<HashMap<String, Object>>> attributes;
  private HashMap<String, List<HashMap<String, Object>>> parentAttributes;

  private HashMap<View, AttributeMap> viewAttributeMap;
  private AttributeInitializer initializer;

  private Context context;
  private LinearLayout container;

  public XmlParser(Context context, String xml) {
    this.context = context;
    this.viewAttributeMap = new HashMap<>();
    initializeAttributes();
    container = new LinearLayout(context);
    container.setLayoutParams(
        new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    try {
      parse(xml);
    } catch (XmlPullParserException | IOException err) {
      err.printStackTrace();
    }
  }

  public View getRoot() {
    View view = container.getChildAt(0);
    container.removeView(view);
    return view;
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

  private void parse(String xml) throws XmlPullParserException, IOException {
    final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    final XmlPullParser xpp = factory.newPullParser();
    xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
    xpp.setInput(new StringReader(xml));

    List<View> viewStack = new ArrayList<>();
    viewStack.add(container);

    int eventType = xpp.getEventType();
    while (eventType != XmlPullParser.END_DOCUMENT) {
      if (eventType == XmlPullParser.START_TAG) {
        View view = (View) InvokeUtil.createView(xpp.getName(), context);
        viewStack.add(view);

        AttributeMap map = new AttributeMap();
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
          if (!xpp.getAttributeName(i).startsWith("xmlns")) {
            map.putValue(xpp.getAttributeName(i), xpp.getAttributeValue(i));
          }
        }
        viewAttributeMap.put(view, map);
      } else if (eventType == XmlPullParser.END_TAG) {
        int index = xpp.getDepth();
        if (viewStack.get(index - 1) != null
            && viewStack.get(index) != null
            && viewStack.get(index - 1) instanceof ViewGroup) {
          ((ViewGroup) viewStack.get(index - 1)).addView(viewStack.get(index));
          viewStack.remove(index);
        }
      }
      eventType = xpp.next();
    }

    IdManager.clear();
    for (View view : viewAttributeMap.keySet()) {
      var map = viewAttributeMap.get(view);
      for (String key : map.keySet()) {
        if (key.equals("android:id")) {
          IdManager.addNewId(view, map.getValue("android:id"));
        }
      }
    }

    for (View view : viewAttributeMap.keySet()) {
      applyAttributes(view, viewAttributeMap.get(view));
    }
  }

  private void applyAttributes(View view, AttributeMap map) {
    var allAttrs = initializer.getAllAttributesForView(view);

    var keys = map.keySet();
    var values = map.values();
    for (int i = keys.size() - 1; i >= 0; i--) {
      String key = keys.get(i);
      var attribute = initializer.getAttributeFromKey(key, allAttrs);

      if (attribute != null) {
        String methodName = attribute.get(Constants.KEY_METHOD_NAME).toString();
        String className = attribute.get(Constants.KEY_CLASS_NAME).toString();

        String value = map.getValue(key);
        if (key.equals("android:id")) {
          continue;
        }
        InvokeUtil.invokeMethod(methodName, className, view, value);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public View createView(String viewName, Context context) {
    View view = null;

    try {
      Class<? extends View> clazz = (Class<? extends View>) Class.forName(viewName);
      Constructor<? extends View> constructor = clazz.getConstructor(Context.class);
      view = constructor.newInstance(context);
    } catch (ClassNotFoundException
        | NoSuchMethodException
        | IllegalAccessException
        | InstantiationException
        | InvocationTargetException e) {
      e.printStackTrace();
    }

    return view;
  }

  public HashMap<View, AttributeMap> getViewAttributeMap() {
    return this.viewAttributeMap;
  }

  public void setViewAttributeMap(HashMap<View, AttributeMap> viewAttributeMap) {
    this.viewAttributeMap = viewAttributeMap;
  }
}
