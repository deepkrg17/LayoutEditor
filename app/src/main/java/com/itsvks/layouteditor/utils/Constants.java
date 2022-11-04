package com.itsvks.layouteditor.utils;

import android.view.Gravity;

import java.util.HashMap;

public class Constants {
    public static final HashMap<String, Integer> gravityMap = new HashMap<>();
    public static final String ATTRIBUTES = "attributes.json";
    public static final String PARENT_ATTRIBUTES = "parent_attributes.json";
    public static final String LAYOUTS = "layouts.json";
    public static final String VIEWS = "views.json";
    
    static {
        gravityMap.put("left", Gravity.START);
        gravityMap.put("right", Gravity.END);
        gravityMap.put("top", Gravity.TOP);
        gravityMap.put("bottom", Gravity.BOTTOM);
        gravityMap.put("center", Gravity.CENTER);
        gravityMap.put("center_horizontal", Gravity.CENTER_HORIZONTAL);
        gravityMap.put("center_vertical", Gravity.CENTER_VERTICAL);
    }
    
    
}
