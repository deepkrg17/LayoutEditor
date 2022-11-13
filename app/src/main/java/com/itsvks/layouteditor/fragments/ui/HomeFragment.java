package com.itsvks.layouteditor.fragments.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.activities.EditorActivity;
import com.itsvks.layouteditor.databinding.FragmentHomeBinding;
import com.itsvks.layouteditor.databinding.ListProjectFileBinding;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;
import com.itsvks.layouteditor.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings("unused")
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ArrayList<ProjectFile> projects = new ArrayList<>();
    private ProjectListAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        binding.fab.setOnClickListener(v -> showCreateProjectDialog());

        adapter = new ProjectListAdapter();

        binding.listProjects.setAdapter(adapter);
        // binding.tv1.setText(String.valueOf(adapter.getCount()));

        binding.noProjectsView.setVisibility(adapter.getCount() != 0 ? View.VISIBLE : View.GONE);
        binding.listProjects.setVisibility(
                binding.noProjectsView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class ProjectListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return projects.size();
        }

        @Override
        public ProjectFile getItem(int pos) {
            return projects.get(pos);
        }

        @Override
        public long getItemId(int p1) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int pos, View buffer, ViewGroup p3) {
            ProjectFile project = getItem(pos);

            ListProjectFileBinding bind = ListProjectFileBinding.inflate(getLayoutInflater());
            bind.icon.setText(project.getName().substring(0, 1).toUpperCase(Locale.US));
            bind.name.setText(project.getName());
            bind.getRoot().setOnClickListener(v -> openProject(project));

            bind.btnOptions.setOnClickListener(
                    v -> {
                        final PopupMenu popupMenu = new PopupMenu(requireContext(), v);
                        popupMenu.inflate(R.menu.menu_project_file_options);
                        popupMenu.setOnMenuItemClickListener(
                                new PopupMenu.OnMenuItemClickListener() {

                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        if (item.getItemId() == R.id.menu_rename) {
                                            renameProject(project);
                                            return true;
                                        }

                                        if (item.getItemId() == R.id.menu_delete) {
                                            deleteProject(project);
                                            return true;
                                        }

                                        return false;
                                    }
                                });

                        popupMenu.show();
                    });

            return bind.getRoot();
        }
    }

    @SuppressLint({"SimpleDateFormat", "RestrictedApi"})
    @SuppressWarnings("deprecation")
    private void showCreateProjectDialog() {
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Create project");

        final TextinputlayoutBinding bind = TextinputlayoutBinding.inflate(getLayoutInflater());
        final TextInputEditText editText = bind.textinputEdittext;
        final TextInputLayout inputLayout = bind.textinputLayout;

        builder.setView(bind.getRoot(), 10, 10, 10, 10);
        builder.setNegativeButton("Cancel", (di, which) -> {});
        builder.setPositiveButton(
                "Create",
                (di, which) -> createProject(bind.textinputEdittext.getText().toString()));

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        inputLayout.setHint("Enter new project name");
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

    private void loadProjects() {
        projects.clear();

        File root = new File(FileUtil.getPackageDataDir(requireContext()) + "/projects/");

        if (!root.exists()) {
            FileUtil.makeDir(FileUtil.getPackageDataDir(requireContext()) + "/projects/");
        }

        for (File file : root.listFiles()) {
            String path = file.getPath();

            ProjectFile project = new ProjectFile(path);
            projects.add(project);
        }

        adapter.notifyDataSetChanged();
    }

    private void createProject(String name) {

        final String projectDir = FileUtil.getPackageDataDir(requireContext()) + "/projects/" + name;
        FileUtil.makeDir(projectDir);
        FileUtil.makeDir(projectDir + "/drawable/");
        FileUtil.copyFileFromAsset("default_image.png", projectDir + "/drawable");

        ProjectFile project = new ProjectFile(projectDir);
        project.saveLayout("");
        projects.add(project);
        adapter.notifyDataSetChanged();

        final Intent intent = new Intent(requireContext(), EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_KEY_PROJECT, project);
        startActivity(intent);
    }

    private void renameProject(final ProjectFile project) {
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Rename project");

        final TextinputlayoutBinding bind = TextinputlayoutBinding.inflate(getLayoutInflater());
        final TextInputEditText editText = bind.textinputEdittext;
        final TextInputLayout inputLayout = bind.textinputLayout;

        editText.setText(project.getName());
        inputLayout.setHint("Enter new project name");

        final int padding =
                (int)
                        TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                10,
                                getResources().getDisplayMetrics());
        builder.setView(bind.getRoot());
        builder.setNegativeButton("Cancel", (di, which) -> {});
        builder.setPositiveButton(
                "Rename",
                (di, which) -> {
                    String path = project.getPath();
                    String newPath =
                            path.substring(0, path.lastIndexOf("/"))
                                    + "/"
                                    + editText.getText().toString();
                    project.rename(newPath);

                    adapter.notifyDataSetChanged();
                });

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        editText.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

                    @Override
                    public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {}

                    @Override
                    public void afterTextChanged(Editable p1) {
                        checkNameErrors(
                                editText.getText().toString(),
                                project.getName(),
                                inputLayout,
                                dialog);
                    }
                });

        checkNameErrors(editText.getText().toString(), project.getName(), inputLayout, dialog);

        editText.requestFocus();
        InputMethodManager inputMethodManager =
                (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        if (!editText.getText().toString().equals("")) {
            editText.setSelection(0, editText.getText().toString().length());
        }
    }

    private void deleteProject(final ProjectFile file) {
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Delete project");
        builder.setMessage("Are you sure you want to remove the project?");
        builder.setNegativeButton("No", (di, which) -> {});
        builder.setPositiveButton(
                "Yes",
                (di, which) -> {
                    projects.remove(file);
                    FileUtil.deleteFile(file.getPath());
                    ((BaseAdapter) binding.listProjects.getAdapter()).notifyDataSetChanged();
                });

        builder.create().show();
    }

    private void openProject(final ProjectFile project) {
        Intent intent = new Intent(requireContext(), EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_KEY_PROJECT, project);
        intent.setAction(EditorActivity.ACTION_OPEN);
        startActivity(intent);
    }

    private void checkNameErrors(
            String name, String currentName, TextInputLayout inputLayout, AlertDialog dialog) {
        if (name.equals("")) {
            inputLayout.setErrorEnabled(true);
            inputLayout.setError("Field cannot be empty!");
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            return;
        }

        for (ProjectFile file : projects) {
            if (name.equals(currentName)) break;

            if (file.getName().equals(name)) {
                inputLayout.setErrorEnabled(true);
                inputLayout.setError("Current name is unavailable!");
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                return;
            }
        }

        inputLayout.setErrorEnabled(false);
        inputLayout.setError("");
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
    }

    private void openUrl(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProjects();
    }
}
