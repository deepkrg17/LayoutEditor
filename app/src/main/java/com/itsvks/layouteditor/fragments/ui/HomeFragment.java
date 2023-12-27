package com.itsvks.layouteditor.fragments.ui;

import com.itsvks.layouteditor.LayoutFile;
import static com.itsvks.layouteditor.R.string;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.LayoutEditor;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.activities.EditorActivity;
import com.itsvks.layouteditor.adapters.ProjectListAdapter;
import com.itsvks.layouteditor.databinding.FragmentHomeBinding;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;
import com.itsvks.layouteditor.managers.ProjectManager;
import com.itsvks.layouteditor.utils.Constants;
import com.itsvks.layouteditor.utils.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

@SuppressWarnings("unused")
public class HomeFragment extends Fragment {

  private FragmentHomeBinding binding;
  private SharedPreferences projectTimes;

  private ArrayList<ProjectFile> projects = new ArrayList<>();
  private ProjectListAdapter adapter;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    binding = FragmentHomeBinding.inflate(inflater, container, false);
    projectTimes =
        PreferenceManager.getDefaultSharedPreferences(LayoutEditor.getInstance().getContext());
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    binding.fab.setOnClickListener(v -> showCreateProjectDialog());
    adapter = new ProjectListAdapter(projects);

    binding.listProjects.setAdapter(adapter);
    binding.listProjects.setLayoutManager(
        new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

    binding.listProjects.setVisibility(
        binding.noProjectsView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  @SuppressLint({"SimpleDateFormat", "RestrictedApi"})
  @SuppressWarnings("deprecation")
  private void showCreateProjectDialog() {
    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
    builder.setTitle(getString(string.create_project));

    final TextinputlayoutBinding bind = TextinputlayoutBinding.inflate(getLayoutInflater());
    final TextInputEditText editText = bind.textinputEdittext;
    final TextInputLayout inputLayout = bind.textinputLayout;

    builder.setView(bind.getRoot(), 10, 10, 10, 10);
    builder.setNegativeButton(string.cancel, (di, which) -> {});
    builder.setPositiveButton(
        string.create, (di, which) -> createProject(bind.textinputEdittext.getText().toString()));

    final AlertDialog dialog = builder.create();
    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    dialog.show();

    inputLayout.setHint(getString(string.msg_new_project_name));
    editText.setText("NewProject" + System.currentTimeMillis());
    editText.addTextChangedListener(
        new TextWatcher() {

          @Override
          public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

          @Override
          public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {}

          @Override
          public void afterTextChanged(Editable p1) {
            checkNameErrors(editText.getText().toString(), null, inputLayout, dialog);
          }
        });

    editText.requestFocus();

    InputMethodManager inputMethodManager =
        (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

    if (!editText.getText().toString().isEmpty()) {
      editText.setSelection(0, editText.getText().toString().length());
    }

    checkNameErrors(editText.getText().toString(), "", inputLayout, dialog);
  }

  @SuppressLint("NotifyDataSetChanged")
  private void loadProjects() {
    projects.clear();

    File root = new File(FileUtil.getPackageDataDir(requireContext()) + "/projects/");

    if (!root.exists()) {
      FileUtil.makeDir(FileUtil.getPackageDataDir(requireContext()) + "/projects/");
    }

    for (File file : root.listFiles()) {
      String path = file.getPath();
      projects.add(new ProjectFile(path, projectTimes.getString(path, getCurrentTime())));
    }

    adapter.notifyDataSetChanged();
    // binding.noProjectsView.setVisibility(projects.size() != 0 ? View.VISIBLE : View.GONE);
  }

  @SuppressLint("NotifyDataSetChanged")
  private void createProject(String name) {
    final String projectDir = FileUtil.getPackageDataDir(requireContext()) + "/projects/" + name;
    final String time = Calendar.getInstance().getTime().toString();
    FileUtil.makeDir(projectDir);
    FileUtil.makeDir(projectDir + "/drawable/");
    FileUtil.makeDir(projectDir + "/values/");
    FileUtil.makeDir(projectDir + "/font/");
    FileUtil.copyFileFromAsset("default_image.png", projectDir + "/drawable");
    FileUtil.copyFileFromAsset("colors.xml", projectDir + "/values");
    FileUtil.copyFileFromAsset("strings.xml", projectDir + "/values");
    FileUtil.copyFileFromAsset("default_font.ttf", projectDir + "/font");

    ProjectFile project = new ProjectFile(projectDir, time);
    //((LayoutFile) getActivity().getIntent().getExtras().getParcelable(Constants.EXTRA_KEY_LAYOUT)).saveLayout("");
    project.createDefaultLayout();
    projects.add(project);
    adapter.notifyDataSetChanged();

    projectTimes.edit().putString(projectDir, time).apply();

    final Intent intent = new Intent(requireContext(), EditorActivity.class);
    ProjectManager.getInstance().openProject(project);
    startActivity(intent);
  }

  private void checkNameErrors(
      String name, String currentName, TextInputLayout inputLayout, AlertDialog dialog) {
    if (name.equals("")) {
      inputLayout.setErrorEnabled(true);
      inputLayout.setError(getString(string.msg_cannnot_empty));
      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
      return;
    }

    for (ProjectFile file : projects) {
      if (name.equals(currentName)) break;

      if (file.getName().equals(name)) {
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(getString(string.msg_current_name_unavailable));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        return;
      }
    }

    inputLayout.setErrorEnabled(false);
    inputLayout.setError("");
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
  }

  @Override
  public void onResume() {
    super.onResume();
    loadProjects();
  }

  private String getCurrentTime() {
    return Calendar.getInstance().getTime().toString();
  }
}
