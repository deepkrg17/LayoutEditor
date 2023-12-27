package com.itsvks.layouteditor;

import android.os.Parcel;
import android.os.Parcelable;
import com.itsvks.layouteditor.utils.FileUtil;
import java.io.File;

public class LayoutFile implements Parcelable {
  private String path;
  public String name;

  public LayoutFile(String path) {
    this.path = path;
    this.name = FileUtil.getLastSegmentFromPath(path);
  }

  public void rename(String newPath) {
    File newFile = new File(newPath);
    File oldFile = new File(getPath());
    oldFile.renameTo(newFile);

    path = newPath;
    name = FileUtil.getLastSegmentFromPath(path);
  }

  public void saveLayout(String text) {
    File file = new File(path);

    FileUtil.writeFile(path, text);
  }

  public String getPath() {
    return path;
  }

  public String getName() {
    return name;
  }

  public String getLayout() {
    return FileUtil.readFile(path);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int flags) {
    parcel.writeString(path);
    parcel.writeString(name);
  }

  public static final Parcelable.Creator<LayoutFile> CREATOR =
      new Parcelable.Creator<LayoutFile>() {
        public LayoutFile createFromParcel(Parcel in) {
          return new LayoutFile(in);
        }

        public LayoutFile[] newArray(int size) {
          return new LayoutFile[size];
        }
      };

  private LayoutFile(Parcel parcel) {
    path = parcel.readString();
    name = parcel.readString();
  }
}
