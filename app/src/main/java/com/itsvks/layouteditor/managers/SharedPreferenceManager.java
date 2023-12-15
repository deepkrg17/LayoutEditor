package com.itsvks.layouteditor.managers;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.LinkedHashSet;
import java.util.Set;

public class SharedPreferenceManager {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferenceManager sharedPreferencesManager;
    private static Context context;

    private static final String LENGTH = "_length";
    private static final String DEFAULT_STRING_VALUE = "";
    private static final int DEFAULT_INT_VALUE = 0; //-1
    private static final double DEFAULT_DOUBLE_VALUE = 0d; //-1d
    private static final float DEFAULT_FLOAT_VALUE = 0f; //-1f
    private static final long DEFAULT_LONG_VALUE = 0L; //-1L
    private static final boolean DEFAULT_BOOLEAN_VALUE = false;
    
    public static void setContext(Context ctx) {
        if (ctx != null) {
            context = ctx;
        }
    }
    
    public static void changePrefInt(Object where, int what) {
        SharedPreferenceManager.with(context).writeInt(where, what);
    }
    
    public static void changePrefString(Object where, String what) {
        SharedPreferenceManager.with(context).writeString(where, what);
    }

    public static void changePrefBool(Object where, boolean what) {
        SharedPreferenceManager.with(context).writeBoolean(where, what);
    }
    
    public static int loadPrefInt(Object what) {
        int i = SharedPreferenceManager.with(context).readInt(what);
        return i;
    }

    public static int loadPrefInt(Object what, int where) {
        int i = SharedPreferenceManager.with(context).readInt(what, where);
        return i;
    }
    
    public static boolean loadPrefBool(Object what) {
        boolean bool = SharedPreferenceManager.with(context).readBoolean(what);
        return bool;
    }

    public static boolean loadPrefBool(Object what, boolean where) {
        boolean bool = SharedPreferenceManager.with(context).readBoolean(what, where);
        return bool;
    }
    
    public static String loadPrefString(Object what) {
        String str = SharedPreferenceManager.with(context).readString(what);
        return str;
    }

    public static String loadPrefString(Object what, String where) {
        String str = SharedPreferenceManager.with(context).readString(what, where);
        return str;
    }
    
