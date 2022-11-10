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
        var context = getPreferenceManager().getContext();
        var screen = getPreferenceManager().createPreferenceScreen(context);

        var categoryCommon = new PreferenceCategory(context);
        categoryCommon.setTitle("Common");

        var vibrationPreference =
                PreferenceUtils.switchPreference(
                        context,
                        "Enable Vibration",
                        "May consume battery",
                        AppCompatResources.getDrawable(context, R.drawable.vibrate));
        vibrationPreference.setKey("vibration");

        var toggleStrokePreference =
                PreferenceUtils.switchPreference(
                        context,
                        "Show Stroke",
                        "Show stroke in the editor?",
                        AppCompatResources.getDrawable(context, R.drawable.dots_square));
        toggleStrokePreference.setKey("toggle_stroke");
        toggleStrokePreference.setChecked(true);

        screen.addPreference(categoryCommon);
        screen.addPreference(vibrationPreference);
        screen.addPreference(toggleStrokePreference);
        setPreferenceScreen(screen);
    }
}
