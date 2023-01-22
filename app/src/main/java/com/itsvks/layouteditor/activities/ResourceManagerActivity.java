package com.itsvks.layouteditor.activities;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.Menu;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.adapters.DrawableResourceAdapter;
import com.itsvks.layouteditor.adapters.ResourcesPagerAdapter;
import com.itsvks.layouteditor.adapters.models.DrawableFile;
import com.itsvks.layouteditor.databinding.ActivityResourceManagerBinding;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;
import com.itsvks.layouteditor.fragments.resources.ColorFragment;
import com.itsvks.layouteditor.fragments.resources.DrawableFragment;
import com.itsvks.layouteditor.fragments.resources.FontFragment;
import com.itsvks.layouteditor.fragments.resources.StringFragment;
import com.itsvks.layouteditor.utils.FilePicker;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.NameErrorChecker;
import com.itsvks.layouteditor.utils.SBUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResourceManagerActivity extends BaseActivity {
  public static final String EXTRA_KEY_PROJECT = "project";

  private ActivityResourceManagerBinding binding;
  private ProjectFile project;
  private List<DrawableFile> drawableList = new ArrayList<>();
  private ResourcesPagerAdapter adapter;
  private DrawableResourceAdapter drawableAdapter;
  private FilePicker filepicker;

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

    project = getIntent().getParcelableExtra(EXTRA_KEY_PROJECT);
    // loadDrawables();
    drawableAdapter = new DrawableResourceAdapter(drawableList, project);
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
            if (FileUtil.isDownloadsDocument(uri)) {
              SBUtils.make(binding.getRoot(), R.string.select_from_storage).showAsError();
              return;
            }
            Fragment fragment =
                getSupportFragmentManager()
                    .findFragmentByTag(
                        "android:switcher:" + R.id.pager + ":" + binding.pager.getCurrentItem());
            if (fragment != null && fragment instanceof DrawableFragment) {
              ((DrawableFragment) fragment).addDrawable(uri);
            }
            // drawableAdapter.addDrawable(FileUtil.convertUriToFilePath(uri),
            // ResourceManagerActivity.this);
          }
        };
    adapter = new ResourcesPagerAdapter(getSupportFragmentManager(), getLifecycle());
    

    adapter.addFragment(new DrawableFragment(project, drawableList));
    adapter.addFragment(new ColorFragment());
    adapter.addFragment(new StringFragment());
    adapter.addFragment(new FontFragment());

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
                    break;
                  case 1:
                    tab.setText(R.string.color);
                    break;
                  case 2:
                    tab.setText(R.string.string);
                    break;
                  case 3:
                    tab.setText(R.string.font);
                    break;
                }
              }
            });
    mediator.attach();
  }

  public void loadDrawables() {
    File[] files = project.getDrawables();

    if (files == null) {
      ToastUtils.showLong("Null");
    } else {

      for (File file : files) {
        Drawable drawable = Drawable.createFromPath(file.getPath());
        String name = file.getName();
        // name = name.substring(0, name.lastIndexOf("."));
        drawableList.add(new DrawableFile(name, drawable, file.getPath()));
      }
    }
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
    if (menu instanceof MenuBuilder) ((MenuBuilder) menu).setOptionalIconsVisible(true);

    getMenuInflater().inflate(R.menu.menu_resource_manager, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    var id = item.getItemId();
    switch (id) {
      case R.id.menu_add:
        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.add)
            .setAdapter(
                new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    new String[] {"Drawable", "Color", "String", "Font"}),
                (d, w) -> {
                  switch (w) {
                    case 0:
                      filepicker.launch("image/*");
                      break;
                  }
                })
            .show();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
