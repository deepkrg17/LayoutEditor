package com.itsvks.layouteditor.adapters.models;

import android.graphics.drawable.Drawable;
import com.itsvks.layouteditor.utils.FileUtil;
import java.io.File;

public class DrawableFile {
  private String name;
  private String path;
  private Drawable drawable;
  private int versions;

  public DrawableFile(int versions, Drawable drawable, String path) {
    this.name = FileUtil.getLastSegmentFromPath(path);
    this.drawable = drawable;
    this.path = path;
    this.versions = versions;
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

  public Drawable getDrawable() {
    return this.drawable;
  }

  public void setDrawable(Drawable drawable) {
    this.drawable = drawable;
  }

  public int getVersions() {
    return this.versions;
  }

  public void setVersions(int versions) {
    this.versions = versions;
  }
}
