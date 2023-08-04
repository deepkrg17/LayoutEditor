package com.itsvks.editor.maps;

import android.view.View;

public class VisibilityMap extends BaseMap {
  
  static {
    map.put("visible", View.VISIBLE);
    map.put("invisible", View.INVISIBLE);
    map.put("gone", View.GONE);
  }
}
