package com.itsvks.layouteditor.editor.callers;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.itsvks.layouteditor.utils.DimensionUtil;
import com.itsvks.layouteditor.utils.Constants;

public class TextViewCaller {

  public static void setText(View target, String value, Context context) {
    
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
      result |= Constants.gravityMap.get(flag);
    }

    ((TextView) target).setGravity(result);
  }
}
