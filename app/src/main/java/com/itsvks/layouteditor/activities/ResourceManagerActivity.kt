package com.itsvks.layouteditor.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ext.SdkExtensions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.adapters.PagerAdapter;
import com.itsvks.layouteditor.adapters.models.DrawableFile;
import com.itsvks.layouteditor.databinding.ActivityResourceManagerBinding;
import com.itsvks.layouteditor.fragments.resources.ColorFragment;
import com.itsvks.layouteditor.fragments.resources.DrawableFragment;
import com.itsvks.layouteditor.fragments.resources.FontFragment;
import com.itsvks.layouteditor.fragments.resources.StringFragment;
import com.itsvks.layouteditor.managers.ProjectManager;
import com.itsvks.layouteditor.utils.Constants;
import com.itsvks.layouteditor.utils.FilePicker;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.SBUtils;
import com.itsvks.layouteditor.vectormaster.VectorMasterDrawable;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ResourceManagerActivity extends BaseActivity {

  private ActivityResourceManagerBinding binding;
  private List<DrawableFile> drawableList = new ArrayList<>();
  private PagerAdapter adapter;
  private FilePicker photoPicker;
  private FilePicker fontPicker;
  private FilePicker xmlPicker;
  private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
  private ActivityResultLauncher<String> requestPermission;

  @SuppressWarnings("deprecation")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityResourceManagerBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(binding.topAppBar);
    getSupportActionBar().setTitle(R.string.res_manager);

    binding.topAppBar.setNavigationOnClickListener(__ -> onBackPressed());

    if (ProjectManager.getInstance().getOpenedProject() == null) {
      var extras = getIntent().getExtras();
      if (extras != null && extras.containsKey(Constants.EXTRA_KEY_PROJECT)) {
        ProjectManager.getInstance()
            .openProject((ProjectFile) extras.getParcelable(Constants.EXTRA_KEY_PROJECT));
      }
    }
    // loadDrawables();
    adapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle());
    adapter.setup(binding.pager, binding.tabLayout);
    adapter.addFragmentToAdapter(new DrawableFragment(drawableList), getString(R.string.drawable), getDrawable(R.drawable.image_outline));
    adapter.addFragmentToAdapter(new ColorFragment(), getString(R.string.color), getDrawable(R.drawable.palette_outline));
    adapter.addFragmentToAdapter(new StringFragment(), getString(R.string.string), getDrawable(R.drawable.format_letter_case));
    adapter.addFragmentToAdapter(new FontFragment(), getString(R.string.font), getDrawable(R.drawable.format_font));
    adapter.setupPager(ViewPager2.ORIENTATION_HORIZONTAL);
    adapter.setupMediatorWithIcon();
        
    requestPermission =
        registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), this::onRequestPermission);
    photoPicker =
        new FilePicker(this) {
          @Override
          public void onPickFile(Uri uri) {
            onPickPhoto(uri);
          }
        };
    fontPicker =
        new FilePicker(this) {
          @Override
          public void onPickFile(Uri uri) {
            onPickFont(uri);
          }
        };
    xmlPicker =
        new FilePicker(this) {
          @Override
          public void onPickFile(Uri uri) {
            onPickXml(uri);
          }
        };
    pickMedia = registerForActivityResult(new PickVisualMedia(), this::onPickPhoto);
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
            new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.select_drawable_type)
                .setAdapter(
                    new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        new String[] {"Vector Drawable", "Image Drawable"}),
                    (d, w) -> {
                      switch (w) {
                        case 0:
                          xmlPicker.launch("text/xml");
                          break;
                        case 1:
                          launchPhotoPicker();
                          break;
                      }
                    })
                .show();
          } else if (fragment instanceof ColorFragment) {
            ((ColorFragment) fragment).addColor();
          } else if (fragment instanceof StringFragment) {
            ((StringFragment) fragment).addString();
          } else if (fragment instanceof FontFragment) {
            fontPicker.launch("font/*");
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
            it.putExtra(ShowXMLActivity.EXTRA_KEY_XML, ProjectManager.getInstance().getColorsXml());
            startActivity(it);
          } else if (fragment instanceof StringFragment) {
            Intent it = new Intent().setClass(this, ShowXMLActivity.class);
            it.putExtra(
                ShowXMLActivity.EXTRA_KEY_XML, ProjectManager.getInstance().getStringsXml());
            startActivity(it);
          } else {
            SBUtils.make(binding.getRoot(), "Unavailable for this fragment..")
                .setSlideAnimation()
                .showAsSuccess();
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

  private void launchPhotoPicker() {
    if (isPhotoPickerAvailable()) {
      if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
          == PackageManager.PERMISSION_DENIED) {
        requestPermission.launch(Manifest.permission.READ_MEDIA_IMAGES);
        return;
      }
      // Launch the photo picker and allow the user to choose only images.
      pickMedia.launch(
          new PickVisualMediaRequest.Builder()
              .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
              .build());
    } else {
      photoPicker.launch("image/*");
    }
  }

  private void onPickPhoto(Uri uri) {
    if (uri != null) {
      Log.d("PhotoPicker", "Selected URI: " + uri);
      if (FileUtil.isDownloadsDocument(uri)) {
        SBUtils.make(binding.getRoot(), R.string.select_from_storage).showAsError();
        return;
      }
      Fragment fragment =
          getSupportFragmentManager().findFragmentByTag("f" + binding.pager.getCurrentItem());
      if (fragment != null && fragment instanceof DrawableFragment) {
        ((DrawableFragment) fragment).addDrawable(uri);
      }
    } else {
      Log.d("PhotoPicker", "No media selected");
      SBUtils.make(binding.getRoot(), "No image selected").setFadeAnimation().show();
    }
  }

  private void onPickFont(Uri uri) {
    if (uri != null) {
      Log.d("FontPicker", "Selected URI: " + uri);
      if (FileUtil.isDownloadsDocument(uri)) {
        SBUtils.make(binding.getRoot(), R.string.select_from_storage).showAsError();
        return;
      }
      Fragment fragment =
          getSupportFragmentManager().findFragmentByTag("f" + binding.pager.getCurrentItem());
      if (fragment != null && fragment instanceof FontFragment) {
        ((FontFragment) fragment).addFont(uri);
      }
    } else {
      Log.d("FontPicker", "No font selected");
      SBUtils.make(binding.getRoot(), "No font selected").setFadeAnimation().show();
    }
  }

  private void onPickXml(Uri uri) {
    if (uri != null) {
      Log.d("DrawablePicker", "Selected URI: " + uri);
      if (FileUtil.isDownloadsDocument(uri)) {
        SBUtils.make(binding.getRoot(), R.string.select_from_storage).showAsError();
        return;
      }
      Fragment fragment =
          getSupportFragmentManager().findFragmentByTag("f" + binding.pager.getCurrentItem());
      if (fragment != null && fragment instanceof DrawableFragment) {
        try {
          var drawable = new VectorMasterDrawable(this);
          drawable.setInputStream(getContentResolver().openInputStream(uri));
          if (drawable.isVector()) ((DrawableFragment) fragment).addDrawable(uri);
          else
            SBUtils.make(binding.getRoot(), "Not a valid vector drawable")
                .setFadeAnimation()
                .setType(SBUtils.Type.INFO)
                .show();
        } catch (FileNotFoundException e) {
          e.printStackTrace();
          ToastUtils.showShort(e.toString());
        }
      }
    } else {
      Log.d("DrawablePicker", "No drawable selected");
      SBUtils.make(binding.getRoot(), "No drawable selected").setFadeAnimation().show();
    }
  }

  public void onRequestPermission(boolean isGranted) {
    if (isGranted)
      SBUtils.make(findViewById(android.R.id.content), R.string.permission_granted)
          .setSlideAnimation()
          .showAsSuccess();
    else
      SBUtils.make(findViewById(android.R.id.content), R.string.permission_denied)
          .setSlideAnimation()
          .showAsError();
  }
}
