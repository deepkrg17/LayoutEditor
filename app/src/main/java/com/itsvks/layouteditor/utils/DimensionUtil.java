package com.itsvks.layouteditor.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.itsvks.layouteditor.LayoutEditor;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* This class is used to perform Dimension Utility operations.
*/
public class DimensionUtil {
  
  /**
  * Constant variable for Dimension Unit type DP
  */
  public static final String DP = "dp";

  /**
  * Constant variable for Dimension Unit type SP
  */
  public static final String SP = "sp";

  /**
  * A map containing dimension unit type and its related integer value
  */
  private static final HashMap<String, Integer> dimensMap = new HashMap<>();

  // Initializing dimensMap with Dimension Unit type and its related integer value
  static {
    dimensMap.put(DP, TypedValue.COMPLEX_UNIT_DIP);
    dimensMap.put(SP, TypedValue.COMPLEX_UNIT_SP);
  }

  /**
  * Pattern for matching Dimension Unit type
  */
  private static final Pattern pattern = Pattern.compile("dp|sp");

  /**
  * Method to parse the input string and return the related dimension value
  *
  * @param input string for parsing
  * @param contxt context
  * @return dimension value
  */
  public static float parse(String input, Context contxt) {
    if (input.equals("match_parent")) return ViewGroup.LayoutParams.MATCH_PARENT;
    else if (input.equals("wrap_content")) return ViewGroup.LayoutParams.WRAP_CONTENT;
    else {
      Matcher matcher = pattern.matcher(input);
      String dimen = DP;

      // Finding dimension unit type from input string
      while (matcher.find()) {
        dimen = input.substring(matcher.start(), matcher.end());
      }

      // Getting dimension number from input string
      float number = Float.valueOf(input.substring(0, input.lastIndexOf(dimen)));
      
      // Returning calculated dimension value
      return TypedValue.applyDimension(
          dimensMap.get(dimen), number, contxt.getResources().getDisplayMetrics());
    }
  }

  /**
  * Method to get the dimension value without the suffix, i.e Dimension Unit type
  *
  * @param input string for parsing
  * @return dimension value without suffix
  */
  public static String getDimenWithoutSuffix(String input) {
    Matcher matcher = pattern.matcher(input);
    String dimen = DP;

    // Finding dimension unit type from input string
    while (matcher.find()) {
      dimen = input.substring(matcher.start(), matcher.end());
    }

    // Getting dimension number from input string
    return input.substring(0, input.lastIndexOf(dimen));
  }

  /**
  * Method to get the dimension value in DIP
  *
  * @param value dimension number
  * @param ctx context
  * @return dimension value in DIP
  */
  public static float getDip(float value, Context ctx) {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value, ctx.getResources().getDisplayMetrics());
  }
}