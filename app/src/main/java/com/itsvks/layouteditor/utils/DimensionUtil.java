package com.itsvks.layouteditor.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.itsvks.layouteditor.LayoutEditor;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DimensionUtil {
    public static final String DP = "dp";
    public static final String SP = "sp";

    private static final HashMap<String, Integer> dimensMap = new HashMap<>();

    static {
        dimensMap.put(DP, TypedValue.COMPLEX_UNIT_DIP);
        dimensMap.put(SP, TypedValue.COMPLEX_UNIT_SP);
    }

    private static final Pattern pattern = Pattern.compile("dp|sp");

    public static float parse(String input, Context contxt) {
        if (input.equals("match_parent")) return ViewGroup.LayoutParams.MATCH_PARENT;
        else if (input.equals("wrap_content")) return ViewGroup.LayoutParams.WRAP_CONTENT;
        else {
            Matcher matcher = pattern.matcher(input);
            String dimen = DP;

            while (matcher.find()) {
                dimen = input.substring(matcher.start(), matcher.end());
            }

            float number = Float.valueOf(input.substring(0, input.lastIndexOf(dimen)));
            return TypedValue.applyDimension(
                    dimensMap.get(dimen), number, contxt.getResources().getDisplayMetrics());
        }
    }

    public static String getDimenWithoutSuffix(String input) {
        Matcher matcher = pattern.matcher(input);
        String dimen = DP;

        while (matcher.find()) {
            dimen = input.substring(matcher.start(), matcher.end());
        }

        return input.substring(0, input.lastIndexOf(dimen));
    }

    public static float getDip(float value, Context ctx) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, ctx.getResources().getDisplayMetrics());
    }
}
