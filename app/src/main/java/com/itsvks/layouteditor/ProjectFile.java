package com.itsvks.layouteditor;

import android.os.Parcel;
import android.os.Parcelable;

import com.itsvks.layouteditor.utils.FileUtil;

import java.io.File;
import java.util.Calendar;

public class ProjectFile implements Parcelable {
  private String path;
  public String name;
  public String date;

  public ProjectFile(String path, String date) {
    this.path = path;
    this.date = date;
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
    File file = new File(path + "/layout/");

    if (!file.exists()) {
      FileUtil.makeDir(path + "/layout/");
    }

    FileUtil.writeFile(path + "/layout/" + "layout_main.xml", text);
  }

  public String getDrawablePath() {
    return path + "/drawable/";
  }

  public String getColorsPath() {
    return path + "/values/colors.xml";
  }

  public String getStringsPath() {
    return path + "/values/strings.xml";
  }

  public File[] getDrawables() {
    File file = new File(path + "/drawable/");

    if (!file.exists()) {
      FileUtil.makeDir(path + "/drawable/");
    }

    return file.listFiles();
  }

  public String getPath() {
    return path;
  }

  public String getName() {
    return name;
  }

  public String getLayout() {
    return FileUtil.readFile(path + "/layout/" + "layout_main.xml");
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

  public static final Parcelable.Creator<ProjectFile> CREATOR =
      new Parcelable.Creator<ProjectFile>() {
        public ProjectFile createFromParcel(Parcel in) {
          return new ProjectFile(in);
        }

        public ProjectFile[] newArray(int size) {
          return new ProjectFile[size];
        }
      };

  private ProjectFile(Parcel parcel) {
    path = parcel.readString();
    name = parcel.readString();
  }
}
