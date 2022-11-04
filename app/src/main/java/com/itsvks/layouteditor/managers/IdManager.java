package com.itsvks.layouteditor.managers;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class IdManager {
    private static HashMap<View, String> ids = new HashMap<>();

    public static void addNewId(View view, String id) {
        if (!ids.containsKey(view)) {
            view.setId(View.generateViewId());
        }

        ids.put(view, id.replace("@+id/", ""));
    }

    public static void addId(View view, String idName, int id) {
        view.setId(id);
        ids.put(view, idName.replace("@+id/", ""));
    }

    public static void removeId(View view, boolean removeChilds) {
        ids.remove(view);

        if (removeChilds && view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); i++) {
                removeId(group.getChildAt(i), true);
            }
        }
    }

    public static boolean containsId(String name) {
        String mName = name.replace("@id/", "");

        for (View view : ids.keySet()) {
            if (ids.get(view).equals(mName)) {
                return true;
            }
        }

        return false;
    }

    public static void clear() {
        ids.clear();
    }

    public static int getViewId(String name) {
        String mName = name.replace("@id/", "");

        for (View view : ids.keySet()) {
            if (ids.get(view).equals(mName)) {
                return view.getId();
            }
        }

        return -1;
    }

    public static ArrayList<String> getIds() {
        return new ArrayList<String>(ids.values());
    }
}
