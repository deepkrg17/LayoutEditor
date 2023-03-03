package com.itsvks.layouteditor.managers;

import com.itsvks.layouteditor.adapters.models.ValuesItem;
import com.itsvks.layouteditor.tools.ValuesResourceParser;
import com.itsvks.layouteditor.utils.SBUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ValuesManager {

  public static String getValueFromResources(String tag, String value, String path) {
    String resValueName = value.substring(value.indexOf("/") + 1);
    String result = value;
    try {
      ValuesResourceParser parser = new ValuesResourceParser(new FileInputStream(path), tag);

      for (ValuesItem item : parser.getValuesList()) {
        if (item.name.equals(resValueName)) {
          result = item.value;
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return result;
  }
}
