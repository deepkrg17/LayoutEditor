package com.itsvks.layouteditor.editor;

public class DeviceConfiguration {
  private DeviceSize size;

  public DeviceConfiguration(DeviceSize size) {
    this.size = size;
  }

  public DeviceSize getSize() {
    return this.size;
  }

  public enum DeviceSize {
    Small,
    Medium,
    Large
  }
}
