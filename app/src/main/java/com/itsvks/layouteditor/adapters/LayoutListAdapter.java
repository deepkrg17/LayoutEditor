package com.itsvks.layouteditor.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.LayoutEditor;
import com.itsvks.layouteditor.LayoutFile;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.R.string;
import com.itsvks.layouteditor.activities.EditorActivity;
import com.itsvks.layouteditor.databinding.ActivityEditorBinding;
import com.itsvks.layouteditor.databinding.LayoutsListItemBinding;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;
import com.itsvks.layouteditor.editor.DesignEditor;
import com.itsvks.layouteditor.editor.convert.ConvertImportedXml;
import com.itsvks.layouteditor.managers.SharedPreferenceManager;
import com.itsvks.layouteditor.tools.XmlLayoutGenerator;
import com.itsvks.layouteditor.utils.Constants;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.SBUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class LayoutListAdapter extends RecyclerView.Adapter<LayoutListAdapter.ViewHolder> {
  private ActivityEditorBinding editorBinding;
  private ActionBar toolbar;

  private DrawerLayout drawerLayout;
  private DesignEditor editorLayout;
  
  private List<LayoutFile> layouts;
  private LayoutFile currentLayout;
  private String defaultLayoutPath;
  private String lastLayoutPath;
  
  private final String projectPath;
  private final String projectName;

  public LayoutListAdapter(
    ActivityEditorBinding editorBinding,
    ActionBar toolbar,
    String projectPath) {
    this.editorBinding = editorBinding;
    this.toolbar = toolbar;
    this.projectPath = projectPath;
    drawerLayout = editorBinding.drawer;
    editorLayout = editorBinding.editorLayout;
    layouts = new ArrayList<>();
    defaultLayoutPath = projectPath + "/layout/layout_main.xml";
    projectName = FileUtil.getLastSegmentFromPath(projectPath);
    lastLayoutPath = SharedPreferenceManager.loadPrefString(projectName, defaultLayoutPath);
    //currentLayout = currentLayout == null ? getLastLayout() : currentLayout;
    
    init();
  }
  
  @SuppressWarnings("deprecation")
  public void init() {
    loadLayouts();
    
    toolbar.setSubtitle(getCurrentLayout().getName());
    if (!new File(defaultLayoutPath).exists()) createNewLayout(defaultLayoutPath, "");
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    LayoutsListItemBinding binding;
    RelativeLayout container;
    TextView layoutName;
    TextView layoutIcon;

    public ViewHolder(@NonNull LayoutsListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;

      container = binding.container;
      layoutName = binding.layoutName;
      layoutIcon = binding.icon;
    }
  }

  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(
        LayoutsListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @SuppressLint("RecyclerView")
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {

    var context = holder.binding.getRoot().getContext();
    holder
        .binding
        .getRoot()
        .setAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.getContext(), R.anim.project_list_animation));
    holder.layoutName.setText(checkLayoutName(holder.layoutName, pos));
    holder.layoutName.setOnClickListener(v -> openLayout(v, pos, true, true));
    holder.layoutIcon.setText(layouts.get(pos).getName().substring(0, 1).toUpperCase(Locale.US));
        
    holder.container.setOnLongClickListener(
      v -> {
        if (layouts.get(pos).getPath().equals(defaultLayoutPath)) SBUtils.make(drawerLayout, "You neither can delete nor can rename the default layout!...").setFadeAnimation().showAsError();
        else showOptions(v, pos);
        return true;
      }
    );
  }

  @Override
  public int getItemCount() {
    return layouts.size();
  }
  
  @SuppressLint("NotifyDataSetChanged")
  public void loadLayouts() {
    layouts.clear();

    File root = new File(projectPath + "/layout/");

    for (File file : root.listFiles()) {
      String path = file.getPath();
      createLayout(path);
    }

    notifyDataSetChanged();
  }

  public void checkNameErrors(
      String name,
      String currentName,
      TextInputLayout inputLayout,
      AlertDialog dialog) {
    if (name.isEmpty()) {
      inputLayout.setErrorEnabled(true);
      inputLayout.setError(dialog.getContext().getString(string.msg_cannnot_empty));
      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
      return;
    }
    if (!Pattern.matches("^[a-z]+(_[a-z]+)*.xml$", name)) {
      inputLayout.setErrorEnabled(true);
      inputLayout.setError(dialog.getContext().getString(string.invalid_name));
      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
      return;
    }

    for (LayoutFile file : layouts) {
      if (name.equals(currentName)) break;

      if (file.getName().equals(name)) {
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(
            LayoutEditor.getInstance().getContext().getString(string.msg_current_name_unavailable));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        return;
      }
    }

    inputLayout.setErrorEnabled(false);
    inputLayout.setError("");
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
  }
  
  @SuppressLint({"SimpleDateFormat", "RestrictedApi"})
  @SuppressWarnings("deprecation")
  public void createLayout(View v) {
    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
    builder.setTitle(string.create_layout);

    final TextinputlayoutBinding bind = TextinputlayoutBinding.inflate(builder.create().getLayoutInflater());
    final TextInputEditText editText = bind.textinputEdittext;
    final TextInputLayout inputLayout = bind.textinputLayout;

    builder.setView(bind.getRoot(), 10, 10, 10, 10);
    builder.setNegativeButton(string.cancel, (di, which) -> {});
    builder.setPositiveButton(string.create, (di, which) -> createNewLayout(projectPath + "/layout/" + bind.textinputEdittext.getText().toString(), ""));

    final AlertDialog dialog = builder.create();
    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    dialog.show();

    inputLayout.setHint(string.msg_new_layout_name);
    editText.setText("layout_new.xml");
    editText.addTextChangedListener(
        new TextWatcher() {

          @Override
          public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

          @Override
          public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
            checkNameErrors(p1.toString(), null, inputLayout, dialog);
          }

          @Override
          public void afterTextChanged(Editable p1) {}
        });

    editText.requestFocus();

    InputMethodManager inputMethodManager =
        (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

    if (!editText.getText().toString().isEmpty()) {
      editText.setSelection(0, editText.getText().toString().length());
    }

    checkNameErrors(editText.getText().toString(), "", inputLayout, dialog);
  }

  @SuppressWarnings("deprecation")
  @SuppressLint("RestrictedApi")
  public void renameLayout(View v, int pos) {
    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
    builder.setTitle(string.rename_layout);
    final TextinputlayoutBinding bind = TextinputlayoutBinding.inflate(builder.create().getLayoutInflater());
    final TextInputEditText editText = bind.textinputEdittext;
    final TextInputLayout inputLayout = bind.textinputLayout;

    editText.setText(layouts.get(pos).getName());
    inputLayout.setHint(string.msg_new_layout_name);

    final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, v.getContext().getResources().getDisplayMetrics());
    builder.setView(bind.getRoot(), padding, padding, padding, padding);
    builder.setNegativeButton(string.cancel, (di, which) -> {});
    builder.setPositiveButton(
        string.rename,
        (di, which) -> {
          String path = layouts.get(pos).getPath();
          String newPath = path.substring(0, path.lastIndexOf("/")) + "/" + editText.getText().toString();
          layouts.get(pos).rename(newPath);
          if (layouts.get(pos) == getCurrentLayout()) openLayout(v, pos, false, true);
          notifyItemChanged(pos);
        });

    final AlertDialog dialog = builder.create();
    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    dialog.show();

    editText.addTextChangedListener(
        new TextWatcher() {

          @Override
          public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

          @Override
          public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
            checkNameErrors(p1.toString(), layouts.get(pos).getName(), inputLayout, dialog);
          }

          @Override
          public void afterTextChanged(Editable p1) {}
        });

    checkNameErrors(
        editText.getText().toString(),
        layouts.get(pos).getName(),
        inputLayout,
        dialog);

    editText.requestFocus();
    InputMethodManager inputMethodManager =
        (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

    if (!editText.getText().toString().isEmpty()) {
      editText.setSelection(0, editText.getText().toString().length());
    }
  }

  public void deleteLayout(View v, int pos) {
    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
    builder.setTitle(string.delete_layout);
    builder.setMessage(string.msg_delete_layout);
    builder.setNegativeButton(string.no, (d, w) -> d.dismiss());
    builder.setPositiveButton(
        string.yes,
        (d, w) -> {
          if (layouts.get(pos) == getCurrentLayout()) openLayout(v, getLayoutIndex("layout_main.xml"), false, false);
          FileUtil.deleteFile(layouts.get(pos).getPath());
          layouts.remove(layouts.get(pos));
          notifyDataSetChanged();
        });

    builder.create().show();
  }

  public void showOptions(View v, int pos) {
    final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
    popupMenu.inflate(R.menu.menu_layout_file_options);
    popupMenu.setOnMenuItemClickListener(
        new PopupMenu.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem item) {
            var id = item.getItemId();
            switch (id) {
              case R.id.menu_delete_layout:
                deleteLayout(v, pos);
                return true;
              case R.id.menu_rename_layout:
                renameLayout(v, pos);
                return true;
            }
            return false;
          }
        });

    popupMenu.show();
  }

  public void openLayout(View v, int pos, boolean rtrn, boolean sve) {
    drawerLayout.closeDrawers();
    if (rtrn && layouts.get(pos) == getCurrentLayout()) return;
    else {
      if (sve) saveXml(v);

      setCurrentLayout(layouts.get(pos));
      setLastLayoutPath(getCurrentLayout().getPath());

      new Intent().putExtra(Constants.EXTRA_KEY_LAYOUT, getCurrentLayout());

      editorLayout.loadLayoutFromParser(getCurrentLayout().getLayout());
      toolbar.setSubtitle(getCurrentLayout().getName());
      SBUtils.make(drawerLayout, "Loaded").setFadeAnimation().showAsSuccess();
    }
  }
  
  public void openLastLayout() {
    setCurrentLayout(getLastLayout());
    new Intent().putExtra(Constants.EXTRA_KEY_LAYOUT, getLastLayout());
    editorLayout.loadLayoutFromParser(getLastLayout().getLayout());
    toolbar.setSubtitle(getLastLayout().getName());
    SBUtils.make(drawerLayout, "Loaded").setFadeAnimation().showAsSuccess();
  }

  public String checkLayoutName(View v, int pos) {
    final String name = layouts.get(pos).getName();

    if (Pattern.matches("^[a-z]+(_[a-z]+)*.xml$", name)) return name;
    else {
      final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
      builder.setTitle(string.note);
      builder.setMessage(
          "Layout: "
              + name
              + "\n\n*Has problem in his name!. Please check it and write a perfect name that only contains lowercase alphabets, no special characters (like :- numbers 0-9 , !₹^+>&÷>#*, etc.) and underscore (single between two words) or it may be a problem with the extension, make sure that the file name ends with .xml. Okay rename the file.");
      builder.setCancelable(false);
      builder.setPositiveButton(string.rename, (d, w) -> renameLayout(v, pos));
      builder.create().show();
    }

    return name;
  }

  public void saveXml(View v) {
    if (getCurrentLayout() == null) return;

    if (editorLayout.getChildCount() == 0) {
      getCurrentLayout().saveLayout("");
      ToastUtils.showShort(v.getContext().getString(string.layout_saved));
      return;
    }

    String result = new XmlLayoutGenerator().generate(editorLayout, false);
    getCurrentLayout().saveLayout(result);
    ToastUtils.showShort(v.getContext().getString(string.layout_saved));
  }
  
  public void createLayout(String path) {
    LayoutFile layout = new LayoutFile(path);
    layouts.add(layout);
    notifyDataSetChanged();
  }
  
  public void createNewLayout(String path, String text) {
    LayoutFile layout = new LayoutFile(path);
    layout.saveLayout(text);
    layouts.add(layout);
    notifyDataSetChanged();
  }
  
  public int getLayoutIndex(String name) {
    loadLayouts();

    for (int i = 0; i < layouts.size(); i++) {
      if (name == layouts.get(i).getName()) {
        return i;
      }
    }

    return 0;
  }
  
  public String getLastLayoutPath() {
    return lastLayoutPath == null ? SharedPreferenceManager.loadPrefString(projectName, defaultLayoutPath) : lastLayoutPath;
  }
  
  public void setLastLayoutPath(String path) {
    SharedPreferenceManager.changePrefString(projectName, path);
    lastLayoutPath = path;
  }
  
  public LayoutFile getDefaultLayout() {
    return new LayoutFile(defaultLayoutPath);
  }
  
  public LayoutFile getLastLayout() {
    return new LayoutFile(getLastLayoutPath());
  }
  
  public LayoutFile getCurrentLayout() {
    return currentLayout == null ? getLastLayout() : currentLayout;
  }
  
  public void setCurrentLayout(LayoutFile layout) {
    currentLayout = layout;
  }
}