package com.itsvks.layouteditor.activities;

import android.os.Bundle;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.LayoutFile;
import com.itsvks.layouteditor.databinding.ActivityPreviewLayoutBinding;
import com.itsvks.layouteditor.tools.XmlLayoutParser;
import com.itsvks.layouteditor.utils.Constants;

public class PreviewLayoutActivity extends BaseActivity {

  public static final String EXTRA_KEY_XML = "xml";

  private ActivityPreviewLayoutBinding binding;

  @SuppressWarnings("deprecation")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityPreviewLayoutBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    LayoutFile layout = (LayoutFile) getIntent().getExtras().getParcelable(Constants.EXTRA_KEY_LAYOUT);

    XmlLayoutParser parser = new XmlLayoutParser(this);
    parser.parseFromXml(layout.getLayout(), this);

    binding.getRoot().addView(parser.getRoot());
  }
}
