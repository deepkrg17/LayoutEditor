package com.itsvks.layouteditor.activities;

import android.os.Bundle;

import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.databinding.ActivityPreviewLayoutBinding;
import com.itsvks.layouteditor.tools.XmlLayoutParser;

public class PreviewLayoutActivity extends BaseActivity {

  public static final String EXTRA_KEY_PROJECT = "project";
  public static final String EXTRA_KEY_XML = "xml";

  private ActivityPreviewLayoutBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityPreviewLayoutBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    ProjectFile project = getIntent().getParcelableExtra(EXTRA_KEY_PROJECT);

    XmlLayoutParser parser = new XmlLayoutParser(this);
    parser.parseFromXml(project.getLayout(), this);

    binding.getRoot().addView(parser.getRoot());
    //    binding.designEditor.addView(parser.getRoot());
    //    binding
    //        .getRoot()
    //        .setOnClickListener(
    //            v -> {
    //              switch (binding.designEditor.getViewType()) {
    //                case BLUEPRINT:
    //                  binding.designEditor.setViewType(DesignEditor.ViewType.DESIGN);
    //                  break;
    //                case DESIGN:
    //                  binding.designEditor.setViewType(DesignEditor.ViewType.BLUEPRINT);
    //                  break;
    //              }
    //              switch (binding.designEditor.getDeviceConfiguration().getSize()) {
    //                case SMALL:
    //                  binding.designEditor.resizeLayout(
    //                      new DeviceConfiguration(DeviceSize.MEDIUM));
    //                  break;
    //                case MEDIUM:
    //                  binding.designEditor.resizeLayout(
    //                      new DeviceConfiguration(DeviceSize.LARGE));
    //                  break;
    //                case LARGE:
    //                  binding.designEditor.resizeLayout(
    //                      new DeviceConfiguration(DeviceSize.SMALL));
    //                  break;
    //              }
    //            });
  }
}
