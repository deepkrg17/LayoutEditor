package com.itsvks.layouteditor.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

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
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.activities.EditorActivity;
import com.itsvks.layouteditor.databinding.ListProjectFileBinding;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;
import com.itsvks.layouteditor.utils.FileUtil;

import java.util.ArrayList;
import java.util.Locale;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {

    ArrayList<ProjectFile> projects = new ArrayList<>();

    public ProjectListAdapter(ArrayList<ProjectFile> projects) {
        this.projects = projects;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                ListProjectFileBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.projectName.setText(projects.get(position).name.toString());
        holder.projectDate.setText(projects.get(position).date.toString());
        TooltipCompat.setTooltipText(holder.menu, "Options");
        holder.binding
                .getRoot()
                .setOnClickListener(
                        v -> {
                            Intent intent = new Intent(v.getContext(), EditorActivity.class);
                            intent.putExtra(
                                    EditorActivity.EXTRA_KEY_PROJECT, projects.get(position));
                            intent.setAction(EditorActivity.ACTION_OPEN);
                            v.getContext().startActivity(intent);
                        });
        holder.projectIcon.setText(
                projects.get(position).getName().substring(0, 1).toUpperCase(Locale.US));
        holder.menu.setOnClickListener(
                v -> {
                    final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.inflate(R.menu.menu_project_file_options);
                    popupMenu.setOnMenuItemClickListener(
                            new PopupMenu.OnMenuItemClickListener() {

                                @Override
                                public boolean onMenuItemClick(MenuItem item) {

                                    var id = item.getItemId();
                                    if (id == R.id.menu_delete) {
                                        final MaterialAlertDialogBuilder builder =
                                                new MaterialAlertDialogBuilder(v.getContext());
                                        builder.setTitle("Delete project");
                                        builder.setMessage(
                                                "Are you sure you want to remove the project?");
                                        builder.setNegativeButton("No", (d, w) -> {});
                                        builder.setPositiveButton(
                                                "Yes",
                                                (d, w) -> {
                                                    FileUtil.deleteFile(
                                                            projects.get(position).getPath());
                                                    projects.remove(projects.get(position));
                                                    notifyDataSetChanged();
                                                });

                                        builder.create().show();
                                        return true;
                                    } else if (id == R.id.menu_rename) {
                                        final MaterialAlertDialogBuilder builder =
                                                new MaterialAlertDialogBuilder(v.getContext());
                                        builder.setTitle("Rename project");
                                        final TextinputlayoutBinding bind =
                                                TextinputlayoutBinding.inflate(
                                                        builder.create().getLayoutInflater());
                                        final TextInputEditText editText = bind.textinputEdittext;
                                        final TextInputLayout inputLayout = bind.textinputLayout;

                                        editText.setText(projects.get(position).getName());
                                        inputLayout.setHint("Enter new project name");

                                        final int padding =
                                                (int)
                                                        TypedValue.applyDimension(
                                                                TypedValue.COMPLEX_UNIT_DIP,
                                                                10,
                                                                v.getContext()
                                                                        .getResources()
                                                                        .getDisplayMetrics());
                                        builder.setView(bind.getRoot());
                                        builder.setNegativeButton("Cancel", (di, which) -> {});
                                        builder.setPositiveButton(
                                                "Rename",
                                                (di, which) -> {
                                                    String path = projects.get(position).getPath();
                                                    String newPath =
                                                            path.substring(0, path.lastIndexOf("/"))
                                                                    + "/"
                                                                    + editText.getText().toString();
                                                    projects.get(position).rename(newPath);
                                                    notifyDataSetChanged();
                                                });

                                        final AlertDialog dialog = builder.create();
                                        dialog.getWindow()
                                                .setSoftInputMode(
                                                        WindowManager.LayoutParams
                                                                .SOFT_INPUT_STATE_VISIBLE);
                                        dialog.show();

                                        editText.addTextChangedListener(
                                                new TextWatcher() {

                                                    @Override
                                                    public void beforeTextChanged(
                                                            CharSequence p1,
                                                            int p2,
                                                            int p3,
                                                            int p4) {}

                                                    @Override
                                                    public void onTextChanged(
                                                            CharSequence p1,
                                                            int p2,
                                                            int p3,
                                                            int p4) {}

                                                    @Override
                                                    public void afterTextChanged(Editable p1) {
                                                        checkNameErrors(
                                                                projects,
                                                                editText.getText().toString(),
                                                                projects.get(position).getName(),
                                                                inputLayout,
                                                                dialog);
                                                    }
                                                });

                                        checkNameErrors(
                                                projects,
                                                editText.getText().toString(),
                                                projects.get(position).getName(),
                                                inputLayout,
                                                dialog);

                                        editText.requestFocus();
                                        InputMethodManager inputMethodManager =
                                                (InputMethodManager)
                                                        v.getContext()
                                                                .getSystemService(
                                                                        Context
                                                                                .INPUT_METHOD_SERVICE);
                                        inputMethodManager.showSoftInput(
                                                editText, InputMethodManager.SHOW_IMPLICIT);

                                        if (!editText.getText().toString().equals("")) {
                                            editText.setSelection(
                                                    0, editText.getText().toString().length());
                                        }

                                        return true;
                                    }
                                    return false;
                                }
                            });

                    popupMenu.show();
                });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    private void checkNameErrors(
            ArrayList<ProjectFile> projects,
            String name,
            String currentName,
            TextInputLayout inputLayout,
            AlertDialog dialog) {
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
}
