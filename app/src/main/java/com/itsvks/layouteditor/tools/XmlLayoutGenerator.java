package com.itsvks.layouteditor.tools;

import android.view.View;
import android.view.ViewGroup;

import com.itsvks.layouteditor.editor.initializer.AttributeMap;
import com.itsvks.layouteditor.editor.layouts.EditorLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.text.StringEscapeUtils;

public class XmlLayoutGenerator {
  StringBuilder builder = new StringBuilder();
  String TAB = "\t";

  boolean useSuperclasses;

  public String generate(EditorLayout editor, boolean useSuperclasses) {
    this.useSuperclasses = useSuperclasses;

    if (editor.getChildCount() == 0) {
      return "";
    }
    builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    builder.append(
        "<!--\n\tWelcome to LayoutEditor!\n\n"
            + "\tWe are proud to present our innovative layout generator app that\n\tallows users to create and customize stunning layouts in no time.\n"
            + "\tWith LayoutEditor, you can easily create beautiful and custom\n\tlayouts that are tailored to fit your unique needs.\n\n"
            + "\tThank you for using LayoutEditor and we hope you enjoy our app!\n-->\n\n");

    return peek(editor.getChildAt(0), editor.getViewAttributeMap(), 0);
  }

  private String peek(View view, HashMap<View, AttributeMap> attributeMap, int depth) {
    if (attributeMap == null || view == null) return "";
    String indent = getIndent(depth);
    int nextDepth = depth;

    String className =
        useSuperclasses ? view.getClass().getSuperclass().getName() : view.getClass().getName();

    if (useSuperclasses) {
      if (className.startsWith("android.widget")) {
        className = view.getClass().getSuperclass().getSimpleName();
      }
    }

    builder.append(indent + "<" + className + "\n");

    if (depth == 0) {
      builder.append(TAB + "xmlns:android=\"http://schemas.android.com/apk/res/android\"\n");
      builder.append(TAB + "xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n");
    }

    List<String> keys = attributeMap.get(view).keySet();
    List<String> values = attributeMap.get(view).values();

    for (String key : keys) {
                                          //If the value contains special characters it will be converted
      builder.append(TAB + indent + key + "=\"" + StringEscapeUtils.escapeXml11(attributeMap.get(view).getValue(key)) + "\"\n");
    }

    builder.deleteCharAt(builder.length() - 1);

    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      nextDepth++;

      if (group.getChildCount() > 0) {
        builder.append(">\n\n");

        for (int i = 0; i < group.getChildCount(); i++) {
          peek(group.getChildAt(i), attributeMap, nextDepth);
        }

        builder.append(indent + "</" + className + ">\n\n");
      } else {
        builder.append("/>\n\n");
      }
    } else {
      builder.append("/>\n\n");
    }

    return builder.toString().trim();
  }

  private String getIndent(int depth) {
    String s = "";

    for (int i = 0; i < depth; i++) {
      s += TAB;
    }

    return s;
  }
}
