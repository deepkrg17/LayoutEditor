package com.itsvks.layouteditor.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.databinding.ActivityPreviewDrawableBinding;
import com.itsvks.layouteditor.interfaces.PreviewDrawableListener;
import com.itsvks.layouteditor.R;

public class PreviewDrawableActivity extends BaseActivity {

  private ActivityPreviewDrawableBinding binding;
  private static PreviewDrawableListener listener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityPreviewDrawableBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    setSupportActionBar(binding.topAppBar);
    getSupportActionBar().setTitle(R.string.preview_drawable);

    binding.topAppBar.setNavigationOnClickListener(v -> onBackPressed());

    if (listener != null) {
      listener.showInImage(binding.mainImage);
      listener.setSubtitle(getSupportActionBar());
    }
  }

  public static void setListener(PreviewDrawableListener viewDrawableListener) {
    listener = viewDrawableListener;
  }
}
