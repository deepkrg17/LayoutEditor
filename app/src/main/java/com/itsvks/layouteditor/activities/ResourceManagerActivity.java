package com.itsvks.layouteditor.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ext.SdkExtensions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.managers.ProjectManager;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.activities.ShowXMLActivity;
import com.itsvks.layouteditor.adapters.ResourcesPagerAdapter;
import com.itsvks.layouteditor.adapters.models.DrawableFile;
import com.itsvks.layouteditor.adapters.models.ValuesItem;
import com.itsvks.layouteditor.databinding.ActivityResourceManagerBinding;
import com.itsvks.layouteditor.fragments.resources.ColorFragment;
import com.itsvks.layouteditor.fragments.resources.DrawableFragment;
import com.itsvks.layouteditor.fragments.resources.FontFragment;
import com.itsvks.layouteditor.fragments.resources.StringFragment;
import com.itsvks.layouteditor.utils.FilePicker;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.SBUtils;
import java.util.ArrayList;
import java.util.List;

public class ResourceManagerActivity extends BaseActivity {

  private ActivityResourceManagerBinding binding;
  private ProjectFile project;
  private List<DrawableFile> drawableList = new ArrayList<>();
  private ResourcesPagerAdapter adapter;
  private FilePicker filepicker;
  private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityResourceManagerBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(binding.topAppBar);
    getSupportActionBar().setTitle(R.string.res_manager);

    binding.topAppBar.setNavigationOnClickListener(
        __ -> {
          onBackPressed();
        });

    project = ProjectManager.INSTANCE.getOpenedProject();
    // loadDrawables();
    adapter = new ResourcesPagerAdapter(getSupportFragmentManager(), getLifecycle());

    adapter.addFragment(new DrawableFragment(drawableList));
    adapter.addFragment(new ColorFragment());
    adapter.addFragment(new StringFragment());
    adapter.addFragment(new FontFragment());
    filepicker =
        new FilePicker(this) {

          @Override
          public void onRequestPermission(boolean isGranted) {
            if (isGranted)
              SBUtils.make(binding.getRoot(), R.string.permission_granted)
                  .setSlideAnimation()
                  .showAsSuccess();
            else
              SBUtils.make(binding.getRoot(), R.string.permission_denied)
                  .setSlideAnimation()
                  .showAsError();
          }

          @Override
          public void onPickFile(Uri uri) {
            if (uri == null) {
              SBUtils.make(binding.getRoot(), "No image selected").setFadeAnimation().show();
            } else {
              if (FileUtil.isDownloadsDocument(uri)) {
                SBUtils.make(binding.getRoot(), R.string.select_from_storage).showAsError();
                return;
              }
              Fragment fragment =
                  getSupportFragmentManager()
                      .findFragmentByTag("f" + binding.pager.getCurrentItem());
              if (fragment != null && fragment instanceof DrawableFragment) {
                ((DrawableFragment) fragment).addDrawable(FileUtil.convertUriToFilePath(uri));
              }
            }
          }
        };
    pickMedia =
        registerForActivityResult(
            new PickVisualMedia(),
            uri -> {
              // Callback is invoked after the user selects a media item or closes the
              // photo picker.
              if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                if (FileUtil.isDownloadsDocument(uri)) {
                  SBUtils.make(binding.getRoot(), R.string.select_from_storage).showAsError();
                  return;
                }
                Fragment fragment =
                    getSupportFragmentManager()
                        .findFragmentByTag("f" + binding.pager.getCurrentItem());
                if (fragment != null && fragment instanceof DrawableFragment) {
                  ((DrawableFragment) fragment).addDrawable(FileUtil.convertUriToFilePath(uri));
                }
              } else {
                Log.d("PhotoPicker", "No media selected");
                SBUtils.make(binding.getRoot(), "No image selected").setFadeAnimation().show();
              }
            });

    binding.pager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    binding.pager.setAdapter(adapter);

    TabLayoutMediator mediator =
        new TabLayoutMediator(
            binding.tabLayout,
            binding.pager,
            new TabLayoutMediator.TabConfigurationStrategy() {
              @Override
              public void onConfigureTab(TabLayout.Tab tab, int position) {
                switch (position) {
                  case 0:
                    tab.setText(R.string.drawable);
                    tab.setIcon(
                        AppCompatResources.getDrawable(
                            ResourceManagerActivity.this, R.drawable.image_outline));
                    break;
                  case 1:
                    tab.setText(R.string.color);
                    tab.setIcon(
                        AppCompatResources.getDrawable(
                            ResourceManagerActivity.this, R.drawable.palette_outline));
                    break;
                  case 2:
                    tab.setText(R.string.string);
                    tab.setIcon(
                        AppCompatResources.getDrawable(
                            ResourceManagerActivity.this, R.drawable.format_letter_case));
                    break;
                  case 3:
                    tab.setText(R.string.font);
                    tab.setIcon(
                        AppCompatResources.getDrawable(
                            ResourceManagerActivity.this, R.drawable.format_font));
                    break;
                }
              }
            });
    mediator.attach();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  public List<DrawableFile> getDrawableList() {
    return this.drawableList;
  }

  public void setDrawableList(List<DrawableFile> drawableList) {
    this.drawableList = drawableList;
  }

  public ProjectFile getProject() {
    return this.project;
  }

  public void setProject(ProjectFile project) {
    this.project = project;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // if (menu instanceof MenuBuilder) ((MenuBuilder) menu).setOptionalIconsVisible(true);

    getMenuInflater().inflate(R.menu.menu_resource_manager, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    var id = item.getItemId();
    Fragment fragment =
        getSupportFragmentManager().findFragmentByTag("f" + binding.pager.getCurrentItem());
    switch (id) {
      case R.id.menu_add:
        if (fragment != null) {
          if (fragment instanceof DrawableFragment) {
            launchPhotoPicker();
          } else if (fragment instanceof ColorFragment) {
            ((ColorFragment)fragment).addColor();
          } else if (fragment instanceof StringFragment) {
            ((StringFragment)fragment).addString();
          } else if (fragment instanceof FontFragment) {
            SBUtils.make(binding.getRoot(), "Soon...").setSlideAnimation().showAsSuccess();
          }
        } else {
          SBUtils.make(binding.getRoot(), "Something went wrong..")
              .setSlideAnimation()
              .showAsError();
        }
        break;
     case R.id.menu_viewxml:
        if (fragment != null) {
          if (fragment instanceof ColorFragment) {
            Intent it = new Intent().setClass(this, ShowXMLActivity.class);
            it.putExtra(ShowXMLActivity.EXTRA_KEY_XML, FileUtil.readFile(project.getColorsPath()));
            startActivity(it);
          } else if (fragment instanceof StringFragment) {
            Intent it = new Intent().setClass(this, ShowXMLActivity.class);
            it.putExtra(ShowXMLActivity.EXTRA_KEY_XML, FileUtil.readFile(project.getStringsPath()));
            startActivity(it);
          } else {
            SBUtils.make(binding.getRoot(), "Unavailable for this fragment..").setSlideAnimation().showAsSuccess();
          }
        } else {
          SBUtils.make(binding.getRoot(), "Something went wrong..")
              .setSlideAnimation()
              .showAsError();
        }
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private boolean isPhotoPickerAvailable() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return true;
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
      return SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2;
    else return false;
  }

  public void launchPhotoPicker() {
    if (isPhotoPickerAvailable()) {
      // Launch the photo picker and allow the user to choose only images.
      pickMedia.launch(
          new PickVisualMediaRequest.Builder()
              .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
              .build());
    } else {
      filepicker.launch("image/*");
    }
  }
}
