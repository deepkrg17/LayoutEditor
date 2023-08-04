package com.itsvks.editor.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.blankj.utilcode.util.FileUtils;
import com.itsvks.editor.utils.FileUtil;
import java.io.File;

public class Project implements Parcelable {
  private String path, name, time;

  public Project(String path, String time) {
    this.path = path;
    this.time = time;
    this.name = FileUtil.getLastSegmentFromPath(path);
  }

  public void rename(String newPath) {
    File newFile = new File(newPath);
    File oldFile = new File(getPath());
    oldFile.renameTo(newFile);

    path = newPath;
  }
  
  public String getDrawablePath() {
    return path + "/drawable/";
  }

  public String getFontPath() {
    return path + "/font/";
  }

  public String getColorsPath() {
    return path + "/values/colors.xml";
  }

  public String getStringsPath() {
    return path + "/values/strings.xml";
  }
  
  public String getMainLayoutPath() {
    return path + "/layout/activity_main.xml";
  }

  public File[] getDrawables() {
    File file = new File(path + "/drawable/");

    if (!file.exists()) {
      FileUtils.createOrExistsDir(path + "/drawable/");
    }

    return file.listFiles();
  }

  public File[] getFonts() {
    File file = new File(path + "/font/");

    if (!file.exists()) {
      FileUtils.createOrExistsDir(path + "/font/");
    }

    return file.listFiles();
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getTime() {
    return this.time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int flags) {
    parcel.writeString(path);
    parcel.writeString(getName());
  }

  public static final Parcelable.Creator<Project> CREATOR =
      new Parcelable.Creator<Project>() {
        public Project createFromParcel(Parcel in) {
          return new Project(in);
        }

        public Project[] newArray(int size) {
          return new Project[size];
        }
      };

  private Project(Parcel parcel) {
    path = parcel.readString();
    name = parcel.readString();
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
