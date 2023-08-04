package com.itsvks.editor.activities;

import android.os.Bundle;
import com.itsvks.editor.databinding.ActivitySettingsBinding;
import com.itsvks.editor.R;

public class SettingsActivity extends BaseActivity {
  private ActivitySettingsBinding binding;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivitySettingsBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    
    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setTitle(R.string.settings);
    binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
  }
}
