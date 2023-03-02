package com.itsvks.layouteditor.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import com.blankj.utilcode.util.ClipboardUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.activities.PreviewDrawableActivity;
import com.itsvks.layouteditor.adapters.models.DrawableFile;
import com.itsvks.layouteditor.databinding.LayoutDrawableItemBinding;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.databinding.LayoutPreviewDrawableBinding;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;
import com.itsvks.layouteditor.interfaces.PreviewDrawableListener;
import com.itsvks.layouteditor.utils.BitmapUtil;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.NameErrorChecker;
import com.itsvks.layouteditor.utils.SBUtils;
import com.itsvks.layouteditor.views.AlphaPatternDrawable;
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
    TextView drawableName;
    TextView imageType;
    TextView versions;
    ImageView drawable;
    ImageView drawableBackground;

    public VH(@NonNull LayoutDrawableItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;

      drawableName = binding.drawableName;
      drawableBackground = binding.background;
      drawable = binding.drawable;
      imageType = binding.imageType;
      versions = binding.versions;
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
    holder.imageType.setText("Drawable");
    holder.versions.setText("1 version");
    holder.drawableBackground.setImageDrawable(new AlphaPatternDrawable(16));
    TooltipCompat.setTooltipText(
        holder.binding.getRoot(), name.substring(0, name.lastIndexOf(".")));
    TooltipCompat.setTooltipText(holder.binding.menu, "Options");

    if (name.endsWith(".xml") || name.endsWith(".svg")) {
      // TODO: Set vector drawable to ImageView
      var icon = VectorDrawableCompat.createFromPath(drawableList.get(position).path);
      holder.drawable.setImageDrawable(icon);
    } else holder.drawable.setImageDrawable(drawableList.get(position).drawable);
    holder.binding.menu.setOnClickListener(v -> showOptions(v, position, holder));

    PreviewDrawableListener listener =
        new PreviewDrawableListener() {

          @Override
          public void showInImage(ImageView imageView) {
            Glide.with(holder.itemView.getContext())
                .load(drawableList.get(position).drawable)
                .placeholder(R.drawable.image_broken)
                .into(imageView);
          }

          @Override
          public void setSubtitle(ActionBar actionBar) {
            actionBar.setSubtitle(name);
          }
        };
    holder
        .binding
        .getRoot()
        .setOnClickListener(
            v -> {
              PreviewDrawableActivity.setListener(listener);
              v.getContext()
                  .startActivity(new Intent(v.getContext(), PreviewDrawableActivity.class));
            });
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
                SBUtils.make(holder.binding.getRoot(), v.getContext().getString(R.string.copied))
                    .setSlideAnimation()
                    .showAsSuccess();
                return true;
              case R.id.menu_delete:
                new MaterialAlertDialogBuilder(v.getContext())
                    .setTitle(R.string.remove_drawable)
                    .setMessage(R.string.msg_remove_drawable)
                    .setNegativeButton(R.string.no, (d, w) -> d.dismiss())
                    .setPositiveButton(
                        R.string.yes,
                        (d, w) -> {
                          FileUtil.deleteFile(drawableList.get(position).path);
                          drawableList.remove(position);
                          notifyDataSetChanged();
                        })
                    .show();
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
}
