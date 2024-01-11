package com.itsvks.layouteditor.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.LayoutEditor;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.R.string;
import com.itsvks.layouteditor.activities.EditorActivity;
import com.itsvks.layouteditor.activities.PreviewLayoutActivity;
import com.itsvks.layouteditor.databinding.ListProjectFileBinding;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;
import com.itsvks.layouteditor.managers.PreferencesManager;
import com.itsvks.layouteditor.utils.Constants;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.SBUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {

  private final List<ProjectFile> projects;
  private final SharedPreferences prefs;

  public ProjectListAdapter(List<ProjectFile> projects) {
    this.projects = projects;
    prefs = PreferencesManager.getPrefs();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    ListProjectFileBinding binding;
    AppCompatTextView projectName;
    AppCompatTextView projectDate;
    AppCompatTextView projectIcon;
    AppCompatImageButton menu;

    public ViewHolder(@NonNull ListProjectFileBinding binding) {
      super(binding.getRoot());
      this.binding = binding;

      projectName = binding.projectName;
      projectDate = binding.projectDate;
      projectIcon = binding.icon;
      menu = binding.menu;
    }
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(
      ListProjectFileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @SuppressLint("RecyclerView")
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    var context = holder.binding.getRoot().getContext();
    holder
      .binding
      .getRoot()
      .setAnimation(
        AnimationUtils.loadAnimation(
          holder.itemView.getContext(), R.anim.project_list_animation));
    holder.projectName.setText(projects.get(position).name);
    holder.projectDate.setText(projects.get(position).date);
    TooltipCompat.setTooltipText(holder.menu, context.getString(string.options));
    holder.binding.getRoot().setOnClickListener(v -> openProject(v, position));
    holder.projectIcon.setText(
      projects.get(position).name.substring(0, 1).toUpperCase(Locale.US));
    holder.menu.setOnClickListener(v -> showOptions(v, position));
  }

  @Override
  public int getItemCount() {
    return projects.size();
  }

  private void checkNameErrors(
    List<ProjectFile> projects,
    @NonNull String name,
    String currentName,
    TextInputLayout inputLayout,
    AlertDialog dialog) {
    if (name.isEmpty()) {
      inputLayout.setErrorEnabled(true);
      inputLayout.setError(dialog.getContext().getString(string.msg_cannnot_empty));
      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
      return;
    }

    for (ProjectFile file : projects) {
      if (name.equals(currentName)) break;

      if (file.name.equals(name)) {
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(
          LayoutEditor.Companion.getInstance().getContext().getString(string.msg_current_name_unavailable));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        return;
      }
    }

    inputLayout.setErrorEnabled(false);
    inputLayout.setError("");
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
  }

  @SuppressWarnings("deprecation")
  @SuppressLint("RestrictedApi")
  private void renameProject(@NonNull View v, int position) {
    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
    builder.setTitle(string.rename_project);
    final TextinputlayoutBinding bind =
      TextinputlayoutBinding.inflate(builder.create().getLayoutInflater());
    final TextInputEditText editText = bind.textinputEdittext;
    final TextInputLayout inputLayout = bind.textinputLayout;

    editText.setText(projects.get(position).name);
    inputLayout.setHint(string.msg_new_project_name);

    final int padding =
      (int)
        TypedValue.applyDimension(
          TypedValue.COMPLEX_UNIT_DIP, 10, v.getContext().getResources().getDisplayMetrics());
    builder.setView(bind.getRoot(), padding, padding, padding, padding);
    builder.setNegativeButton(string.cancel, (di, which) -> {
    });
    builder.setPositiveButton(
      string.rename,
      (di, which) -> {
        String path = projects.get(position).getPath();
        String newPath =
          path.substring(0, path.lastIndexOf("/")) + "/" + editText.getText().toString();
        projects.get(position).rename(newPath);
        notifyItemChanged(position);
      });

    final AlertDialog dialog = builder.create();
    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    dialog.show();

    editText.addTextChangedListener(
      new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
        }

        @Override
        public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
        }

        @Override
        public void afterTextChanged(Editable p1) {
          checkNameErrors(
            projects,
            editText.getText().toString(),
            projects.get(position).name,
            inputLayout,
            dialog);
        }
      });

    checkNameErrors(
      projects,
      editText.getText().toString(),
      projects.get(position).name,
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

  private void deleteProject(@NonNull View v, int position) {
    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
    builder.setTitle(string.delete_project);
    builder.setMessage(string.msg_delete_project);
    builder.setNegativeButton(string.no, (d, w) -> d.dismiss());
    builder.setPositiveButton(
      string.yes,
      (d, w) -> {
        FileUtil.deleteFile(projects.get(position).getPath());
        projects.remove(projects.get(position));
        notifyItemRemoved(position);
      });

    builder.create().show();
  }

  private void showOptions(View v, int position) {
    final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
    popupMenu.inflate(R.menu.menu_project_file_options);
    popupMenu.setOnMenuItemClickListener(
      item -> {
        var id = item.getItemId();

        if (id == R.id.menu_delete) {
          deleteProject(v, position);
          return true;
        } else if (id == R.id.menu_preview) {
          previewLayout(v, position);
          return true;
        } else if (id == R.id.menu_rename) {
          renameProject(v, position);
          return true;
        }
        return false;
      });

    popupMenu.show();
  }

  private void openProject(@NonNull View v, int position) {
    Intent intent = new Intent(v.getContext(), EditorActivity.class);

    intent.putExtra(Constants.EXTRA_KEY_PROJECT, projects.get(position));
    intent.setAction(EditorActivity.ACTION_OPEN);

    final String projectDir =
      FileUtil.getPackageDataDir(LayoutEditor.Companion.getInstance().getContext())
        + "/projects/"
        + projects.get(position).name;
    if (!prefs.getBoolean("copyAssets", false)
      && !(new File(projectDir + "/values/colors.xml").exists())) {
      FileUtil.makeDir(projectDir + "/values/");
      // FileUtil.makeDir(projectDir + "/drawable/");
      // FileUtil.copyFileFromAsset("default_image.png", projectDir + "/drawable");
      FileUtil.copyFileFromAsset("colors.xml", projectDir + "/values");
      prefs.edit().putBoolean("copyAssets", true).apply();
    }
    v.getContext().startActivity(intent);
  }

  private void previewLayout(@NonNull View v, int position) {
    ArrayList<String> layouts = new ArrayList<>();
    var allLayouts = projects.get(position).getAllLayouts();
    allLayouts.forEach(layoutFile -> layouts.add(layoutFile.name));

    new MaterialAlertDialogBuilder(v.getContext())
      .setTitle("Choose layout")
      .setAdapter(new ArrayAdapter<>(v.getContext(), android.R.layout.simple_list_item_1, layouts), (d, w) -> {
        Intent intent = new Intent(v.getContext(), PreviewLayoutActivity.class);
        intent.putExtra(Constants.EXTRA_KEY_LAYOUT, allLayouts.get(w));
        if (allLayouts.get(w).read().isEmpty()) {
          SBUtils.make(v, layouts.get(w) + " is empty...").setFadeAnimation().showAsError();
        } else v.getContext().startActivity(intent);
        d.dismiss();
      })
      .setPositiveButton(string.cancel, null)
      .show();
  }
}
