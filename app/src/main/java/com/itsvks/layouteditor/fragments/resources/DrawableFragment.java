package com.itsvks.layouteditor.fragments.resources;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.adapters.DrawableResourceAdapter;
import com.itsvks.layouteditor.adapters.models.DrawableFile;
import com.itsvks.layouteditor.databinding.FragmentResourcesBinding;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.NameErrorChecker;
import com.itsvks.layouteditor.R;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DrawableFragment extends Fragment {

  private FragmentResourcesBinding binding;
  private DrawableResourceAdapter adapter;
  private ProjectFile project;
  private RecyclerView mRecyclerView;
  List<DrawableFile> drawableList = new ArrayList<>();

  public DrawableFragment(ProjectFile project, List<DrawableFile> drawableList) {
    this.drawableList = drawableList;
    this.project = project;
  }

  public DrawableFragment() {}

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentResourcesBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    loadDrawables();
    mRecyclerView = binding.recyclerView;
    // Create the adapter and set it to the RecyclerView
    adapter = new DrawableResourceAdapter(drawableList, project);
    mRecyclerView.setAdapter(adapter);
    mRecyclerView.setLayoutManager(
        new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
  }

  public void loadDrawables() {
    File[] files = project.getDrawables();

    if (files == null) {
      ToastUtils.showLong("Null");
    } else {

      for (File file : files) {
        Drawable drawable = Drawable.createFromPath(file.getPath());
        String name = file.getName();
        // name = name.substring(0, name.lastIndexOf("."));
        drawableList.add(new DrawableFile(name, drawable, file.getPath()));
      }
    }
  }

  public void addDrawable(final String path) {
    if (TextUtils.isEmpty(path)) {
      ToastUtils.showLong(R.string.invalid_data_intent);
      return;
    }
    // File name with extension
    final String lastSegment = FileUtil.getLastSegmentFromPath(path);

    // File name without extension
    final String fileName = lastSegment.substring(0, lastSegment.lastIndexOf("."));

    // Extension
    final String extension =
        lastSegment.substring(lastSegment.lastIndexOf("."), lastSegment.length());
    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
    final TextinputlayoutBinding bind =
        TextinputlayoutBinding.inflate(builder.create().getLayoutInflater());
    final TextInputEditText editText = bind.textinputEdittext;
    final TextInputLayout inputLayout = bind.textinputLayout;
    inputLayout.setHint(R.string.msg_enter_new_name);
    editText.setText(fileName);

    builder.setView(bind.getRoot());
    builder.setTitle(R.string.add_drawable);
    builder.setNegativeButton(R.string.cancel, (di, which) -> {});
    builder.setPositiveButton(
        R.string.add,
        (di, which) -> {
          String drawablePath = project.getDrawablePath();

          String toPath = drawablePath + editText.getText().toString() + extension;
          FileUtil.copyFile(path, toPath);

          Drawable drawable = Drawable.createFromPath(toPath);
          String name = editText.getText().toString();
          drawableList.add(new DrawableFile(name + extension, drawable, toPath));
          // holder.drawableName.setText(name);
          // holder.drawable.setImageDrawable(drawable);
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
            NameErrorChecker.checkForDrawable(
                editText.getText().toString(), inputLayout, dialog, drawableList);
          }
        });

    NameErrorChecker.checkForDrawable(fileName, inputLayout, dialog, drawableList);

    editText.requestFocus();
    InputMethodManager inputMethodManager =
        (InputMethodManager)
            bind.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

    if (!editText.getText().toString().equals("")) {
      editText.setSelection(0, editText.getText().toString().length());
    }
  }
}
