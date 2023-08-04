package com.itsvks.editor.crash;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.content.res.AppCompatResources;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.google.android.material.snackbar.Snackbar;
import com.itsvks.editor.activities.BaseActivity;
import com.itsvks.editor.BuildConfig;
import com.itsvks.editor.R;
import com.itsvks.editor.databinding.ActivityCrashBinding;

public class CrashActivity extends BaseActivity {
  private ActivityCrashBinding binding;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityCrashBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    
    setSupportActionBar(binding.topAppBar);
    getSupportActionBar().setTitle(R.string.app_crashed);

    var error = new StringBuilder();

    error.append("Manufacturer: " + DeviceUtils.getManufacturer() + "\n");
    error.append("Device: " + DeviceUtils.getModel() + "\n");
    error.append(getIntent().getStringExtra("Software"));
    error.append("App version: " + BuildConfig.VERSION_NAME);
    error.append("\n\n");
    error.append(getIntent().getStringExtra("Error"));
    error.append("\n\n");
    error.append(getIntent().getStringExtra("Date"));

    binding.result.setText(error.toString());

    binding.fab.setOnClickListener(
        v -> {
          ClipboardUtils.copyText(binding.result.getText());
          Snackbar.make(binding.getRoot(), getString(R.string.copied), Snackbar.LENGTH_SHORT)
              .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
              .setAnchorView(binding.fab)
              .show();
        });
  }
  
  @Override
  public void onBackPressed() {
    System.exit(0);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    var close = menu.add(getString(R.string.close));
    close.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    close.setIcon(AppCompatResources.getDrawable(this, R.drawable.close));
    close.setContentDescription(getString(R.string.close));

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getTitle().equals(getString(R.string.close))) {
      System.exit(0);
      return true;
    }
    return false;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
  }
}
