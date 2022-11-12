package com.itsvks.layouteditor.editor.initializer;

import java.util.ArrayList;

public class AttributeMap {
    private ArrayList<Attribute> attrs = new ArrayList<>();

    public void putValue(String key, String value) {
        if (contains(key)) {
            int index = getAttributeIndexFromKey(key);
            attrs.get(index).value = value;
        } else attrs.add(new Attribute(key, value));
    }

    public void removeValue(String key) {
        attrs.remove(getAttributeIndexFromKey(key));
    }

    public String getValue(String key) {
        Attribute attr = attrs.get(getAttributeIndexFromKey(key));
        return attr.value;
    }

    public ArrayList<String> keySet() {
        ArrayList<String> keys = new ArrayList<>();

        for (Attribute attr : attrs) {
            keys.add(attr.key);
        }

        return keys;
    }

    public ArrayList<String> values() {
        ArrayList<String> values = new ArrayList<>();

        for (Attribute attr : attrs) {
            values.add(attr.value);
        }

        return values;
    }

    public boolean contains(String key) {
        for (Attribute attr : attrs) {
            if (attr.key.equals(key)) return true;
        }

        return false;
    }

    private int getAttributeIndexFromKey(String key) {
        int index = 0;

        for (Attribute attr : attrs) {
            if (attr.key.equals(key)) return index;
            index++;
        }

        return index;
    }

    private class Attribute {
        private String key, value;

        public Attribute(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
