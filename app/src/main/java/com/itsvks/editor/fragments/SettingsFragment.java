package com.itsvks.editor.fragments;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import com.itsvks.editor.R;

public class SettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings, rootKey);
  }
}
