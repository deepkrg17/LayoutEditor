package com.itsvks.layouteditor.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.FitWindowsLinearLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.databinding.ActivityEditorBinding;
import com.itsvks.layouteditor.databinding.WidgetsListBinding;
import com.itsvks.layouteditor.managers.DrawableManager;
import com.itsvks.layouteditor.managers.IdManager;
import com.itsvks.layouteditor.managers.UndoRedoManager;
import com.itsvks.layouteditor.tools.XmlLayoutGenerator;
import com.itsvks.layouteditor.utils.Constants;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.InvokeUtil;
import com.itsvks.layouteditor.utils.SBUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class EditorActivity extends BaseActivity {

    public static final String EXTRA_KEY_PROJECT = "project";
    public static final String ACTION_OPEN = "com.itsvks.layouteditor.open";

    private ActivityEditorBinding binding;

    private DrawerLayout drawerLayout;
    private FitWindowsLinearLayout contentView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private ArrayList<HashMap<String, Object>> views;
    private ArrayList<HashMap<String, Object>> layouts;

    private ProjectFile project;

    private UndoRedoManager undoRedo;
    private MenuItem undo = null;
    private MenuItem redo = null;

    final Runnable updateMenuIconsState = () -> setupMenuIcons();

    @SuppressWarnings("deprecation")
    @SuppressLint("UnsafeOptInUsageError")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditorBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.topAppBar);

        project = getIntent().getParcelableExtra(EXTRA_KEY_PROJECT);
        getSupportActionBar().setTitle(project.getName());

        undoRedo = new UndoRedoManager(undo, redo);
        updateUndoRedoBtnState();

        binding.editorLayout.updateUndoRedoHistory();

        contentView = binding.content;
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

        binding.editorLayout.setStructureView(binding.structureView);
        if (undoRedo != null) {
            binding.editorLayout.bindUndoRedoManager(undoRedo);
        }
        binding.structureView.setOnItemClickListener(
                v -> {
                    binding.editorLayout.showDefinedAttributes(v);
                    drawerLayout.closeDrawer(GravityCompat.END);
                });

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Views"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Layouts"));
        binding.tabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        if (tab.getPosition() == 0) {
                            binding.listView.setAdapter(new ListViewAdapter(views));
                        } else {
                            binding.listView.setAdapter(new ListViewAdapter(layouts));
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {}

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {}
                });

        views =
                new Gson()
                        .fromJson(
                                FileUtil.readFromAsset(Constants.VIEWS, this),
                                new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
        layouts =
                new Gson()
                        .fromJson(
                                FileUtil.readFromAsset(Constants.LAYOUTS, this),
                                new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
        // sortListMap(views, "view", false, true);
        binding.listView.setAdapter(new ListViewAdapter(views));

        IdManager.clear();

        if (getIntent().getAction() != null && getIntent().getAction().equals(ACTION_OPEN)) {
            DrawableManager.loadFromFiles(project.getDrawables());
            binding.editorLayout.loadLayoutFromParser(project.getLayout());
        }

        setupMenuIcons();
        drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {

                    @Override
                    public void onDrawerStateChanged(int arg0) {
                        super.onDrawerStateChanged(arg0);
                        setupMenuIcons();
                    }

                    @Override
                    public void onDrawerSlide(View arg0, float arg1) {
                        super.onDrawerSlide(arg0, arg1);
                        setupMenuIcons();
                    }

                    @Override
                    public void onDrawerClosed(View arg0) {
                        super.onDrawerClosed(arg0);
                        setupMenuIcons();
                    }

                    @Override
                    public void onDrawerOpened(View arg0) {
                        super.onDrawerOpened(arg0);
                        setupMenuIcons();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)
                || drawerLayout.isDrawerVisible(GravityCompat.END)) drawerLayout.closeDrawers();
        else super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        var id = item.getItemId();
        setupMenuIcons();
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (id == R.id.undo) {
            binding.editorLayout.undo();
            return true;
        } else if (id == R.id.redo) {
            binding.editorLayout.redo();
            return true;
        } else if (id == R.id.show_structure) {
            drawerLayout.openDrawer(GravityCompat.END);
            return true;
        } else if (id == R.id.save_xml) {
            saveXml();

            return true;
        } else if (id == R.id.show_xml) {
            String result = new XmlLayoutGenerator().generate(binding.editorLayout, true);
            saveXml();
            startActivity(
                    new Intent(this, ShowXMLActivity.class)
                            .putExtra(ShowXMLActivity.EXTRA_KEY_XML, result));
            return true;
        } else return false;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        actionBarDrawerToggle.onConfigurationChanged(config);
        setupMenuIcons();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
        setupMenuIcons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // DrawableManager.loadFromFiles(project.getDrawables());
        setupMenuIcons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveXml();
    }

    private void saveXml() {

        if (project == null) return;

        if (binding.editorLayout.getChildCount() == 0) {
            project.saveLayout("");
            SBUtils.make(binding.getRoot(), R.string.project_empty)
                    .setFadeAnimation()
                    .showLongAsError();
            return;
        }

        String result = new XmlLayoutGenerator().generate(binding.editorLayout, false);
        project.saveLayout(result);
        SBUtils.make(binding.getRoot(), R.string.project_saved).setSlideAnimation().showAsSuccess();
    }

    private class ListViewAdapter extends BaseAdapter implements View.OnLongClickListener {

        private ArrayList<HashMap<String, Object>> list;

        public ListViewAdapter(ArrayList<HashMap<String, Object>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public HashMap<String, Object> getItem(int pos) {
            return list.get(pos);
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int pos, View buffer, ViewGroup parent) {
            WidgetsListBinding bind = WidgetsListBinding.inflate(getLayoutInflater());
            bind.name.setText(getItem(pos).get("name").toString());
            final float density = getResources().getDisplayMetrics().density;
            final int width = Math.round(20 * density);
            final int height = Math.round(20 * density);

            Drawable icon =
                    AppCompatResources.getDrawable(
                            EditorActivity.this,
                            InvokeUtil.getMipmapId(getItem(pos).get("iconName").toString()));
            icon.setBounds(0, 0, width, height);

            var iconColor =
                    MaterialColors.getColor(
                            bind.name, com.google.android.material.R.attr.colorOnSurface);

            icon.setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN));
            bind.name.setCompoundDrawables(icon, null, null, null);

            bind.getRoot().setOnLongClickListener(this);
            bind.getRoot().setTag(pos);
            bind.getRoot()
                    .post(
                            () -> {
                                bind.getRoot().setTranslationX(-bind.getRoot().getWidth());
                                bind.getRoot().setAlpha(0);
                                bind.getRoot()
                                        .animate()
                                        .alpha(255)
                                        // .translationY(0)
                                        .translationX(0)
                                        .setStartDelay(pos * 50)
                                        .setDuration(500)
                                        .start();
                            });

            return bind.getRoot();
        }

        @Override
        public boolean onLongClick(View v) {
            v.startDragAndDrop(null, new View.DragShadowBuilder(v), list.get((int) v.getTag()), 0);
            binding.drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        undo = menu.findItem(R.id.undo);
        redo = menu.findItem(R.id.redo);
        setupMenuIcons();
        return super.onCreateOptionsMenu(menu);
    }

    public void setupMenuIcons() {
        if (undo == null || redo == null) {
            return;
        }
        undo.getIcon().setAlpha(undoRedo.isUndoEnabled() ? 255 : 130);
        undo.setEnabled(undo.getIcon().getAlpha() == 130 ? false : true);
        redo.getIcon().setAlpha(undoRedo.isRedoEnabled() ? 255 : 130);
        redo.setEnabled(redo.getIcon().getAlpha() == 130 ? false : true);
    }

    public void updateUndoRedoBtnState() {
        new Handler(Looper.getMainLooper()).postDelayed(updateMenuIconsState, 10);
    }

    public static void sortListMap(
            final ArrayList<HashMap<String, Object>> listMap,
            final String key,
            final boolean isNumber,
            final boolean ascending) {
        Collections.sort(
                listMap,
                new Comparator<HashMap<String, Object>>() {
                    public int compare(
                            HashMap<String, Object> _compareMap1,
                            HashMap<String, Object> _compareMap2) {
                        if (isNumber) {
                            int _count1 = Integer.valueOf(_compareMap1.get(key).toString());
                            int _count2 = Integer.valueOf(_compareMap2.get(key).toString());
                            if (ascending) {
                                return _count1 < _count2 ? -1 : _count1 < _count2 ? 1 : 0;
                            } else {
                                return _count1 > _count2 ? -1 : _count1 > _count2 ? 1 : 0;
                            }
                        } else {
                            if (ascending) {
                                return (_compareMap1.get(key).toString())
                                        .compareTo(_compareMap2.get(key).toString());
                            } else {
                                return (_compareMap2.get(key).toString())
                                        .compareTo(_compareMap1.get(key).toString());
                            }
                        }
                    }
                });
    }
}
