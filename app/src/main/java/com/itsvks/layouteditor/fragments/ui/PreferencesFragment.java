package com.itsvks.layouteditor.fragments.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.itsvks.layouteditor.LayoutEditor;
import com.itsvks.layouteditor.R;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);

        var preferenceTheme = findPreference("app_theme");
        var app = LayoutEditor.getInstance();
        preferenceTheme.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        switch ((String) newValue) {
                            case "2":
                                app.updateTheme(AppCompatDelegate.MODE_NIGHT_YES, requireActivity());
                                return true;
                            case "1":
                                app.updateTheme(AppCompatDelegate.MODE_NIGHT_NO, requireActivity());
                                return true;
                            case "3":
                                if (LayoutEditor.isAtLeastQ()) {
                                    app.updateTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, requireActivity());
                                } else {
                                    app.updateTheme(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, requireActivity());
                                }
                                return true;
                        }
                        return false;
                    }
                });
    }
}
