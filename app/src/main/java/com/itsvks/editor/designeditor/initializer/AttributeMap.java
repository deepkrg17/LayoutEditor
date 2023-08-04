package com.itsvks.editor.designeditor.initializer;

import java.util.List;
import java.util.ArrayList;

public class AttributeMap {
  private List<Attribute> attrs = new ArrayList<>();

  /**
   * Puts a key-value pair into the AttributeMap
   *
   * @param key the key of the attribute
   * @param value the value of the attribute
   */
  public void putValue(String key, String value) {
    if (contains(key)) {
      int index = getAttributeIndexFromKey(key);
      attrs.get(index).value = value;
    } else {
      attrs.add(new Attribute(key, value));
    }
  }

  /**
   * Removes a key-value pair from the AttributeMap
   *
   * @param key the key of the attribute to be removed
   */
  public void removeValue(String key) {
    int index = getAttributeIndexFromKey(key);
    attrs.remove(index);
  }

  /**
   * Gets the value associated with the given key in the AttributeMap
   *
   * @param key the key of the attribute
   * @return the value of the attribute
   */
  public String getValue(String key) {
    int index = getAttributeIndexFromKey(key);
    Attribute attr = attrs.get(index);
    return attr.value;
  }

  /**
   * Gets a list of all the keys in the AttributeMap
   *
   * @return a list of all keys
   */
  public List<String> keySet() {
    List<String> keys = new ArrayList<>();

    for (Attribute attr : attrs) {
      keys.add(attr.key);
    }

    return keys;
  }

  /**
   * Gets a list of all the values in the AttributeMap
   *
   * @return a list of all values
   */
  public List<String> values() {
    List<String> values = new ArrayList<>();

    for (Attribute attr : attrs) {
      values.add(attr.value);
    }

    return values;
  }

  /**
   * Checks if the AttributeMap contains a key
   *
   * @param key the key to check for
   * @return true if the AttributeMap contains the key, false otherwise
   */
  public boolean contains(String key) {
    for (Attribute attr : attrs) {
      if (attr.key.equals(key)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Gets the index of the Attribute with the given key
   *
   * @param key the key of the Attribute
   * @return the index of the Attribute
   */
  private int getAttributeIndexFromKey(String key) {
    int index = 0;

    for (Attribute attr : attrs) {
      if (attr.key.equals(key)) {
        return index;
      }

      index++;
    }

    return index;
  }

  private class Attribute {
    private String key, value;

    /**
     * Constructs an Attribute with the specified key-value pair
     *
     * @param key the key of the Attribute
     * @param value the value of the Attribute
     */
    public Attribute(String key, String value) {
      this.key = key;
      this.value = value;
    }
  }
}
