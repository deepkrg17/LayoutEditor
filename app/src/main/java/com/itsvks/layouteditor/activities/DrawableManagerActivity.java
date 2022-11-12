package com.itsvks.layouteditor.activities;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.databinding.ActivityDrawableManagerBinding;
import com.itsvks.layouteditor.databinding.LayoutDrawableGridItemBinding;
import com.itsvks.layouteditor.utils.FilePicker;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.SBUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class DrawableManagerActivity extends BaseActivity {

    public static final String EXTRA_KEY_PROJECT = "project";

    private ActivityDrawableManagerBinding binding;

    private ProjectFile project;
    private FilePicker filepicker;

    private ArrayList<DrawableItem> drawables = new ArrayList<>();
    private GridAdapter gridAdapter;

    private boolean isSelectedMode;

    Drawable ic_check;
    Drawable ic_delete;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDrawableManagerBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setTitle(getString(R.string.drawable_manager));

        project = getIntent().getParcelableExtra(EXTRA_KEY_PROJECT);

        ic_check =
                AppCompatResources.getDrawable(
                        DrawableManagerActivity.this, R.drawable.ic_progress_check);
        ic_check.setColorFilter(new PorterDuffColorFilter(0xff4caf50, PorterDuff.Mode.SRC_IN));

        ic_delete = AppCompatResources.getDrawable(this, R.drawable.delete);
        var errorColor =
                MaterialColors.getColor(
                        binding.getRoot(), com.google.android.material.R.attr.colorErrorContainer);
        ic_delete.setColorFilter(new PorterDuffColorFilter(errorColor, PorterDuff.Mode.SRC_IN));

        gridAdapter = new GridAdapter();
        binding.gridView.setAdapter(gridAdapter);

        loadDrawables();
        filepicker =
                new FilePicker(this, binding.fab) {
                    @Override
                    public void onResult(String path) {
                        addDrawable(path);
                    }
                };

        binding.topAppBar.setNavigationOnClickListener(
                v -> {
                    if (isSelectedMode) {
                        stopSelection();
                        return;
                    }
                    super.onBackPressed();
                });
        binding.fab.setOnClickListener(
                v -> {
                    if (isSelectedMode) {

                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Remove drawable")
                                .setMessage("Do you want to remove the drawables?")
                                .setNegativeButton(
                                        "No",
                                        (d, w) -> {
                                            d.dismiss();
                                            stopSelection();
                                        })
                                .setPositiveButton(
                                        "Yes",
                                        (d, w) -> {
                                            for (int i = drawables.size() - 1; i >= 0; i--) {
                                                if (drawables.get(i).selected) {
                                                    FileUtil.deleteFile(drawables.get(i).path);
                                                    drawables.remove(i);
                                                }
                                            }
                                            stopSelection();
                                        })
                                .show();

                        return;
                    }

                    filepicker.launch("image/*");
                });
    }

    private void addDrawable(final String path) {
        // File name with extension
        final String lastSegment = FileUtil.getLastSegmentFromPath(path);

        // File name without extension
        final String fileName = lastSegment.substring(0, lastSegment.lastIndexOf("."));

        // Extension
        final String extension =
                lastSegment.substring(lastSegment.lastIndexOf("."), lastSegment.length());

        final TextInputLayout inputLayout =
                (TextInputLayout)
                        getLayoutInflater().inflate(R.layout.textinputlayout, null, false);
        inputLayout.setHint("Enter new name");

        final TextInputEditText editText = inputLayout.findViewById(R.id.textinput_edittext);
        editText.setText(fileName);

        final int padding =
                (int)
                        TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                10,
                                getResources().getDisplayMetrics());

        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(inputLayout);
        builder.setTitle("Add drawable");
        builder.setNegativeButton("Cancel", (di, which) -> {});
        builder.setPositiveButton(
                "Add",
                (di, which) -> {
                    String drawablePath = project.getDrawablePath();

                    String toPath = drawablePath + editText.getText().toString() + extension;
                    FileUtil.copyFile(path, toPath);

                    Drawable drawable = Drawable.createFromPath(toPath);
                    String name = editText.getText().toString();
                    drawables.add(new DrawableItem(drawable, name, toPath));
                    gridAdapter.notifyDataSetChanged();
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
                        checkNameErrors(editText.getText().toString(), inputLayout, dialog);
                    }
                });

        checkNameErrors(fileName, inputLayout, dialog);

        editText.requestFocus();
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        if (!editText.getText().toString().equals("")) {
            editText.setSelection(0, editText.getText().toString().length());
        }
    }

    private void checkNameErrors(String name, TextInputLayout inputLayout, AlertDialog dialog) {
        if (!Pattern.matches("[a-z][a-z0-9_]*", name)) {
            inputLayout.setErrorEnabled(true);
            inputLayout.setError("Only small letters(a-z) and numbers!");
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            return;
        }

        for (DrawableItem item : drawables) {
            if (item.name.equals(name)) {
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

    private void loadDrawables() {
        File[] files = project.getDrawables();

        for (File file : files) {
            Drawable drawable = Drawable.createFromPath(file.getPath());
            String name = file.getName();
            name = name.substring(0, name.lastIndexOf("."));
            drawables.add(new DrawableItem(drawable, name, file.getPath()));
        }
    }

    private void startSelection() {
        isSelectedMode = true;
        gridAdapter.notifyDataSetChanged();
        binding.fab.setIcon(AppCompatResources.getDrawable(this, R.drawable.delete));
        binding.fab.setText(getString(R.string.delete));
    }

    private void stopSelection() {
        isSelectedMode = false;
        binding.fab.setIcon(AppCompatResources.getDrawable(this, R.drawable.plus));
        binding.fab.setText(getString(R.string.add_new));

        for (DrawableItem item : drawables) {
            item.selected = false;
        }

        gridAdapter.notifyDataSetChanged();
    }

    private class GridAdapter extends BaseAdapter
            implements View.OnClickListener, View.OnLongClickListener {

        @Override
        public int getCount() {
            return drawables.size();
        }

        @Override
        public DrawableItem getItem(int pos) {
            return drawables.get(pos);
        }

        @Override
        public long getItemId(int p1) {
            return 0;
        }

        @Override
        public View getView(int pos, View buffer, ViewGroup p3) {
            DrawableItem item = getItem(pos);

            LayoutDrawableGridItemBinding bind =
                    LayoutDrawableGridItemBinding.inflate(getLayoutInflater());
            bind.name.setText(item.name);
            bind.image.setImageDrawable(item.drawable);

            // if (item.selected) bind.imgCheck.setImageDrawable(ic_check);

            bind.imgCheck.setImageDrawable(item.selected ? ic_check : ic_delete);

            if (isSelectedMode && !item.name.equals("default_image")) {
                bind.imgCheck.animate().alpha(1).setDuration(100).start();
            } else {
                bind.imgCheck.animate().alpha(0).setDuration(100).start();
            }

            bind.getRoot().setTag(pos);
            bind.getRoot().setOnClickListener(this);
            bind.getRoot().setOnLongClickListener(this);

            return bind.getRoot();
        }

        @Override
        public void onClick(View v) {
            DrawableItem item = drawables.get((int) v.getTag());

            if (isSelectedMode) {
                if (item.name.equals("default_image")) {
                    SBUtils.make(binding.getRoot(), "You cannot select the default image..")
                            .setType(SBUtils.Type.ERROR)
                            .setSlideAnimation()
                            .setAnchorView(binding.fab)
                            .show();
                    return;
                }
                item.selected = !item.selected;

                AppCompatImageView check = v.findViewById(R.id.img_check);
                check.setImageDrawable(item.selected ? ic_check : ic_delete);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            DrawableItem item = drawables.get((int) v.getTag());

            if (item.name.equals("default_image")) {
                return false;
            }

            if (!isSelectedMode) {
                item.selected = true;
                startSelection();
            }

            return true;
        }
    }

    private class DrawableItem {
        private Drawable drawable;
        private String name;
        private String path;

        public DrawableItem(Drawable drawable, String name, String path) {
            this.drawable = drawable;
            this.name = name;
            this.path = path;
        }

        private boolean selected;
    }

    @Override
    public void onBackPressed() {
        if (isSelectedMode) {
            stopSelection();
            return;
        }
        super.onBackPressed();
    }
}
