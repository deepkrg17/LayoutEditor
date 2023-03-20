package com.itsvks.layouteditor.utils;

import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import java.util.HashMap;

public class Constants {
  public static final HashMap<String, Integer> gravityMap = new HashMap<>();
  public static final HashMap<String, Integer> inputTypes = new HashMap<>();
  public static final HashMap<String, Integer> imeOptions = new HashMap<>();
  public static final HashMap<String, Integer> visibilityMap = new HashMap<>();

  public static final String ATTRIBUTES_FILE = "attributes/attributes.json";
  public static final String PARENT_ATTRIBUTES_FILE = "parent_attributes.json";
  public static final String LAYOUTS_FILE = "layouts.json";
  public static final String VIEWS_FILE = "views.json";
  public static final String ANDROIDX_WIDGETS_FILE = "androidx_widgets.json";
  public static final String MATERIAL_DESIGN_WIDGETS_FILE = "material_design_widgets.json";
  
  public static final String PALETTE_COMMON     = "palette/common.json";
  public static final String PALETTE_TEXT       = "palette/text.json";
  public static final String PALETTE_BUTTONS    = "palette/buttons.json";
  public static final String PALETTE_WIDGETS    = "palette/widgets.json";
  public static final String PALETTE_LAYOUTS    = "palette/layouts.json";
  public static final String PALETTE_CONTAINERS = "palette/containers.json";
  public static final String PALETTE_GOOGLE     = "palette/google.json";
  public static final String PALETTE_LEGACY     = "palette/legacy.json";

  public static final String TAB_TITLE_VIEWS      = "Views";
  public static final String TAB_TITLE_ANDROIDX   = "AndroidX";
  public static final String TAB_TITLE_MATERIAL   = "Material Design";
  public static final String TAB_TITLE_COMMON     = "Common";
  public static final String TAB_TITLE_TEXT       = "Text";
  public static final String TAB_TITLE_BUTTONS    = "Buttons";
  public static final String TAB_TITLE_WIDGETS    = "Widgets";
  public static final String TAB_TITLE_LAYOUTS    = "Layouts";
  public static final String TAB_TITLE_CONTAINERS = "Containers";
  public static final String TAB_TITLE_GOOGLE     = "Google";
  public static final String TAB_TITLE_LEGACY     = "Legacy";

  public static final String KEY_ATTRIBUTE_NAME = "attributeName";
  public static final String KEY_CLASS_NAME     = "className";
  public static final String KEY_METHOD_NAME    = "methodName";
  public static final String KEY_ARGUMENT_TYPE  = "argumentType";
  public static final String KEY_CAN_DELETE     = "canDelete";
  public static final String KEY_CONSTANT       = "constant";
  public static final String KEY_DEFAULT_VALUE  = "defaultValue";
  public static final String KEY_DEFAULT_ATTRS  = "defaultAttributes";

  public static final String ARGUMENT_TYPE_SIZE      = "size";
  public static final String ARGUMENT_TYPE_DIMENSION = "dimension";
  public static final String ARGUMENT_TYPE_ID        = "id";
  public static final String ARGUMENT_TYPE_VIEW      = "view";
  public static final String ARGUMENT_TYPE_BOOLEAN   = "boolean";
  public static final String ARGUMENT_TYPE_DRAWABLE  = "drawable";
  public static final String ARGUMENT_TYPE_STRING    = "string";
  public static final String ARGUMENT_TYPE_TEXT      = "text";
  public static final String ARGUMENT_TYPE_INT       = "int";
  public static final String ARGUMENT_TYPE_FLOAT     = "float";
  public static final String ARGUMENT_TYPE_FLAG      = "flag";
  public static final String ARGUMENT_TYPE_ENUM      = "enum";
  public static final String ARGUMENT_TYPE_COLOR     = "color";
  
  public static final int BLUEPRINT_DASH_COLOR = Color.WHITE;
  public static final int BLUEPRINT_BACKGROUND_COLOR = Color.parseColor("#235C6F");
  public static final int DESIGN_DASH_COLOR = Color.parseColor("#1689F6");
  public static final int DESIGN_BACKGROUND_COLOR = Color.WHITE;

  public static final String GITHUB_URL = "https://github.com/itsvks19/LayoutEditor";

  static {
    gravityMap.put("left", Gravity.START);
    gravityMap.put("right", Gravity.END);
    gravityMap.put("top", Gravity.TOP);
    gravityMap.put("bottom", Gravity.BOTTOM);
    gravityMap.put("center", Gravity.CENTER);
    gravityMap.put("center_horizontal", Gravity.CENTER_HORIZONTAL);
    gravityMap.put("center_vertical", Gravity.CENTER_VERTICAL);

    inputTypes.put("date", InputType.TYPE_DATETIME_VARIATION_DATE);
    inputTypes.put("datetime", InputType.TYPE_CLASS_DATETIME);
    inputTypes.put("none", InputType.TYPE_NULL);
    inputTypes.put("number", InputType.TYPE_CLASS_NUMBER);
    inputTypes.put("numberDecimal", InputType.TYPE_NUMBER_FLAG_DECIMAL);
    inputTypes.put("numberSigned", InputType.TYPE_NUMBER_FLAG_SIGNED);
    inputTypes.put("numberPassword", InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    inputTypes.put("phone", InputType.TYPE_CLASS_PHONE);
    inputTypes.put("text", InputType.TYPE_CLASS_TEXT);
    inputTypes.put("textAutoComplete", InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
    inputTypes.put("textAutoCorrect", InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
    inputTypes.put("textCapCharacters", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
    inputTypes.put("textCapSentences", InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    inputTypes.put("textCapWords", InputType.TYPE_TEXT_FLAG_CAP_WORDS);
    inputTypes.put("textEmailAddress", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    inputTypes.put("textEmailSubject", InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
    inputTypes.put(
        "textEnableTextConversionSuggestions",
        InputType.TYPE_TEXT_FLAG_ENABLE_TEXT_CONVERSION_SUGGESTIONS);
    inputTypes.put("textFilter", InputType.TYPE_TEXT_VARIATION_FILTER);
    inputTypes.put("textImeMultiLine", InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
    inputTypes.put("textLongMessage", InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
    inputTypes.put("textMultiLine", InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    inputTypes.put("textNoSuggestions", InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    inputTypes.put("textPassword", InputType.TYPE_TEXT_VARIATION_PASSWORD);
    inputTypes.put("textPersonName", InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    inputTypes.put("textPhonetic", InputType.TYPE_TEXT_VARIATION_PHONETIC);
    inputTypes.put("textPostalAddress", InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
    inputTypes.put("textShortMessage", InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
    inputTypes.put("textUri", InputType.TYPE_TEXT_VARIATION_URI);
    inputTypes.put("textVisiblePassword", InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    inputTypes.put("textWebEditText", InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
    inputTypes.put("textWebEmailAddress", InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
    inputTypes.put("textWebPassword", InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
    inputTypes.put("time", InputType.TYPE_DATETIME_VARIATION_TIME);

    visibilityMap.put("visible", View.VISIBLE);
    visibilityMap.put("invisible", View.INVISIBLE);
    visibilityMap.put("gone", View.GONE);
  }
}
