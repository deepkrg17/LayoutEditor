package com.itsvks.layouteditor.activities;

import android.os.Bundle;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.databinding.ActivityPreviewDrawableBinding;
import com.itsvks.layouteditor.interfaces.PreviewDrawableListener;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.views.AlphaPatternDrawable;

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
    binding.background.setImageDrawable(new AlphaPatternDrawable(24));

    if (listener != null) {
      listener.showInImage(binding.mainImage);
      listener.setSubtitle(getSupportActionBar());
    }
  }

  public static void setListener(PreviewDrawableListener viewDrawableListener) {
    listener = viewDrawableListener;
  }
}
