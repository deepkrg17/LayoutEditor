package com.itsvks.layouteditor.managers;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

/** This is IdManager class which is used for managing ids */
public class IdManager {
  /** This is hashmap for storing view and its ids */
  private static HashMap<View, String> ids = new HashMap<>();

  /**
   * This method is used to add new id to view
   *
   * @param view View to which id needs to be added
   * @param id String id which needs to be added
   */
  public static void addNewId(View view, String id) {
    if (!ids.containsKey(view)) {
      view.setId(View.generateViewId());
    }

    ids.put(view, id.replace("@+id/", ""));
  }

  /**
   * This method is used to add id to view with specified name and id
   *
   * @param view View to which id needs to be added
   * @param idName String name of the id
   * @param id Integer of the id
   */
  public static void addId(View view, String idName, int id) {
    view.setId(id);
    ids.put(view, idName.replace("@+id/", ""));
  }

  /**
   * This method is used to remove the id for specified view
   *
   * @param view View to which id needs to be removed
   * @param removeChilds Boolean value to remove childs of ViewGroup
   */
  public static void removeId(View view, boolean removeChilds) {
    ids.remove(view);

    if (removeChilds && view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;

      for (int i = 0; i < group.getChildCount(); i++) {
        removeId(group.getChildAt(i), true);
      }
    }
  }

  /**
   * This method is used to check if id exists for specified name
   *
   * @param name String name of the id
   * @return Boolean value true/false
   */
  public static boolean containsId(String name) {
    String mName = name.replace("@id/", "");

    for (View view : ids.keySet()) {
      if (ids.get(view).equals(mName)) {
        return true;
      }
    }

    return false;
  }

  /** This method is used to clear the HashMap ids */
  public static void clear() {
    ids.clear();
  }

  /**
   * This method is used to get the view id for specified name
   *
   * @param name String name of the id
   * @return Integer of the view id
   */
  public static int getViewId(String name) {
    String mName = name.replace("@id/", "");

    for (View view : ids.keySet()) {
      if (ids.get(view).equals(mName)) {
        return view.getId();
      }
    }

    return -1;
  }

  /**
   * This method is used to get the list of ids
   *
   * @return List of ids
   */
  public static ArrayList<String> getIds() {
    return new ArrayList<String>(ids.values());
  }

  public static HashMap<View, String> getIdMap() {
    return ids;
  }
}
