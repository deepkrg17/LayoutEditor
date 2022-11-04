package com.itsvks.layouteditor.editor.callers;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.itsvks.layouteditor.utils.Constants;
import com.itsvks.layouteditor.utils.DimensionUtil;

import java.util.HashMap;

public class LinearLayoutCaller {
	
	private static final HashMap<String, Integer> orientationMap = new HashMap<>();
	
	static {
		orientationMap.put("horizontal", LinearLayout.HORIZONTAL);
		orientationMap.put("vertical", LinearLayout.VERTICAL);
	}
	
	public static void setOrientation(View target, String value, Context context) {
		((LinearLayout) target).setOrientation(orientationMap.get(value));
	}
	
	public static void setGravity(View target, String value, Context context) {
		String[] flags = value.split("\\|");
		int result = 0;

		for(String flag : flags) {
			result |= Constants.gravityMap.get(flag);
		}

		((LinearLayout) target).setGravity(result);
	}
	
	public static void setLayoutWeight(View target, String value, Context context) {
		((LinearLayout.LayoutParams) target.getLayoutParams()).weight = Float.valueOf(value);
		target.requestLayout();
	}
	
	public static void setLayoutMargin(View target, String value, Context context) {
		int margin = (int) DimensionUtil.parse(value, context);
		((LinearLayout.LayoutParams) target.getLayoutParams()).setMargins(margin, margin, margin, margin);
		target.requestLayout();
	}

	public static void setLayoutMarginLeft(View target, String value, Context context) {
		int margin = (int) DimensionUtil.parse(value, context);
		((LinearLayout.LayoutParams) target.getLayoutParams()).leftMargin = margin;
		target.requestLayout();
	}

	public static void setLayoutMarginRight(View target, String value, Context context) {
		int margin = (int) DimensionUtil.parse(value, context);
		((LinearLayout.LayoutParams) target.getLayoutParams()).rightMargin = margin;
		target.requestLayout();
	}

	public static void setLayoutMarginTop(View target, String value, Context context) {
		int margin = (int) DimensionUtil.parse(value, context);
		((LinearLayout.LayoutParams) target.getLayoutParams()).topMargin = margin;
		target.requestLayout();
	}

	public static void setLayoutMarginBottom(View target, String value, Context context) {
		int margin = (int) DimensionUtil.parse(value, context);
		((LinearLayout.LayoutParams) target.getLayoutParams()).bottomMargin = margin;
		target.requestLayout();
	}
}