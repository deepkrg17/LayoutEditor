package com.itsvks.layouteditor.managers;

import com.itsvks.layouteditor.ProjectFile;

public enum ProjectManager {
  INSTANCE;

  private ProjectFile project;

  public void setOpenedProject(ProjectFile project) {
    this.project = project;
  }

  public ProjectFile getOpenedProject() {
    return project;
  }
}
