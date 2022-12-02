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
        preferenceTheme.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        switch ((String) newValue) {
                            case "2":
                                updateTheme(AppCompatDelegate.MODE_NIGHT_YES);
                                return true;
                            case "1":
                                updateTheme(AppCompatDelegate.MODE_NIGHT_NO);
                                return true;
                            case "3":
                                if (LayoutEditor.isAtLeastQ()) {
                                    updateTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                } else {
                                    updateTheme(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                                }
                                return true;
                        }
                        return false;
                    }
                });
    }

    private void updateTheme(int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);
        requireActivity().recreate();
    }
}
