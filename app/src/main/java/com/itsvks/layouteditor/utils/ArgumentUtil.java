package com.itsvks.layouteditor.utils;

import java.util.HashMap;
import java.util.regex.Pattern;

/** This class holds all utility methods related to arguments */
public class ArgumentUtil {
  /** Default color value */
  public static final String COLOR = "color";
  /** Default drawable value */
  public static final String DRAWABLE = "drawable";

  /** Map used to store the patterns of color and drawable */
  static final HashMap<String, String> patterns = new HashMap<>();

  /** The static block initializes the map with the color and drawable patterns */
  static {
    patterns.put(COLOR, "#[a-fA-F0-9]{6,8}");
    patterns.put(DRAWABLE, "@drawable/.*");
  }

  /**
   * Method to parse the type of the value from given list of variants
   *
   * @param value     The value to be parsed
   * @param variants  The list of variants from which type should be parsed
   * @return          The type of the value
   */
  public static String parseType(String value, String[] variants) {
    for (String variant : variants) {
      if (patterns.containsKey(variant))
        if (Pattern.matches(patterns.get(variant).toString(), value)) return variant;
    }

    return "string";
  }
}
