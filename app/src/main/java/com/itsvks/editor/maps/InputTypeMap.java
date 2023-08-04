package com.itsvks.editor.maps;

import android.text.InputType;

public class InputTypeMap extends BaseMap {

  static {
    map.put("date", InputType.TYPE_DATETIME_VARIATION_DATE);
    map.put("datetime", InputType.TYPE_CLASS_DATETIME);
    map.put("none", InputType.TYPE_NULL);
    map.put("number", InputType.TYPE_CLASS_NUMBER);
    map.put("numberDecimal", InputType.TYPE_NUMBER_FLAG_DECIMAL);
    map.put("numberSigned", InputType.TYPE_NUMBER_FLAG_SIGNED);
    map.put("numberPassword", InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    map.put("phone", InputType.TYPE_CLASS_PHONE);
    map.put("text", InputType.TYPE_CLASS_TEXT);
    map.put("textAutoComplete", InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
    map.put("textAutoCorrect", InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
    map.put("textCapCharacters", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
    map.put("textCapSentences", InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    map.put("textCapWords", InputType.TYPE_TEXT_FLAG_CAP_WORDS);
    map.put("textEmailAddress", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    map.put("textEmailSubject", InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
    map.put(
        "textEnableTextConversionSuggestions",
        InputType.TYPE_TEXT_FLAG_ENABLE_TEXT_CONVERSION_SUGGESTIONS);
    map.put("textFilter", InputType.TYPE_TEXT_VARIATION_FILTER);
    map.put("textImeMultiLine", InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
    map.put("textLongMessage", InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
    map.put("textMultiLine", InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    map.put("textNoSuggestions", InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    map.put("textPassword", InputType.TYPE_TEXT_VARIATION_PASSWORD);
    map.put("textPersonName", InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    map.put("textPhonetic", InputType.TYPE_TEXT_VARIATION_PHONETIC);
    map.put("textPostalAddress", InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
    map.put("textShortMessage", InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
    map.put("textUri", InputType.TYPE_TEXT_VARIATION_URI);
    map.put("textVisiblePassword", InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    map.put("textWebEditText", InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
    map.put("textWebEmailAddress", InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
    map.put("textWebPassword", InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
    map.put("time", InputType.TYPE_DATETIME_VARIATION_TIME);
  }
}
