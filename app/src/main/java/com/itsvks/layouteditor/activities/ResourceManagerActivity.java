package com.itsvks.layouteditor.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.adapters.ResourcesPagerAdapter;
import com.itsvks.layouteditor.adapters.models.DrawableFile;
import com.itsvks.layouteditor.databinding.ActivityResourceManagerBinding;
import com.itsvks.layouteditor.R;
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
  public static final String EXTRA_KEY_PROJECT = "project";

  private ActivityResourceManagerBinding binding;
  private ProjectFile project;
  private List<DrawableFile> drawableList = new ArrayList<>();
  private ResourcesPagerAdapter adapter;
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
    adapter = new ResourcesPagerAdapter(getSupportFragmentManager(), getLifecycle());

    adapter.addFragment(new DrawableFragment(project, drawableList));
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
            if (FileUtil.isDownloadsDocument(uri)) {
              SBUtils.make(binding.getRoot(), R.string.select_from_storage).showAsError();
              return;
            }
            Fragment fragment =
                getSupportFragmentManager().findFragmentByTag("f" + binding.pager.getCurrentItem());
            if (fragment != null && fragment instanceof DrawableFragment) {
              ((DrawableFragment) fragment).addDrawable(FileUtil.convertUriToFilePath(uri));
            }
          }
        };

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
    if (menu instanceof MenuBuilder) ((MenuBuilder) menu).setOptionalIconsVisible(true);

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
            filepicker.launch("image/*");
          } else if (fragment instanceof ColorFragment) {
            SBUtils.make(binding.getRoot(), "Soon...").setSlideAnimation().showAsSuccess();
          } else if (fragment instanceof StringFragment) {
            SBUtils.make(binding.getRoot(), "Soon...").setSlideAnimation().showAsSuccess();
          } else if (fragment instanceof FontFragment) {
            SBUtils.make(binding.getRoot(), "Soon...").setSlideAnimation().showAsSuccess();
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
}
