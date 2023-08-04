package com.itsvks.editor.maps;

import java.util.HashMap;

public class BaseMap {
  public static final HashMap<String, Integer> map = new HashMap<>();

  public static int get(String key) {
    return map.get(key);
  }
}
