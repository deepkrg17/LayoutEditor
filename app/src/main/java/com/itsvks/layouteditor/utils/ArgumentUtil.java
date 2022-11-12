package com.itsvks.layouteditor.utils;

import java.util.HashMap;
import java.util.regex.Pattern;

public class ArgumentUtil {
    public static final String COLOR = "color";
    public static final String DRAWABLE = "drawable";

    static final HashMap<String, String> patterns = new HashMap<>();

    static {
        patterns.put(COLOR, "#[a-fA-F0-9]{6,8}");
        patterns.put(DRAWABLE, "@drawable/.*");
    }

    public static String parseType(String value, String[] variants) {
        for (String variant : variants) {
            if (patterns.containsKey(variant))
                if (Pattern.matches(patterns.get(variant).toString(), value)) return variant;
        }

        return "string";
    }
}
