package com.itsvks.editor.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.blankj.utilcode.util.ToastUtils;
import com.itsvks.editor.databinding.ActivityDesignBinding;
import com.itsvks.editor.designeditor.DeviceSize;
import com.itsvks.editor.designeditor.ViewType;
import com.itsvks.editor.managers.ProjectManager;
import com.itsvks.editor.managers.UndoRedoManager;
import com.itsvks.editor.models.Project;
import com.itsvks.editor.utils.Utils;
import com.itsvks.editor.R;
import com.itsvks.editor.view.ComponentTree;
import java.io.File;

public class DesignActivity extends BaseActivity {
  public static final String ACTION_OPEN = "com.itsvks.editor.open";
  
  private ActivityDesignBinding binding;

  private DrawerLayout drawerLayout;
  private ActionBarDrawerToggle actionBarDrawerToggle;
  
  private Project project;
  private ProjectManager projectManager;
  
  private UndoRedoManager undoRedo;
  private MenuItem undo = null;
  private MenuItem redo = null;
  
  final Runnable updateMenuIconsState = () -> undoRedo.updateButtons();
  
  @Override
  protected void onSaveInstanceState(Bundle outstate) {
    super.onSaveInstanceState(outstate);
    outstate.putParcelable("project", project);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityDesignBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(binding.topAppBar);
    drawerLayout = binding.drawer;
    projectManager = ProjectManager.getInstance();
    
    if (savedInstanceState != null && savedInstanceState.getParcelable("project") != null) {
      projectManager.openProject(savedInstanceState.getParcelable("project"));
    }
    project = projectManager.getOpenedProject();

    getSupportActionBar().setTitle(getString(R.string.app_name));
    getSupportActionBar().setSubtitle(project.getName());

    actionBarDrawerToggle =
        new ActionBarDrawerToggle(
            this, drawerLayout, binding.topAppBar, R.string.app_name, R.string.app_name);

    drawerLayout.addDrawerListener(actionBarDrawerToggle);
    actionBarDrawerToggle.syncState();
    setBottomBarItemClickListener();
    setupComponentTree();
    // Utils.animateNavigationDrawer(drawerLayout, binding.content);
    if (getIntent().getAction() != null && getIntent().getAction().equals(ACTION_OPEN)) {
      binding.editorLayout.setLayoutFile(new File(project.getMainLayoutPath()));
    }
    
    binding.fab.setOnClickListener(v -> {
      ToastUtils.showShort("Palette");
    });
  }

  private void setBottomBarItemClickListener() {
    binding.bottomAppBar.setOnMenuItemClickListener(
        menuItem -> {
          var id = menuItem.getItemId();
          if (id == R.id.view_type_design) {
            binding.editorLayout.setViewType(ViewType.DESIGN);
            return true;
          } else if (id == R.id.view_type_blueprint) {
            binding.editorLayout.setViewType(ViewType.BLUEPRINT);
            return true;
          } else if (id == R.id.device_size_small) {
            binding.editorLayout.setSize(DeviceSize.SMALL);
            return true;
          } else if (id == R.id.device_size_medium) {
            binding.editorLayout.setSize(DeviceSize.MEDIUM);
            return true;
          } else if (id == R.id.device_size_large) {
            binding.editorLayout.setSize(DeviceSize.LARGE);
            return true;
          }
          return false;
        });
  }
  
  private void setupComponentTree() {
    binding.editorLayout.setComponentTree(binding.componentTree);

    binding.componentTree.setOnItemClickListener(
        new ComponentTree.OnItemClickListener() {

          @Override
          public void onItemClick(View view) {
            // binding.editorLayout.showDefinedAttributes(view);
            drawerLayout.closeDrawer(GravityCompat.END);
          }
        });
  }

  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerVisible(GravityCompat.START))
      drawerLayout.closeDrawer(GravityCompat.START);
    else super.onBackPressed();
  }

  @Override
  public void onConfigurationChanged(Configuration config) {
    super.onConfigurationChanged(config);
    actionBarDrawerToggle.onConfigurationChanged(config);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
    var id = item.getItemId();
    if (id == R.id.component_tree) {
      drawerLayout.openDrawer(GravityCompat.END);
      return true;
    }
    return false;
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    actionBarDrawerToggle.syncState();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.editor_menu, menu);
    undo = menu.findItem(R.id.undo);
    redo = menu.findItem(R.id.redo);
    undoRedo = new UndoRedoManager(undo, redo);
    if (undoRedo != null) binding.editorLayout.setUndoRedoManager(undoRedo);
    binding.editorLayout.updateUndoRedoHistory();
    updateUndoRedoBtnState();
    return super.onCreateOptionsMenu(menu);
  }

  public void updateUndoRedoBtnState() {
    new Handler(Looper.getMainLooper()).postDelayed(updateMenuIconsState, 10);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
    ProjectManager.getInstance().closeProject();
  }
}
