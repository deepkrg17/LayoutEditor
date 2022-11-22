package com.itsvks.layouteditor.fragments.ui;

import android.os.Bundle;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.utils.PreferenceUtils;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        var screen = getPreferenceManager().createPreferenceScreen(requireContext());

        var categoryCommon = new PreferenceCategory(requireContext());
        categoryCommon.setTitle(getString(R.string.common));

        var vibrationPreference =
                PreferenceUtils.switchPreference(
                        requireContext(),
                        getString(R.string.enable_vibration),
                        getString(R.string.summary_enable_vibration),
                        AppCompatResources.getDrawable(requireContext(), R.drawable.vibrate));
        vibrationPreference.setKey("vibration");

        var toggleStrokePreference =
                PreferenceUtils.switchPreference(
                        requireContext(),
                        getString(R.string.show_stroke),
                        getString(R.string.summary_show_stroke),
                        AppCompatResources.getDrawable(requireContext(), R.drawable.dots_square));
        toggleStrokePreference.setKey("toggle_stroke");
        toggleStrokePreference.setChecked(true);

        screen.addPreference(categoryCommon);
        screen.addPreference(vibrationPreference);
        screen.addPreference(toggleStrokePreference);
        setPreferenceScreen(screen);
    }
}
