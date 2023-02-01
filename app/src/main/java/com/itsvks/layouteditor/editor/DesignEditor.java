package com.itsvks.layouteditor.editor;

public class DesignEditor {
  private ViewType viewType;
  private String layoutFile;
  private DeviceConfiguration deviceConfiguration;
  private APILevel apiLevel;

  public DesignEditor() {}

  public void setViewType(ViewType viewType) {
    this.viewType = viewType;
  }

  public void setLayoutFile(String layoutFile) {
    this.layoutFile = layoutFile;
  }

  public void previewLayout(DeviceConfiguration deviceConfiguration, APILevel apiLevel) {
    this.deviceConfiguration = deviceConfiguration;
    this.apiLevel = apiLevel;
  }

  public void resizeLayout(DeviceConfiguration deviceConfiguration) {
    this.deviceConfiguration = deviceConfiguration;
  }

  public void saveLayout() {
    // Save the layout to disk
  }

  public enum ViewType {
    Design,
    Blueprint,
    Both
  }
}
