package com.itsvks.layouteditor.editor;

public class APILevel {
  private Level level;

  public APILevel(Level level) {
    this.level = level;
  }

  public Level getLevel() {
    return this.level;
  }

  public enum Level {
    LOLLIPOP,          // Android 5.0 & 5.1    }
    MARSHMALLOW,       // Android 6.0          ) --> They are not usable because this app's minSdkVersion is 26
    NOUGAT,            // Android 7.0 & 7.1    }
    OREO,              // Android 8.0 & 8.1
    PIE,               // Android 9
    QUEEN_CAKE,        // Android 10
    RED_VELVET_CAKE,   // Android 11
    SNOW_CONE          // Android 12
  }
}
