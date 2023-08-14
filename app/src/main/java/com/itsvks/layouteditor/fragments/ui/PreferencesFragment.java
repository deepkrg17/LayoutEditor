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
  }
}
