package com.itsvks.editor.maps;

import android.view.Gravity;

public class GravityMap extends BaseMap {

  static {
    map.put("left", Gravity.LEFT);
    map.put("right", Gravity.RIGHT);
    map.put("top", Gravity.TOP);
    map.put("bottom", Gravity.BOTTOM);
    map.put("center", Gravity.CENTER);
    map.put("center_horizontal", Gravity.CENTER_HORIZONTAL);
    map.put("center_vertical", Gravity.CENTER_VERTICAL);
    map.put("fill", Gravity.FILL);
    map.put("fill_vertical", Gravity.FILL_VERTICAL);
    map.put("fill_horizontal", Gravity.FILL_HORIZONTAL);
    map.put("start", Gravity.START);
    map.put("end", Gravity.END);
  }
}
