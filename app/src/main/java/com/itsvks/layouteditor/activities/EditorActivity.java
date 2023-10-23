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
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.adapters.PaletteListAdapter;
import com.itsvks.layouteditor.databinding.ActivityEditorBinding;
import com.itsvks.layouteditor.editor.DesignEditor;
import com.itsvks.layouteditor.editor.DeviceConfiguration;
import com.itsvks.layouteditor.editor.DeviceSize;
import com.itsvks.layouteditor.editor.convert.ConvertImportedXml;
import com.itsvks.layouteditor.managers.DrawableManager;
import com.itsvks.layouteditor.managers.IdManager;
import com.itsvks.layouteditor.managers.ProjectManager;
import com.itsvks.layouteditor.managers.UndoRedoManager;
import com.itsvks.layouteditor.tools.XmlLayoutGenerator;
import com.itsvks.layouteditor.utils.BitmapUtil;
import com.itsvks.layouteditor.utils.Constants;
import com.itsvks.layouteditor.utils.FileCreator;
import com.itsvks.layouteditor.utils.FilePicker;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.SBUtils;
import com.itsvks.layouteditor.utils.Utils;
import com.itsvks.layouteditor.views.StructureView;

@SuppressLint("UnsafeOptInUsageError")
public class EditorActivity extends BaseActivity {

  public static final String ACTION_OPEN = "com.itsvks.layouteditor.open";
  public static final int PICK_XML_FILE_REQUEST = 2255;

  private ActivityEditorBinding binding;

  private DrawerLayout drawerLayout;
  private LinearLayoutCompat contentView;
  private ActionBarDrawerToggle actionBarDrawerToggle;

  private ProjectManager projectManager;
  private ProjectFile project;

  private UndoRedoManager undoRedo;
  private FileCreator fileCreator;
  private FilePicker xmlPicker;
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

    projectManager = ProjectManager.getInstance();
    var extras = getIntent().getExtras();
    if (extras != null && extras.containsKey(Constants.EXTRA_KEY_PROJECT)) {
      ProjectFile projectFile = (ProjectFile) extras.getParcelable(Constants.EXTRA_KEY_PROJECT);
      projectManager.openProject(projectFile);
    }
    project = projectManager.getOpenedProject();

    getSupportActionBar().setTitle(getString(R.string.app_name));
    getSupportActionBar().setSubtitle(project.getName());

    contentView = binding.content;
    binding.editorLayout.setBackgroundColor(Utils.getSurfaceColor(this));

    defineFileCreator();
    defineXmlPicker();
    setupDrawerLayout();
    setupStructureView();

