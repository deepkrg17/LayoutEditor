package com.itsvks.editor.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.editor.Constants;
import com.itsvks.editor.adapters.ProjectAdapter;
import com.itsvks.editor.app.LayoutEditor;
import com.itsvks.editor.databinding.ActivityHomeBinding;
import com.itsvks.editor.R;
import com.itsvks.editor.databinding.LayoutCreateProjectBinding;
import com.itsvks.editor.managers.ProjectManager;
import com.itsvks.editor.models.Project;
import com.itsvks.editor.utils.FileContentReader;
import com.itsvks.editor.utils.FileUtil;
import com.itsvks.editor.utils.NameErrorChecker;
import com.itsvks.editor.utils.Utils;
import com.itsvks.editor.view.CustomBottomSheetDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends BaseActivity {
  public static final String TAG = "HomeActivity";

  private ActivityHomeBinding binding;
  private SharedPreferences projectTimes;

  private DrawerLayout drawerLayout;
  private NavigationView navigationView;
  private CoordinatorLayout contentView;

  private List<Project> projects;
  private ProjectAdapter adapter;
  private ProjectAdapter.OnItemOptionsClickListener onItemOptionsClickListener;

  private InterstitialAd interstitialAd;
  private String intermediateAdUnit1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityHomeBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    drawerLayout = binding.drawer;
    navigationView = binding.navigationView;
    contentView = binding.content;

    FileContentReader.readFileContent(
        Constants.INTERMEDIATE_AD_UNIT_1, fileContents -> intermediateAdUnit1 = fileContents);

    projectTimes = PreferenceManager.getDefaultSharedPreferences(LayoutEditor.getInstance());
    loadProjects();
    adapter = new ProjectAdapter(projects);
    onItemOptionsClickListener =
        new ProjectAdapter.OnItemOptionsClickListener() {

          @Override
          public void onDeleteProjectClick(int position) {
            deleteProject(position);
          }

          @Override
          public void onEditProjectClick(int position) {
            showBottomSheet(true, position);
          }

          @Override
          public void onPreviewLayoutClick(int position) {
            ToastUtils.make()
                .setNotUseSystemToast()
                .setMode(ToastUtils.MODE.DARK)
                .show("Soon...!!");
          }

          @Override
          public void onOpenProjectClick(int position) {
            Intent intent = new Intent(HomeActivity.this, DesignActivity.class);
            ProjectManager.getInstance().openProject(projects.get(position));

            intent.setAction(DesignActivity.ACTION_OPEN);
            startActivity(intent);
          }
        };
    adapter.setOnItemOptionsClickListener(onItemOptionsClickListener);
    binding.projectList.setAdapter(adapter);
    binding.projectList.setLayoutManager(new GridLayoutManager(this, 2));
    LayoutAnimationController animation =
        AnimationUtils.loadLayoutAnimation(this, R.anim.grid_layout_animation_from_bottom);
    binding.projectList.setLayoutAnimation(animation);

    binding.bottomAppBar.setNavigationOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });

    binding.fab.setOnClickListener(
        v -> {
          showBottomSheet(false, 0);
        });
    Utils.animateNavigationDrawer(drawerLayout, contentView);
    setBottomBarItemClickListener();
    setNavigationItemClickListener();

    MobileAds.initialize(
        this,
        new OnInitializationCompleteListener() {
          @Override
          public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
    loadAd();
  }

  private void loadProjects() {
    projects = new ArrayList<>();

    File root = new File(PathUtils.getExternalAppFilesPath() + "/Projects/");
    if (!root.exists()) root.mkdir();

    for (File file : root.listFiles()) {
      String path = file.getPath();
      projects.add(new Project(path, projectTimes.getString(path, getCurrentTime())));
    }
  }

  private void setNavigationItemClickListener() {
    navigationView.setNavigationItemSelectedListener(
        item -> {
          var id = item.getItemId();
          if (id == R.id.nav_share) {
            var shareIntent = new ShareCompat.IntentBuilder(this);
            shareIntent.setType("text/plain");
            shareIntent.setChooserTitle(getString(R.string.app_name));
            shareIntent.setText(getString(R.string.share_description, Constants.GITHUB_URL));
            shareIntent.startChooser();
            return true;
          } else if (id == R.id.nav_licence) {
            startActivity(new Intent(this, OssLicensesMenuActivity.class));
            return true;
          }
          return false;
        });
  }

  private void setBottomBarItemClickListener() {
    binding.bottomAppBar.setOnMenuItemClickListener(
        menuItem -> {
          int itemId = menuItem.getItemId();

          if (itemId == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
          } else if (itemId == R.id.github) {
            openUrl(Constants.GITHUB_URL);
            return true;
          } else if (itemId == R.id.about) {
            final String profileUrl = "https://github.com/itsvks19";
            var aboutText = "Developed by Vivek.";

            new MaterialAlertDialogBuilder(this)
                .setPositiveButton(
                    android.R.string.ok,
                    (d, w) -> {
                      d.dismiss();
                      showInterstitial();
                    })
                .setTitle(R.string.about)
                .setMessage(aboutText)
                .show();
            return true;
          }
          return false;
        });
  }

  private String getCurrentTime() {
    return Calendar.getInstance().getTime().toString();
  }

  private void createProject(String name) {
    final String projectDir = PathUtils.getExternalAppFilesPath() + "/Projects/" + name;
    final String time = getCurrentTime();

    FileUtils.createOrExistsDir(projectDir);
    FileUtil.unzipFromAssets(this, "template.zip", projectDir);

    projects.add(new Project(projectDir, time));
    adapter.notifyDataSetChanged();

    projectTimes.edit().putString(projectDir, time).apply();

    //    final Intent intent = new Intent(requireContext(), EditorActivity.class);
    //    ProjectManager.getInstance().openProject(project);
    //    startActivity(intent);
  }

  private void showBottomSheet(boolean isEdit, int position) {
    CustomBottomSheetDialog dialog = new CustomBottomSheetDialog(this);
    LayoutCreateProjectBinding createProjectBinding =
        LayoutCreateProjectBinding.inflate(dialog.getLayoutInflater());
    dialog.setContentView(createProjectBinding.getRoot());
    final TextInputEditText projectName = createProjectBinding.projectName;
    final TextInputLayout textField = createProjectBinding.textField;
    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    createProjectBinding.createProject.setOnClickListener(
        v -> {
          if (isEdit) {
            String path = projects.get(position).getPath();
            String newPath =
                path.substring(0, path.lastIndexOf("/")) + "/" + projectName.getText().toString();
            projects.get(position).rename(newPath);
            adapter.notifyItemChanged(position);
          } else {
            createProject(projectName.getText().toString());
          }
          dialog.dismiss();
        });
    if (isEdit) {
      projectName.setText(projects.get(position).getName());
      createProjectBinding.createProject.setText(R.string.save);
      createProjectBinding.title.setText(R.string.edit_project);
      textField.setHint(R.string.msg_new_project_name);
    } else {
      projectName.setText("NewProject" + System.currentTimeMillis());
    }
    projectName.addTextChangedListener(
        new TextWatcher() {

          @Override
          public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

          @Override
          public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {}

          @Override
          public void afterTextChanged(Editable p1) {
            if (isEdit) {
              NameErrorChecker.checkForCreateOrRenameProject(
                  projectName.getText().toString(),
                  projects.get(position).getName(),
                  projects,
                  textField,
                  createProjectBinding.createProject);
            } else {
              NameErrorChecker.checkForCreateOrRenameProject(
                  projectName.getText().toString(),
                  null,
                  projects,
                  textField,
                  createProjectBinding.createProject);
            }
          }
        });

    projectName.requestFocus();

    InputMethodManager inputMethodManager =
        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.showSoftInput(projectName, InputMethodManager.SHOW_IMPLICIT);

    if (!projectName.getText().toString().isEmpty()) {
      projectName.setSelection(0, projectName.getText().toString().length());
    }

    if (isEdit) {
      NameErrorChecker.checkForCreateOrRenameProject(
          projectName.getText().toString(),
          projects.get(position).getName(),
          projects,
          textField,
          createProjectBinding.createProject);
    } else {
      NameErrorChecker.checkForCreateOrRenameProject(
          projectName.getText().toString(),
          "",
          projects,
          textField,
          createProjectBinding.createProject);
    }
    dialog.show();
  }

  private void deleteProject(int position) {
    new MaterialAlertDialogBuilder(HomeActivity.this)
        .setTitle(R.string.delete_project)
        .setMessage(R.string.msg_delete_project)
        .setNegativeButton(R.string.no, (d, w) -> d.dismiss())
        .setPositiveButton(
            R.string.yes,
            (d, w) -> {
              FileUtils.delete(projects.get(position).getPath());
              projects.remove(projects.get(position));
              adapter.notifyDataSetChanged();
            })
        .show();
  }

  public void loadAd() {
    AdRequest adRequest = new AdRequest.Builder().build();
    InterstitialAd.load(
        HomeActivity.this,
        intermediateAdUnit1,
        adRequest,
        new InterstitialAdLoadCallback() {
          @Override
          public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
            // The mInterstitialAd reference will be null until
            // an ad is loaded.
            HomeActivity.this.interstitialAd = interstitialAd;
            ToastUtils.showShort("onAdLoaded()");
            interstitialAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                  @Override
                  public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    HomeActivity.this.interstitialAd = null;
                    ToastUtils.showShort("The ad was dismissed.");
                  }

                  @Override
                  public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    HomeActivity.this.interstitialAd = null;
                    ToastUtils.showShort("The ad failed to show.");
                  }

                  @Override
                  public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    ToastUtils.showShort("The ad was shown.");
                  }
                });
          }

          @Override
          public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            // Handle the error
            Log.i(TAG, loadAdError.getMessage());
            interstitialAd = null;

            String error =
                String.format(
                    "domain: %s, code: %d, message: %s",
                    loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
            Toast.makeText(
                    HomeActivity.this,
                    "onAdFailedToLoad() with error: " + error,
                    Toast.LENGTH_SHORT)
                .show();
          }
        });
  }

  private void showInterstitial() {
    // Show the ad if it's ready. Otherwise toast and load the ad.
    if (interstitialAd != null) {
      interstitialAd.show(this);
    } else {
      Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
      loadAd();
    }
  }
}
