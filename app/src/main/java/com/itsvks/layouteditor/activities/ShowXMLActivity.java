package com.itsvks.layouteditor.activities;

import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;

import com.blankj.utilcode.util.ClipboardUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.databinding.ActivityShowXMLBinding;

public class ShowXMLActivity extends BaseActivity {

    public static final String EXTRA_KEY_XML = "xml";

    private ActivityShowXMLBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowXMLBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setTitle("XML Preview");

        binding.topAppBar.setNavigationOnClickListener(
                v -> {
                    super.onBackPressed();
                });

        binding.result.setText(getIntent().getStringExtra(EXTRA_KEY_XML));
        binding.result.setTypeface(ResourcesCompat.getFont(this, R.font.jetbrains_mono_regular));

        binding.fab.setOnClickListener(
                v -> {
                    ClipboardUtils.copyText(binding.result.getText());
                    Snackbar.make(binding.getRoot(), "Copied", Snackbar.LENGTH_SHORT)
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .setAnchorView(binding.fab)
                            .show();
                });

        binding.nestedScrollView.setOnScrollChangeListener(
                (v, x, y, oldX, oldY) -> {
                    if (y > oldY + 6 && binding.fab.isExtended()) {
                        binding.fab.shrink();
                        binding.fab.hide();
                    }
                    if (y < oldY - 6 && !binding.fab.isExtended()) {
                        binding.fab.show();
                        binding.fab.extend();
                    }
                    if (y == 0) {
                        binding.fab.show();
                        binding.fab.extend();
                    }
                });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
