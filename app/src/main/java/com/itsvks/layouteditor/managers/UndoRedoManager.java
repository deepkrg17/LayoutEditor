package com.itsvks.layouteditor.managers;

import android.view.MenuItem;

import java.util.ArrayList;

public class UndoRedoManager {
    private final int maxSize = 20;

    private ArrayList<String> history;
    private int index;

    private MenuItem btnUndo;
    private MenuItem btnRedo;

    public UndoRedoManager(MenuItem undo, MenuItem redo) {
        btnUndo = undo;
        btnRedo = redo;
        history = new ArrayList<>();
    }

    public void addToHistory(String xml) {
        if (matchLastHistory(xml)) return;

        history.add(xml);

        if (history.size() == maxSize) {
            history.remove(0);
        }

        index = history.size() - 1;
        updateButtons();
    }

    public String undo() {
        if (index > 0) {
            index--;
            updateButtons();
            return history.get(index);
        }

        return "";
    }

    public String redo() {
        if (index < history.size() - 1) {
            index++;
            updateButtons();
            return history.get(index);
        }

        return "";
    }

    public void updateButtons() {
        if (btnRedo == null || btnUndo == null) return;

        btnUndo.getIcon().setAlpha(isUndoEnabled() ? 255 : 130);
        btnUndo.setEnabled(isUndoEnabled());
        btnRedo.getIcon().setAlpha(isRedoEnabled() ? 255 : 130);
        btnRedo.setEnabled(isRedoEnabled());
    }

    public boolean isUndoEnabled() {
        return index > 0;
    }

    public boolean isRedoEnabled() {
        return index < history.size() - 1;
    }

    public boolean matchLastHistory(String xml) {
        int lastIndex = history.size() - 1;
        if (lastIndex < 0) return false;
        if (xml == history.get(lastIndex)) return true;
        return false;
    }
}
