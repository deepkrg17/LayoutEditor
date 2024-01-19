package com.itsvks.layouteditor.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ClipboardUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.adapters.models.FontItem;
import com.itsvks.layouteditor.databinding.LayoutFontItemBinding;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;
import com.itsvks.layouteditor.managers.ProjectManager;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.NameErrorChecker;
import com.itsvks.layouteditor.utils.SBUtils;
import com.itsvks.layouteditor.utils.Utils;
import java.io.File;
import java.util.List;

public class FontResourceAdapter extends RecyclerView.Adapter<FontResourceAdapter.VH> {

  private List<FontItem> fontList;
  private ProjectFile project;

  public FontResourceAdapter(List<FontItem> fontList) {
    this.fontList = fontList;
    this.project = ProjectManager.getInstance().getOpenedProject();
  }

  public class VH extends RecyclerView.ViewHolder {
    LayoutFontItemBinding binding;
    TextView fontName;
    TextView fontLook;

    public VH(LayoutFontItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;

      this.fontName = binding.name;
      this.fontLook = binding.fontLook;
    }
  }

  @Override
  public VH onCreateViewHolder(ViewGroup parent, int viewType) {
    return new VH(
        LayoutFontItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    var fontName = fontList.get(position).name;
    holder
        .binding
        .getRoot()
        .setAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.getContext(), R.anim.project_list_animation));
    holder.fontName.setText(fontName.substring(0, fontName.lastIndexOf(".")));
    holder.fontLook.setTypeface(
        Typeface.createFromFile(new File(fontList.get(position).path)));

    holder
        .binding
        .getRoot()
        .setOnClickListener(
            v -> {
              ClipboardUtils.copyText(fontName.substring(0, fontName.lastIndexOf(".")));
              SBUtils.make(
                      holder.binding.getRoot(),
                      v.getContext().getString(R.string.copied) + " ".concat(fontName))
                  .setSlideAnimation()
                  .showAsSuccess();
            });
    holder.binding.menu.setOnClickListener(v -> showOptions(v, position, holder));
  }

  @Override
  public int getItemCount() {
    return fontList.size();
  }

  private void showOptions(View v, int position, VH holder) {
    final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
    popupMenu.inflate(R.menu.menu_font);
    popupMenu.setOnMenuItemClickListener(
        new PopupMenu.OnMenuItemClickListener() {

          @Override
          public boolean onMenuItemClick(MenuItem item) {

            var id = item.getItemId();
            if (id == R.id.menu_delete) {
              new MaterialAlertDialogBuilder(v.getContext())
                .setTitle(R.string.remove_font)
                .setMessage(R.string.msg_remove_font)
                .setNegativeButton(R.string.no, (d, w) -> d.dismiss())
                .setPositiveButton(
                  R.string.yes,
                  (d, w) -> {
                    var name = fontList.get(position).name;
                    if (name.substring(0, name.lastIndexOf(".")).equals("default_font")) {
                      SBUtils.make(
                          v,
                          v.getContext()
                            .getString(
                              R.string.msg_cannot_delete_default, "font"))
                        .setFadeAnimation()
                        .setType(SBUtils.Type.INFO)
                        .show();
                    } else {
                      FileUtil.deleteFile(fontList.get(position).path);
                      fontList.remove(position);
                      notifyDataSetChanged();
                    }
                  })
                .show();
              return true;
            } else if (id == R.id.menu_rename) {
              rename(v, position, holder);
              return true;
            }
            return false;
          }
        });

    popupMenu.show();
  }

  private void rename(View v, int position, VH holder) {
    final String lastSegment = FileUtil.getLastSegmentFromPath(fontList.get(position).path);
    final String fileName = lastSegment.substring(0, lastSegment.lastIndexOf("."));
    final String extension =
        lastSegment.substring(lastSegment.lastIndexOf("."), lastSegment.length());
    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
    final TextinputlayoutBinding bind =
        TextinputlayoutBinding.inflate(builder.create().getLayoutInflater());
    final TextInputEditText editText = bind.textinputEdittext;
    final TextInputLayout inputLayout = bind.textinputLayout;
    editText.setText(fileName);
    var padding = Utils.pxToDp(builder.getContext(), 10);
    builder.setView(bind.getRoot(), padding, padding, padding, padding);
    builder.setTitle(R.string.rename_font);
    builder.setNegativeButton(R.string.cancel, (di, which) -> {});
    builder.setPositiveButton(
        R.string.rename,
        (di, which) -> {
          if (fontList
              .get(position).name
              .substring(0, fontList.get(position).name.lastIndexOf("."))
              .equals("default_font")) {
            SBUtils.make(v, v.getContext().getString(R.string.msg_cannot_rename_default, "font"))
                .setFadeAnimation()
                .setType(SBUtils.Type.INFO)
                .show();
          } else {
            String fontPath = project.getFontPath();

            String toPath = fontPath + editText.getText().toString() + extension;
            File newFile = new File(toPath);
            File oldFile = new File(fontList.get(position).path);
            oldFile.renameTo(newFile);

            String name = editText.getText().toString();
            fontList.get(position).path = toPath;
            fontList.get(position).name = FileUtil.getLastSegmentFromPath(toPath);
            holder.fontName.setText(name);
            holder.fontLook.setTypeface(
                Typeface.createFromFile(new File(fontList.get(position).path)));
            notifyItemChanged(position);
          }
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
            NameErrorChecker.checkForFont(
                editText.getText().toString(), inputLayout, dialog, fontList, position);
          }
        });

    NameErrorChecker.checkForFont(fileName, inputLayout, dialog, fontList, position);

    editText.requestFocus();
    InputMethodManager inputMethodManager =
        (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

    if (!editText.getText().toString().equals("")) {
      editText.setSelection(0, editText.getText().toString().length());
    }
  }
}
