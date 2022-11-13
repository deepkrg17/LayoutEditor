package com.itsvks.layouteditor.tools;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itsvks.layouteditor.editor.initializer.AttributeInitializer;
import com.itsvks.layouteditor.editor.initializer.AttributeMap;
import com.itsvks.layouteditor.managers.IdManager;
import com.itsvks.layouteditor.utils.Constants;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.InvokeUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XmlLayoutParser {
    
    private HashMap<String, List<HashMap<String, Object>>> attributes;
    private HashMap<String, List<HashMap<String, Object>>> parentAttributes;

    private HashMap<View, AttributeMap> viewAttributeMap = new HashMap<>();
    private AttributeInitializer initializer;

    private LinearLayoutCompat container;

    public XmlLayoutParser(Context context) {
        
        attributes =
                new Gson()
                        .fromJson(
                                FileUtil.readFromAsset(Constants.ATTRIBUTES, context),
                                new TypeToken<
                                        HashMap<
                                                String,
                                                List<HashMap<String, Object>>>>() {}.getType());
        parentAttributes =
                new Gson()
                        .fromJson(
                                FileUtil.readFromAsset(Constants.PARENT_ATTRIBUTES, context),
                                new TypeToken<
                                        HashMap<
                                                String,
                                                List<HashMap<String, Object>>>>() {}.getType());

        initializer = new AttributeInitializer(context, attributes, parentAttributes);

        container = new LinearLayoutCompat(context);
        container.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public View getRoot() {
        View view = container.getChildAt(0);
        container.removeView(view);
        return view;
    }

    public HashMap<View, AttributeMap> getViewAttributeMap() {
        return viewAttributeMap;
    }

    public void parseFromXml(final String xml, Context context) {
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
                                    map.putValue(
                                            parser.getAttributeName(i),
                                            parser.getAttributeValue(i));
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

    private void applyAttributes( View target, AttributeMap attributeMap) {
        final List<HashMap<String, Object>> allAttrs = initializer.getAllAttributesForView(target);

        final List<String> keys = attributeMap.keySet();
        final List<String> values = attributeMap.values();

        for (int i = keys.size() - 1; i >= 0; i--) {
            String key = keys.get(i);

            HashMap<String, Object> attr = initializer.getAttributeFromKey(key, allAttrs);
            String methodName = attr.get("methodName").toString();
            String className = attr.get("className").toString();
            String value = attributeMap.getValue(key);

            if (key.equals("android:id")) {
                continue;
            }

            InvokeUtil.invokeMethod(methodName, className, target, value, target.getContext());
        }
    }
}