    private SharedPreferenceManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(
                context.getPackageName() + "_preferences",
                Context.MODE_PRIVATE
        );
    }

    private SharedPreferenceManager(Context context, Object preferencesName) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(
                checkKey(preferencesName),
                Context.MODE_PRIVATE
        );
    }

    /**
     * @param context
     * @return Returns a 'Preferences' instance
     */
    public static SharedPreferenceManager with(Context context) {
        if (sharedPreferencesManager == null) {
            sharedPreferencesManager = new SharedPreferenceManager(context);
        }
        return sharedPreferencesManager;
    }

    /**
     * @param context
     * @param forceInstantiation
     * @return Returns a 'Preferences' instance
     */
    public static SharedPreferenceManager with(Context context, boolean forceInstantiation) {
        if (forceInstantiation) {
            sharedPreferencesManager = new SharedPreferenceManager(context);
        }
        return sharedPreferencesManager;
    }

    /**
     * @param context
     * @param preferencesName
     * @return Returns a 'Preferences' instance
     */
    public static SharedPreferenceManager with(Context context, Object preferencesName) {
        if (sharedPreferencesManager == null) {
            sharedPreferencesManager = new SharedPreferenceManager(context, checkKey(preferencesName));
        }
        return sharedPreferencesManager;
    }

    /**
     * @param context
     * @param preferencesName
     * @param forceInstantiation
     * @return Returns a 'Preferences' instance
     */
    public static SharedPreferenceManager with(Context context, Object preferencesName, boolean forceInstantiation) {
        if (forceInstantiation) {
            sharedPreferencesManager = new SharedPreferenceManager(context, checkKey(preferencesName));
        }
        return sharedPreferencesManager;
    }

    // String related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public static String readString(Object what) {
        return sharedPreferences.getString(checkKey(what), DEFAULT_STRING_VALUE);
    }

    /**
     * @param what
     * @param defaultString
     * @return Returns the stored value of 'what'
     */
    public static String readString(Object what, Object defaultString) {
        return sharedPreferences.getString(checkKey(what), String.valueOf(defaultString));
    }
    
    /**
     * @param where
     * @param what
     */
    public static void writeString(Object where, Object what) {
        sharedPreferences.edit().putString(checkKey(where), String.valueOf(what)).apply();
    }

    // int related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public static int readInt(Object what) {
        try {
            return sharedPreferences.getInt(checkKey(what), DEFAULT_INT_VALUE);
        } catch (java.lang.ClassCastException ex) {
            return DEFAULT_INT_VALUE;
        }
    }

    /**
     * @param what
     * @param defaultInt
     * @return Returns the stored value of 'what'
     */
    public static int readInt(Object what, Object defaultInt) {
        return sharedPreferences.getInt(checkKey(what), Integer.parseInt(String.valueOf(defaultInt)));
    }

    /**
     * @param where
     * @param what
     */
    public static void writeInt(Object where, Object what) {
        sharedPreferences.edit().putInt(checkKey(where), Integer.parseInt(String.valueOf(what))).apply();
    }

    // double related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public static double readDouble(Object what) {
        if (!contains(what))
            return DEFAULT_DOUBLE_VALUE;
        return Double.longBitsToDouble(readLong(what));
    }

    /**
     * @param what
     * @param defaultDouble
     * @return Returns the stored value of 'what'
     */
    public static double readDouble(Object what, Object defaultDouble) {
        if (!contains(what))
            return Double.parseDouble(String.valueOf(defaultDouble));
        return Double.longBitsToDouble(readLong(what));
    }

    /**
     * @param where
     * @param what
     */
    public static void writeDouble(Object where, Object what) {
        writeLong(where, Double.doubleToRawLongBits(Double.parseDouble(String.valueOf(what))));
    }

    // float related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public static float readFloat(Object what) {
        return sharedPreferences.getFloat(checkKey(what), DEFAULT_FLOAT_VALUE);
    }

    /**
     * @param what
     * @param defaultFloat
     * @return Returns the stored value of 'what'
     */
    public static float readFloat(Object what, Object defaultFloat) {
        return sharedPreferences.getFloat(checkKey(what), Float.parseFloat(String.valueOf(defaultFloat)));
    }

    /**
     * @param where
     * @param what
     */
    public static void writeFloat(Object where, Object what) {
        sharedPreferences.edit().putFloat(checkKey(where), Float.parseFloat(String.valueOf(what))).apply();
    }

    // long related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public static long readLong(Object what) {
        return sharedPreferences.getLong(checkKey(what), DEFAULT_LONG_VALUE);
    }

    /**
     * @param what
     * @param defaultLong
     * @return Returns the stored value of 'what'
     */
    public static long readLong(Object what, Object defaultLong) {
        return sharedPreferences.getLong(checkKey(what), Long.parseLong(String.valueOf(defaultLong)));
    }

    /**
     * @param where
     * @param what
     */
    public static void writeLong(Object where, Object what) {
        sharedPreferences.edit().putLong(checkKey(where), Long.parseLong(String.valueOf(what))).apply();
    }

    // boolean related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public static boolean readBoolean(Object what) {
        return sharedPreferences.getBoolean(checkKey(what), DEFAULT_BOOLEAN_VALUE);
    }

    /**
     * @param what
     * @param defaultBoolean
     * @return Returns the stored value of 'what'
     */
    public static boolean readBoolean(Object what, Object defaultBoolean) {
        /*if (defaultBoolean == true && !sharedPreferences.contains(String.valueOf(what)))
            writeBoolean(what, true);*/
        try {
            return sharedPreferences.getBoolean(checkKey(what), Boolean.parseBoolean(String.valueOf(defaultBoolean)));
        } catch (java.lang.ClassCastException ex) {
            return Boolean.parseBoolean(String.valueOf(defaultBoolean));
        }
    }

    /**
     * @param where
     * @param what
     */
    public static void writeBoolean(Object where, Object what) {
        sharedPreferences.edit().putBoolean(checkKey(where), Boolean.parseBoolean(String.valueOf(what))).apply();
    }

    // String set methods

    /**
     * @param key
     * @param value
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void putStringSet(final Object key, final Set<String> value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            sharedPreferences.edit().putStringSet(checkKey(key), value).apply();
        } else {
            // Workaround for pre-HC's lack of StringSets
            putOrderedStringSet(key, value);
        }
    }

    /**
     * @param key
     * @param value
     */
    public static void putOrderedStringSet(Object key, Set<String> value) {
        int stringSetLength = 0;
        if (sharedPreferences.contains(key + LENGTH)) {
            // First read what the value was
            stringSetLength = readInt(key + LENGTH);
        }
        writeInt(key + LENGTH, value.size());
        int i = 0;
        for (String aValue : value) {
            writeString(key + "[" + i + "]", aValue);
            i++;
        }
        for (; i < stringSetLength; i++) {
            // Remove any remaining values
            remove(key + "[" + i + "]");
        }
    }

    /**
     * @param key
     * @param defValue
     * @return Returns the String Set with HoneyComb compatibility
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Set<String> getStringSet(final Object key, final Set<String> defValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return sharedPreferences.getStringSet(checkKey(key), defValue);
        } else {
            // Workaround for pre-HC's missing getStringSet
            return getOrderedStringSet(key, defValue);
        }
    }

    /**
     * @param key
     * @param defValue
     * @return Returns the ordered String Set
     */
    public static Set<String> getOrderedStringSet(Object key, final Set<String> defValue) {
        if (contains(key + LENGTH)) {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            int stringSetLength = readInt(key + LENGTH);
            if (stringSetLength >= 0) {
                for (int i = 0; i < stringSetLength; i++) {
                    set.add(readString(key + "[" + i + "]"));
                }
            }
            return set;
        }
        return defValue;
    }
    
    // checking related methods
    
    /**
     * @param obj
     */
    public static String checkKey(Object obj) {
        // Check if the our Object is String or not if yes then return it
        if (obj instanceof String) return (String) obj;
        
        /**
         * If the Object is not String then check if the Object is Integer
         * if yes then try to fetch the string from resources if availble
         * if not then convert the Integer to String
        **/
        if (obj instanceof Integer) return fetchKeyString((int) obj);
        
        // Return null if the Object is neither String nor Integer
        return (String) obj;
    }
    
    // fetching related methods
    
    /**
     * @param key
     */
    public static String fetchKeyString(int key) {
        // Try to fetch the String from key if availble
        try {
            return context.getString(key);
        } catch (android.content.res.Resources.NotFoundException e) {
            // If not then return the converted Integer to String
            return String.valueOf(key);
        }
    }
    
    // end related methods

    /**
     * @param key
     */
    public static void remove(final Object key) {
        if (contains(key + LENGTH)) {
            // Workaround for pre-HC's lack of StringSets
            int stringSetLength = readInt(key + LENGTH);
            if (stringSetLength >= 0) {
                sharedPreferences.edit().remove(key + LENGTH).apply();
                for (int i = 0; i < stringSetLength; i++) {
                    sharedPreferences.edit().remove(key + "[" + i + "]").apply();
                }
            }
        }
        sharedPreferences.edit().remove(checkKey(key)).apply();
    }

    /**
     * @param key
     * @return Returns if that key exists
     */
    public static boolean contains(final Object key) {
        return sharedPreferences.contains(checkKey(key));
    }

    /**
     * Clear all the preferences
     */
    public static void clear() {
        sharedPreferences.edit().clear().apply();
    }
}