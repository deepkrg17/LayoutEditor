package com.itsvks.layouteditor.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.adapters.WidgetListAdapter;
import com.itsvks.layouteditor.databinding.ActivityEditorBinding;
import com.itsvks.layouteditor.managers.DrawableManager;
import com.itsvks.layouteditor.managers.IdManager;
import com.itsvks.layouteditor.managers.UndoRedoManager;
import com.itsvks.layouteditor.tools.XmlLayoutGenerator;
import com.itsvks.layouteditor.utils.Constants;
import com.itsvks.layouteditor.utils.FileCreator;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.SBUtils;
import com.itsvks.layouteditor.views.StructureView;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("UnsafeOptInUsageError")
public class EditorActivity extends BaseActivity
    implements WidgetListAdapter.WidgetListClickListener {

  public static final String EXTRA_KEY_PROJECT = "project";
  public static final String ACTION_OPEN = "com.itsvks.layouteditor.open";

  private ActivityEditorBinding binding;

  private DrawerLayout drawerLayout;
  private LinearLayoutCompat contentView;
  private ActionBarDrawerToggle actionBarDrawerToggle;

  private ArrayList<HashMap<String, Object>> views;
  private ArrayList<HashMap<String, Object>> layouts;
  private ArrayList<HashMap<String, Object>> androidxWidgets;
  private ArrayList<HashMap<String, Object>> materialDesignWidgets;

  private ProjectFile project;

  private UndoRedoManager undoRedo;
  private FileCreator fileCreator;
  private MenuItem undo = null;
  private MenuItem redo = null;

  final Runnable updateMenuIconsState = () -> undoRedo.updateButtons();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    init();
  }

  @SuppressWarnings("deprecation")
  private void init() {
    binding = ActivityEditorBinding.inflate(getLayoutInflater());

    setContentView(binding.getRoot());
    setSupportActionBar(binding.topAppBar);

    project = getIntent().getParcelableExtra(EXTRA_KEY_PROJECT);
    getSupportActionBar().setTitle(getString(R.string.app_name));
    getSupportActionBar().setSubtitle(project.getName());

    contentView = binding.content;

    defineFileCreator();
    setupDrawerLayout();
    setupStructureView();
    initializeWidgetLists();
    setupDrawerTab();
    if (getIntent().getAction() != null && getIntent().getAction().equals(ACTION_OPEN)) {
      DrawableManager.loadFromFiles(project.getDrawables());
      binding.editorLayout.loadLayoutFromParser(project.getLayout());
    }
  }

  private void defineFileCreator() {
    fileCreator =
        new FileCreator(this) {

          @Override
          public void onCreateFile(Uri uri) {
            String result = new XmlLayoutGenerator().generate(binding.editorLayout, true);

            if (uri != null) {
              if (FileUtil.saveFile(uri, result))
                SBUtils.make(binding.getRoot(), "Success!").setSlideAnimation().showAsSuccess();
              else {
                SBUtils.make(binding.getRoot(), "Failed to save!")
                    .setSlideAnimation()
                    .showAsError();
                FileUtil.deleteFile(FileUtil.convertUriToFilePath(uri));
              }
            } else
              SBUtils.make(binding.getRoot(), "Failed to export!")
                  .setSlideAnimation()
                  .showAsError();
          }
        };
  }

  private void setupDrawerLayout() {
    drawerLayout = binding.drawer;
    actionBarDrawerToggle =
        new ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.topAppBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);

    drawerLayout.addDrawerListener(actionBarDrawerToggle);
    actionBarDrawerToggle.syncState();
    drawerLayout.addDrawerListener(
        new DrawerLayout.SimpleDrawerListener() {

          @Override
          public void onDrawerStateChanged(int arg0) {
            super.onDrawerStateChanged(arg0);
            undoRedo.updateButtons();
          }

          @Override
          public void onDrawerSlide(View arg0, float arg1) {
            super.onDrawerSlide(arg0, arg1);
            undoRedo.updateButtons();
          }

          @Override
          public void onDrawerClosed(View arg0) {
            super.onDrawerClosed(arg0);
            undoRedo.updateButtons();
          }

          @Override
          public void onDrawerOpened(View arg0) {
            super.onDrawerOpened(arg0);
            undoRedo.updateButtons();
          }
        });
  }

  private void setupStructureView() {
    binding.editorLayout.setStructureView(binding.structureView);

    binding.structureView.setOnItemClickListener(
        new StructureView.OnItemClickListener() {

          @Override
          public void onItemClick(View view) {
            binding.editorLayout.showDefinedAttributes(view);
            drawerLayout.closeDrawer(GravityCompat.END);
          }
        });
  }

  private void setupDrawerTab() {
    addDrawerTab(Constants.TAB_TITLE_VIEWS);
    addDrawerTab(Constants.TAB_TITLE_LAYOUTS);
    addDrawerTab(Constants.TAB_TITLE_ANDROIDX);
    addDrawerTab(Constants.TAB_TITLE_MATERIAL);
    binding.tabLayout.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {

          @Override
          public void onTabSelected(TabLayout.Tab tab) {
            if (tab.getPosition() == 0)
              binding.listView.setAdapter(new WidgetListAdapter(views, EditorActivity.this));
            else if (tab.getPosition() == 1)
              binding.listView.setAdapter(new WidgetListAdapter(layouts, EditorActivity.this));
            else if (tab.getPosition() == 2)
              binding.listView.setAdapter(
                  new WidgetListAdapter(androidxWidgets, EditorActivity.this));
            else if (tab.getPosition() == 3)
              binding.listView.setAdapter(
                  new WidgetListAdapter(materialDesignWidgets, EditorActivity.this));
          }

          @Override
          public void onTabUnselected(TabLayout.Tab tab) {}

          @Override
          public void onTabReselected(TabLayout.Tab tab) {}
        });
    binding.listView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

    binding.listView.setAdapter(new WidgetListAdapter(views, this));

    IdManager.clear();
  }

  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerVisible(GravityCompat.START)
        || drawerLayout.isDrawerVisible(GravityCompat.END)) drawerLayout.closeDrawers();
    else {
      String result = new XmlLayoutGenerator().generate(binding.editorLayout, true);
      if (!result.isEmpty()) {
        saveXml();
        super.onBackPressed();
      } else super.onBackPressed();
    }
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    var id = item.getItemId();
    undoRedo.updateButtons();
    if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
    switch (id) {
      case android.R.id.home:
        drawerLayout.openDrawer(GravityCompat.START);
        return true;
      case R.id.undo:
        binding.editorLayout.undo();
        return true;
      case R.id.redo:
        binding.editorLayout.redo();
        return true;
      case R.id.show_structure:
        drawerLayout.openDrawer(GravityCompat.END);
        return true;
      case R.id.save_xml:
        saveXml();
        return true;
      case R.id.edit_xml:
        showXml();
        return true;
      case R.id.resources_manager:
        saveXml();
        startActivity(
            new Intent(this, ResourceManagerActivity.class)
                .putExtra(ResourceManagerActivity.EXTRA_KEY_PROJECT, project));

        return true;
      case R.id.preview:
        String result = new XmlLayoutGenerator().generate(binding.editorLayout, false);
        if (result.isEmpty()) showNothingDialog();
        else {
          saveXml();
          startActivity(
              new Intent(this, PreviewLayoutActivity.class)
                  .putExtra(PreviewLayoutActivity.EXTRA_KEY_XML, result));
        }
        return true;
      case R.id.export_xml:
        fileCreator.create(project.getName().concat(".xml"), "text/xml");
        return true;
      default:
        return false;
    }
  }

  @Override
  public void onConfigurationChanged(Configuration config) {
    super.onConfigurationChanged(config);
    actionBarDrawerToggle.onConfigurationChanged(config);
    undoRedo.updateButtons();
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    actionBarDrawerToggle.syncState();
    if (undoRedo != null) undoRedo.updateButtons();
  }

  @Override
  protected void onResume() {
    super.onResume();
    DrawableManager.loadFromFiles(project.getDrawables());
    if (undoRedo != null) undoRedo.updateButtons();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  private void saveXml() {

    if (project == null) return;

    if (binding.editorLayout.getChildCount() == 0) {
      project.saveLayout("");
      SBUtils.make(binding.getRoot(), R.string.project_empty).setSlideAnimation().showLongAsError();
      return;
    }

    String result = new XmlLayoutGenerator().generate(binding.editorLayout, false);
    project.saveLayout(result);
    ToastUtils.showShort(getString(R.string.project_saved));
  }

  private void showXml() {
    String result = new XmlLayoutGenerator().generate(binding.editorLayout, true);
    if (result.isEmpty()) {
      showNothingDialog();
    } else {
      saveXml();
      startActivity(
          new Intent(this, ShowXMLActivity.class).putExtra(ShowXMLActivity.EXTRA_KEY_XML, result));
    }
  }

  private void showNothingDialog() {
    new MaterialAlertDialogBuilder(this)
        .setTitle(R.string.nothing)
        .setMessage(R.string.msg_add_some_widgets)
        .setPositiveButton(R.string.okay, (d, w) -> d.cancel())
        .show();
  }

  @SuppressLint("RestrictedApi")
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (menu instanceof MenuBuilder) ((MenuBuilder) menu).setOptionalIconsVisible(true);

    getMenuInflater().inflate(R.menu.menu_editor, menu);
    undo = menu.findItem(R.id.undo);
    redo = menu.findItem(R.id.redo);
    undoRedo = new UndoRedoManager(undo, redo);
    if (undoRedo != null) binding.editorLayout.bindUndoRedoManager(undoRedo);
    binding.editorLayout.updateUndoRedoHistory();
    updateUndoRedoBtnState();
    return super.onCreateOptionsMenu(menu);
  }

  public void updateUndoRedoBtnState() {
    new Handler(Looper.getMainLooper()).postDelayed(updateMenuIconsState, 10);
  }

  private void addDrawerTab(CharSequence title) {
    binding.tabLayout.addTab(binding.tabLayout.newTab().setText(title));
  }

  private void initializeWidgetLists() {
    views =
        new Gson()
            .fromJson(
                FileUtil.readFromAsset(Constants.VIEWS_FILE, this),
                new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
    layouts =
        new Gson()
            .fromJson(
                FileUtil.readFromAsset(Constants.LAYOUTS_FILE, this),
                new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());

    materialDesignWidgets =
        new Gson()
            .fromJson(
                FileUtil.readFromAsset(Constants.MATERIAL_DESIGN_WIDGETS_FILE, this),
                new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
    androidxWidgets =
        new Gson()
            .fromJson(
                FileUtil.readFromAsset(Constants.ANDROIDX_WIDGETS_FILE, this),
                new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
  }

  @Override
  public boolean onWidgetLongClicked(View view, HashMap<String, Object> widgetItem) {
    view.startDragAndDrop(null, new View.DragShadowBuilder(view), widgetItem, 0);
    binding.drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}
