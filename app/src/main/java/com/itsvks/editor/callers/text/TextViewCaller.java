package com.itsvks.editor.callers.text;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;
import com.itsvks.editor.managers.DrawableManager;
import com.itsvks.editor.managers.ProjectManager;
import com.itsvks.editor.managers.ValuesManager;
import com.itsvks.editor.maps.GravityMap;
import com.itsvks.editor.maps.TextStyleMap;
import com.itsvks.editor.models.Project;
import com.itsvks.editor.Constants;
import com.itsvks.editor.parser.ValuesResourceParser;
import com.itsvks.editor.utils.DimensionUtil;

public class TextViewCaller {
  public static void setText(View target, String value, Context context) {
    if (value.startsWith("@string/")) {
      Project project = ProjectManager.getInstance().getOpenedProject();

      value =
          ValuesManager.getValueFromResources(
              ValuesResourceParser.TAG_STRING, value, project.getStringsPath());
    }
    ((TextView) target).setText(value);
  }

  public static void setTextSize(View target, String value, Context context) {
    ((TextView) target).setTextSize(DimensionUtil.parse(value, context));
  }

  public static void setTextColor(View target, String value, Context context) {
    ((TextView) target).setTextColor(Color.parseColor(value));
  }

  public static void setGravity(View target, String value, Context context) {
    String[] flags = value.split("\\|");
    int result = 0;

    for (String flag : flags) {
      result |= GravityMap.get(flag);
    }

    ((TextView) target).setGravity(result);
  }

  public static void setCheckMark(View target, String value, Context context) {
    String name = value.replace("@drawable/", "");
    if (target instanceof CheckedTextView)
      ((CheckedTextView) target).setCheckMarkDrawable(DrawableManager.getDrawable(context, name));
  }

  public static void setChecked(View target, String value, Context context) {
    if (target instanceof CheckedTextView) {
      if (value.equals("true")) ((CheckedTextView) target).setChecked(true);
      else if (value.equals("false")) ((CheckedTextView) target).setChecked(false);
    }
  }

  public static void setTextStyle(View target, String value, Context context) {
    ((TextView) target).setTypeface(null, TextStyleMap.get(value));
  }
}
