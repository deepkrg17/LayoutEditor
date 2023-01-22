package com.itsvks.layouteditor.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.adapters.models.DrawableFile;
import com.itsvks.layouteditor.databinding.LayoutDrawableItemBinding;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;
import com.itsvks.layouteditor.utils.BitmapUtil;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.NameErrorChecker;
import com.itsvks.layouteditor.utils.SBUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DrawableResourceAdapter extends RecyclerView.Adapter<DrawableResourceAdapter.VH> {

  private List<DrawableFile> drawableList = new ArrayList<>();
  private ProjectFile project;

  public DrawableResourceAdapter(List<DrawableFile> drawableList, ProjectFile project) {
    this.drawableList = drawableList;
    this.project = project;
  }

  public class VH extends RecyclerView.ViewHolder {
    LayoutDrawableItemBinding binding;
    AppCompatTextView drawableName;
    AppCompatImageView drawable;

    public VH(@NonNull LayoutDrawableItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;

      drawableName = binding.drawableName;
      drawable = binding.image;
    }
  }

  @Override
  public VH onCreateViewHolder(ViewGroup parent, int viewType) {
    return new VH(
        LayoutDrawableItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    var name = drawableList.get(position).name;
    holder
        .binding
        .getRoot()
        .setAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.getContext(), R.anim.project_list_animation));
    holder.drawableName.setText(name.substring(0, name.lastIndexOf(".")));

    BitmapUtil.setBackgroundAccordingToImage(holder.itemView.getContext(), holder.binding.getRoot(), drawableList.get(position).drawable);
    BitmapUtil.setImageTintAccordingToBackground(holder.binding.menu, holder.binding.getRoot());

    if (name.endsWith(".xml") || name.endsWith(".svg")) {
      // TODO: Set vector drawable to ImageView
      var icon = VectorDrawableCompat.createFromPath(drawableList.get(position).path);
      holder.drawable.setImageDrawable(icon);
    } else holder.drawable.setImageDrawable(drawableList.get(position).drawable);
    holder.binding.menu.setOnClickListener(v -> showOptions(v, position, holder));
  }

  @Override
  public int getItemCount() {
    return drawableList.size();
  }

  private void showOptions(View v, int position, VH holder) {
    final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
    popupMenu.inflate(R.menu.menu_drawable);
    popupMenu.setOnMenuItemClickListener(
        new PopupMenu.OnMenuItemClickListener() {

          @Override
          public boolean onMenuItemClick(MenuItem item) {

            var id = item.getItemId();
            switch (id) {
              case R.id.menu_copy_name:
                ClipboardUtils.copyText(
                    drawableList
                        .get(position)
                        .name
                        .substring(0, drawableList.get(position).name.lastIndexOf(".")));
                return true;
              case R.id.menu_delete:
                FileUtil.deleteFile(drawableList.get(position).path);
                drawableList.remove(position);
                notifyDataSetChanged();
                return true;
              case R.id.menu_rename:
                rename(v, position, holder);
                return true;
            }
            return false;
          }
        });

    popupMenu.show();
  }

  @SuppressWarnings("deprecation")
  @SuppressLint("RestrictedApi")
  private void rename(View v, int position, VH holder) {
    // File name with extension
    final String lastSegment = FileUtil.getLastSegmentFromPath(drawableList.get(position).path);

    // File name without extension
    final String fileName = lastSegment.substring(0, lastSegment.lastIndexOf("."));

    // Extension
    final String extension =
        lastSegment.substring(lastSegment.lastIndexOf("."), lastSegment.length());
    
    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
    final TextinputlayoutBinding bind =
        TextinputlayoutBinding.inflate(builder.create().getLayoutInflater());
    final TextInputEditText editText = bind.textinputEdittext;
    final TextInputLayout inputLayout = bind.textinputLayout;
    editText.setText(fileName);
    builder.setView(bind.getRoot());
    builder.setTitle(R.string.rename_drawable);
    builder.setNegativeButton(R.string.cancel, (di, which) -> {});
    builder.setPositiveButton(
        R.string.rename,
        (di, which) -> {
          String drawablePath = project.getDrawablePath();

          String toPath = drawablePath + editText.getText().toString() + extension;
          File newFile = new File(toPath);
          File oldFile = new File(drawableList.get(position).path);
          oldFile.renameTo(newFile);

          Drawable drawable = Drawable.createFromPath(toPath);
          String name = editText.getText().toString();
          drawableList.get(position).path = toPath;
          drawableList.get(position).name = FileUtil.getLastSegmentFromPath(toPath);
          if (drawableList.get(position).name.endsWith(".xml")
              || drawableList.get(position).name.endsWith(".svg")) {
            // TODO: Set vector drawable to ImageView
            drawable = VectorDrawableCompat.createFromPath(toPath);
            holder.drawable.setImageDrawable(drawable);
          }
          holder.drawableName.setText(name);
          holder.drawable.setImageDrawable(drawable);
          notifyItemChanged(position);
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
        (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

    if (!editText.getText().toString().equals("")) {
      editText.setSelection(0, editText.getText().toString().length());
    }
  }
  
  public void addDrawable(final String path, Context context) {
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
    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
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
          notifyDataSetChanged();
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
        (InputMethodManager) bind.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

    if (!editText.getText().toString().equals("")) {
      editText.setSelection(0, editText.getText().toString().length());
    }
  }
}
