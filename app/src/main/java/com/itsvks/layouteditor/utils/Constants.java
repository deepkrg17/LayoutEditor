package com.itsvks.layouteditor.utils;

import android.view.Gravity;

import java.util.HashMap;

public class Constants {
    public static final HashMap<String, Integer> gravityMap = new HashMap<>();
    public static final String ATTRIBUTES = "attributes.json";
    public static final String PARENT_ATTRIBUTES = "parent_attributes.json";
    public static final String LAYOUTS = "layouts.json";
    public static final String VIEWS = "views.json";
    
    public static final String KEY_ATTRIBUTE_NAME = "attributeName";
    public static final String KEY_CLASS_NAME = "className";
    public static final String KEY_METHOD_NAME = "methodName";
    public static final String KEY_ARGUMENT_TYPE = "argumentType";
    public static final String KEY_CAN_DELETE = "canDelete";
    public static final String KEY_CONSTANT = "constant";
    public static final String KEY_DEFAULT_VALUE = "defaultValue";
    public static final String KEY_DEFAULT_ATTRS = "defaultAttributes";
    
    public static final String ARGUMENT_TYPE_SIZE = "size";
    public static final String ARGUMENT_TYPE_DIMENSION = "dimension";
    public static final String ARGUMENT_TYPE_ID = "id";
    public static final String ARGUMENT_TYPE_VIEW = "view";
    public static final String ARGUMENT_TYPE_BOOLEAN = "boolean";
    public static final String ARGUMENT_TYPE_DRAWABLE = "drawable";
    public static final String ARGUMENT_TYPE_STRING = "string";
    public static final String ARGUMENT_TYPE_INT = "int";
    public static final String ARGUMENT_TYPE_FLOAT = "float";
    public static final String ARGUMENT_TYPE_FLAG = "flag";
    public static final String ARGUMENT_TYPE_ENUM = "enum";
    public static final String ARGUMENT_TYPE_COLOR = "color";
    
    public static final String REPO_URL = "https://github.com/itsvks19/LayoutEditor/";
    
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
