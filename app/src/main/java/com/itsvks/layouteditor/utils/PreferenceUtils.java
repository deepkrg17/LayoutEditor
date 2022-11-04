package com.itsvks.layouteditor.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.preference.SwitchPreferenceCompat;

public class PreferenceUtils {
    public static SwitchPreferenceCompat switchPreference(Context context, String title) {
        var preference = new SwitchPreferenceCompat(context);
        preference.setTitle(title);
        return preference;
    }

    public static SwitchPreferenceCompat switchPreference(
            Context context, String title, String summary) {
        var preference = new SwitchPreferenceCompat(context);
        preference.setTitle(title);
        preference.setSummary(summary);
        return preference;
    }

    public static SwitchPreferenceCompat switchPreference(
            Context context, String title, String summary, Drawable icon) {
        var preference = new SwitchPreferenceCompat(context);
        preference.setTitle(title);
        preference.setSummary(summary);
        preference.setIcon(icon);
        return preference;
    }
}
