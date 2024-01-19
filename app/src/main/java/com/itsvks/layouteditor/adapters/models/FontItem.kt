package com.itsvks.layouteditor.adapters.models;

public class FontItem {
  private String name;
  private String path;

  public FontItem(String name, String path) {
    this.name = name;
    this.path = path;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
