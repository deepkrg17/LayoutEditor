package com.itsvks.layouteditor.fragments.ui.preferences;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.utils.PreferenceUtils;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        var context = getPreferenceManager().getContext();
        var screen = getPreferenceManager().createPreferenceScreen(context);

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
        
        screen.addPreference(vibrationPreference);
        screen.addPreference(toggleStrokePreference);
        setPreferenceScreen(screen);
    }
}
