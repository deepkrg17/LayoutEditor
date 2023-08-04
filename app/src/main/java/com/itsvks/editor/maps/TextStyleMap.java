package com.itsvks.editor.maps;

import android.graphics.Typeface;

public class TextStyleMap extends BaseMap {
  
  static {
    map.put("normal", Typeface.NORMAL);
    map.put("bold", Typeface.BOLD);
    map.put("italic", Typeface.ITALIC);
    map.put("bold|italic", Typeface.BOLD_ITALIC);
  }
}
