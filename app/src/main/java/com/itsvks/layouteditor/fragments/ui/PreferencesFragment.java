package com.itsvks.layouteditor.fragments.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import androidx.preference.SwitchPreferenceCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.LayoutEditor;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.managers.SharedPreferenceManager;

public class PreferencesFragment extends PreferenceFragmentCompat {
    
  private ListPreference choose_theme;
  private SwitchPreferenceCompat dynamic_colors;

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.preference, rootKey);
        
    choose_theme = findPreference("choose_theme");
    dynamic_colors = findPreference("dynamic_colors");
    
    choose_theme.setValue(String.valueOf(SharedPreferenceManager.loadPrefInt(choose_theme.getTitle().toString(), -1)));
    choose_theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object obj) {
            SharedPreferenceManager.changePrefInt(choose_theme.getTitle().toString(), Integer.parseInt(String.valueOf(obj)));
            new LayoutEditor().updateTheme(SharedPreferenceManager.loadPrefInt(choose_theme.getTitle().toString(), Integer.valueOf(String.valueOf(obj))), BaseActivity.getInstance());
            return true;
        }
    });
        
    dynamic_colors.setChecked(SharedPreferenceManager.loadPrefBool(dynamic_colors.getTitle().toString(), false));
    dynamic_colors.setOnPreferenceChangeListener(dynamicThemeChangeListener());
  }
    
  public Preference.OnPreferenceChangeListener dynamicThemeChangeListener() {
        return new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object obj) {
                new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.note)
                    .setMessage(R.string.msg_dynamic_colors_dialog)
                    .setCancelable(false)
                    .setNegativeButton(R.string.cancel, (d, w) -> {
                        dynamic_colors.setOnPreferenceChangeListener(null);
                        dynamic_colors.setChecked(SharedPreferenceManager.loadPrefBool(dynamic_colors.getTitle().toString(), false));
                        dynamic_colors.setOnPreferenceChangeListener(dynamicThemeChangeListener());
                        d.cancel();
                    })
                    .setPositiveButton(R.string.okay, (d, w) -> {
                        SharedPreferenceManager.changePrefBool(dynamic_colors.getTitle().toString(), (boolean) obj);
                        getActivity().finishAffinity();
                    })
                    .show();
                return true;
            }
        };
    }
}
