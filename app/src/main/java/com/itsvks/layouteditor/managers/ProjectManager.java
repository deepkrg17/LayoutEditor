package com.itsvks.layouteditor.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itsvks.layouteditor.LayoutEditor;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.utils.Constants;
import com.itsvks.layouteditor.utils.FileUtil;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ProjectManager {
  private static ProjectManager INSTANCE;

  public static synchronized ProjectManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ProjectManager();
    }
    return INSTANCE;
  }

  private List<List<HashMap<String, Object>>> paletteList = new ArrayList<>();

  private ProjectFile openedProject;

  private ProjectManager() {
    CompletableFuture.runAsync(() -> initPalette());
  }

  public void openProject(ProjectFile project) {
    openedProject = project;
    DrawableManager.loadFromFiles(openedProject.getDrawables());
    FontManager.loadFromFiles(openedProject.getFonts());
  }

  public void closeProject() {
    openedProject = null;
    DrawableManager.clear();
    FontManager.clear();
  }

  public ProjectFile getOpenedProject() {
    return openedProject;
  }

  public String getColorsXml() {
    return FileUtil.readFile(openedProject.getColorsPath());
  }

  public String getStringsXml() {
    return FileUtil.readFile(openedProject.getStringsPath());
  }

  public String getFormattedProjectName() {
    String projectName = openedProject.getName().toLowerCase(Locale.getDefault()).trim();

    if (projectName.contains(" ")) {
      projectName = projectName.replaceAll(" ", "_");
    }
    if (!projectName.endsWith(".xml")) {
      projectName = projectName.concat(".xml");
    }
    return projectName;
  }

  public List<HashMap<String, Object>> getPalette(int position) {
    return paletteList.get(position);
  }

  private void initPalette() {
    final var gson = new Gson();
    final var type = new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType();
    paletteList.clear();
    paletteList.add(convertJsonToJavaObject(gson, type, Constants.PALETTE_COMMON));
    paletteList.add(convertJsonToJavaObject(gson, type, Constants.PALETTE_TEXT));
    paletteList.add(convertJsonToJavaObject(gson, type, Constants.PALETTE_BUTTONS));
    paletteList.add(convertJsonToJavaObject(gson, type, Constants.PALETTE_WIDGETS));
    paletteList.add(convertJsonToJavaObject(gson, type, Constants.PALETTE_LAYOUTS));
    paletteList.add(convertJsonToJavaObject(gson, type, Constants.PALETTE_CONTAINERS));
   // paletteList.add(convertJsonToJavaObject(gson, type, Constants.PALETTE_GOOGLE));
    paletteList.add(convertJsonToJavaObject(gson, type, Constants.PALETTE_LEGACY));
  }

  private ArrayList<HashMap<String, Object>> convertJsonToJavaObject(
      Gson gson, Type type, String filePath) {
    return gson.fromJson(
        FileUtil.readFromAsset(filePath, LayoutEditor.getInstance().getContext()), type);
  }
}