    setupDrawerNavigationRail();
    if (getIntent().getAction() != null && getIntent().getAction().equals(ACTION_OPEN)) {
      binding.editorLayout.loadLayoutFromParser(project.getLayout());
    }
    setToolbarButtonOnClickListener(binding);
  }

  private void defineXmlPicker() {
    xmlPicker =
        new FilePicker(this) {
          @Override
          public void onPickFile(Uri uri) {
            if (uri == null) {
              SBUtils.make(binding.getRoot(), "No xml selected").setFadeAnimation().show();
            } else {
              if (FileUtil.isDownloadsDocument(uri)) {
                SBUtils.make(binding.getRoot(), R.string.select_from_storage).showAsError();
                return;
              }
              String path = uri.getPath();
              if (path != null && path.endsWith(".xml")) {
                String xml = FileUtil.readFromUri(uri, EditorActivity.this);
                String xmlConverted =
                    new ConvertImportedXml(xml).getXmlConverted(EditorActivity.this);

                if (xmlConverted != null) {
                  binding.editorLayout.loadLayoutFromParser(xmlConverted);
                  SBUtils.make(binding.getRoot(), "Imported").setFadeAnimation().showAsSuccess();
                } else {
                  SBUtils.make(binding.getRoot(), "Failed to import!")
                      .setSlideAnimation()
                      .showAsError();
                }
              } else {
                Toast.makeText(
                        EditorActivity.this,
                        "Selected file is not an Android XML layout file",
                        Toast.LENGTH_SHORT)
                    .show();
              }
            }
          }
        };
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
            this, drawerLayout, binding.topAppBar, R.string.palette, R.string.palette);

    drawerLayout.addDrawerListener(actionBarDrawerToggle);
    actionBarDrawerToggle.syncState();
    drawerLayout.addDrawerListener(
        new DrawerLayout.SimpleDrawerListener() {

          @Override
          public void onDrawerStateChanged(int state) {
            super.onDrawerStateChanged(state);
            undoRedo.updateButtons();
          }

          @Override
          public void onDrawerSlide(View v, float slideOffset) {
            super.onDrawerSlide(v, slideOffset);
            undoRedo.updateButtons();
          }

          @Override
          public void onDrawerClosed(View v) {
            super.onDrawerClosed(v);
            undoRedo.updateButtons();
          }

          @Override
          public void onDrawerOpened(View v) {
            super.onDrawerOpened(v);
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

  private void setupDrawerNavigationRail() {
    var menu = binding.navigation.getMenu();
    menu.add(Menu.NONE, 0, Menu.NONE, Constants.TAB_TITLE_COMMON).setIcon(R.drawable.android);
    menu.add(Menu.NONE, 1, Menu.NONE, Constants.TAB_TITLE_TEXT)
        .setIcon(R.mipmap.ic_palette_text_view);
    menu.add(Menu.NONE, 2, Menu.NONE, Constants.TAB_TITLE_BUTTONS)
        .setIcon(R.mipmap.ic_palette_button);
    menu.add(Menu.NONE, 3, Menu.NONE, Constants.TAB_TITLE_WIDGETS)
        .setIcon(R.mipmap.ic_palette_view);
    menu.add(Menu.NONE, 4, Menu.NONE, Constants.TAB_TITLE_LAYOUTS)
        .setIcon(R.mipmap.ic_palette_relative_layout);
    menu.add(Menu.NONE, 5, Menu.NONE, Constants.TAB_TITLE_CONTAINERS)
        .setIcon(R.mipmap.ic_palette_view_pager);
    menu.add(Menu.NONE, 6, Menu.NONE, Constants.TAB_TITLE_LEGACY)
        .setIcon(R.mipmap.ic_palette_grid_layout);

    PaletteListAdapter adapter = new PaletteListAdapter(binding.drawer);
    adapter.submitPaletteList(projectManager.getPalette(0));

    binding.navigation.setOnItemSelectedListener(
        item -> {
          adapter.submitPaletteList(projectManager.getPalette(item.getItemId()));
          binding.title.setText(item.getTitle());
          return true;
        });
    binding.listView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    binding.listView.setAdapter(adapter);
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
                .putExtra(Constants.EXTRA_KEY_PROJECT, project));

        return true;
      case R.id.preview:
        String result = new XmlLayoutGenerator().generate(binding.editorLayout, true);
        if (result.isEmpty()) showNothingDialog();
        else {
          saveXml();
          startActivity(
              new Intent(this, PreviewLayoutActivity.class)
                  .putExtra(Constants.EXTRA_KEY_PROJECT, project));
        }
        return true;
      case R.id.export_xml:
        fileCreator.create(projectManager.getFormattedProjectName(), "text/xml");
        return true;
      case R.id.export_as_image:
        if (binding.editorLayout.getChildAt(0) != null)
          showSaveMessage(
              Utils.saveBitmapAsImageToGallery(
                  this, BitmapUtil.createBitmapFromView(binding.editorLayout), project.getName()));
        else
          SBUtils.make(binding.getRoot(), "Add some views...")
              .setFadeAnimation()
              .setType(SBUtils.Type.INFO)
              .show();
        return true;
      case R.id.import_xml:
        xmlPicker.launch("text/xml");
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
    ProjectManager.getInstance().closeProject();
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

  private void showSaveMessage(boolean success) {
    if (success)
      SBUtils.make(binding.getRoot(), "Saved to gallery.")
          .setFadeAnimation()
          .setType(SBUtils.Type.INFO)
          .show();
    else
      SBUtils.make(binding.getRoot(), "Failed to save...")
          .setFadeAnimation()
          .setType(SBUtils.Type.ERROR)
          .show();
  }

  private void setToolbarButtonOnClickListener(ActivityEditorBinding binding) {
    TooltipCompat.setTooltipText(binding.viewType, "View Type");
    TooltipCompat.setTooltipText(binding.deviceSize, "Size");
    binding.viewType.setOnClickListener(
        v -> {
          final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
          popupMenu.inflate(R.menu.menu_view_type);
          popupMenu.setOnMenuItemClickListener(
              new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {

                  var id = item.getItemId();
                  switch (id) {
                    case R.id.view_type_design:
                      binding.editorLayout.setViewType(DesignEditor.ViewType.DESIGN);
                      break;
                    case R.id.view_type_blueprint:
                      binding.editorLayout.setViewType(DesignEditor.ViewType.BLUEPRINT);
                      break;
                  }
                  return true;
                }
              });

          popupMenu.show();
        });
    binding.deviceSize.setOnClickListener(
        v -> {
          final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
          popupMenu.inflate(R.menu.menu_device_size);
          popupMenu.setOnMenuItemClickListener(
              new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {

                  var id = item.getItemId();
                  switch (id) {
                    case R.id.device_size_small:
                      binding.editorLayout.resizeLayout(new DeviceConfiguration(DeviceSize.SMALL));
                      break;
                    case R.id.device_size_medium:
                      binding.editorLayout.resizeLayout(new DeviceConfiguration(DeviceSize.MEDIUM));
                      break;
                    case R.id.device_size_large:
                      binding.editorLayout.resizeLayout(new DeviceConfiguration(DeviceSize.LARGE));
                      break;
                  }
                  return true;
                }
              });

          popupMenu.show();
        });
  }
}
