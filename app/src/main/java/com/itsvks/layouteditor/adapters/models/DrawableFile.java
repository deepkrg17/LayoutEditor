package com.itsvks.layouteditor.adapters.models;

import android.graphics.drawable.Drawable;
import com.itsvks.layouteditor.utils.FileUtil;
import java.io.File;

public class DrawableFile {
  public String name;
  public String path;
  public Drawable drawable;

  public DrawableFile(String name, Drawable drawable, String path) {
    this.name = name;
    this.drawable = drawable;
    this.path = path;
  }

  public void rename(String newPath, String extension) {
    File newFile = new File(newPath);
    File oldFile = new File(path);
    oldFile.renameTo(newFile);

    path = newPath;
    name = FileUtil.getLastSegmentFromPath(path);
  }
}
