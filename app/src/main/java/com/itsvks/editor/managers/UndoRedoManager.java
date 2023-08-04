package com.itsvks.editor.managers;

import android.view.MenuItem;
import java.util.ArrayList;

/** UndoRedoManager class is used to add XML to history and Undo/Redo the previous changes. */
public class UndoRedoManager {

  /** Max size of the history */
  private final int maxSize = 20;

  /** History list to store the XMLs */
  private ArrayList<String> history;
  /** Index to track the current history */
  private int index;

  /** MenuItem for Undo Button */
  private MenuItem btnUndo;
  /** MenuItem for Redo Button */
  private MenuItem btnRedo;

  /**
   * Constructor to initialize the instance
   *
   * @param undo MenuItem for Undo Button
   * @param redo MenuItem for Redo Button
   */
  public UndoRedoManager(MenuItem undo, MenuItem redo) {
    btnUndo = undo;
    btnRedo = redo;
    history = new ArrayList<>();
  }

  /**
   * Add XML to the history
   *
   * @param xml XML string to add to history
   */
  public void addToHistory(String xml) {
    if (matchLastHistory(xml)) return;

    history.add(xml);

    if (history.size() == maxSize) {
      history.remove(0);
    }

    index = history.size() - 1;
    updateButtons();
  }

  /**
   * To get the previous XML from the history
   *
   * @return Previous XML from the history
   */
  public String undo() {
    if (index > 0) {
      index--;
      updateButtons();
      return history.get(index);
    }

    return "";
  }

  /**
   * To get the next XML from the history
   *
   * @return Next XML from the history
   */
  public String redo() {
    if (index < history.size() - 1) {
      index++;
      updateButtons();
      return history.get(index);
    }

    return "";
  }

  /** To update the button's enabled state */
  public void updateButtons() {
    if (btnRedo == null || btnUndo == null) return;

    btnUndo.getIcon().setAlpha(isUndoEnabled() ? 255 : 130);
    btnUndo.setEnabled(isUndoEnabled());
    btnRedo.getIcon().setAlpha(isRedoEnabled() ? 255 : 130);
    btnRedo.setEnabled(isRedoEnabled());
  }

  /**
   * To check if Undo is enabled or not
   *
   * @return true if undo is enabled
   */
  public boolean isUndoEnabled() {
    return index > 0;
  }

  /**
   * To check if Redo is enabled or not
   *
   * @return true if redo is enabled
   */
  public boolean isRedoEnabled() {
    return index < history.size() - 1;
  }

  /**
   * To check if the last history is same as the current one
   *
   * @param xml Current XML
   * @return true if last history is same as the current one
   */
  public boolean matchLastHistory(String xml) {
    int lastIndex = history.size() - 1;
    if (lastIndex < 0) return false;
    if (xml == history.get(lastIndex)) return true;
    return false;
  }
}
