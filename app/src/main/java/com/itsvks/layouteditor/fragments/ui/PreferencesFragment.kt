package com.itsvks.layouteditor.fragments.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import androidx.preference.SwitchPreferenceCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.LayoutEditor;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.managers.PreferencesManager;

public class PreferencesFragment extends PreferenceFragmentCompat {

  private SwitchPreferenceCompat dynamic_colors;

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.preference, rootKey);

    ListPreference choose_theme = findPreference("choose_theme");
    dynamic_colors = findPreference("dynamic_colors");

    assert choose_theme != null;
//    choose_theme.setValue(String.valueOf(PreferencesManager.getCurrentTheme()));
        
    dynamic_colors.setChecked(PreferencesManager.isApplyDynamicColors());
    dynamic_colors.setOnPreferenceChangeListener(dynamicThemeChangeListener());
  }
    
  public Preference.OnPreferenceChangeListener dynamicThemeChangeListener() {
        return (preference, obj) -> {
            new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.note)
                .setMessage(R.string.msg_dynamic_colors_dialog)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, (d, w) -> {
                    dynamic_colors.setOnPreferenceChangeListener(null);
                    dynamic_colors.setChecked(PreferencesManager.isApplyDynamicColors());
                    dynamic_colors.setOnPreferenceChangeListener(dynamicThemeChangeListener());
                    d.cancel();
                })
                .setPositiveButton(R.string.okay, (d, w) -> {
                    getActivity().finishAffinity();
                })
                .show();
            return true;
        };
    }
}
