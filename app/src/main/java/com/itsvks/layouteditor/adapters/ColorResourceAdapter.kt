package com.itsvks.layouteditor.adapters;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ClipboardUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.adapters.models.ValuesItem;
import com.itsvks.layouteditor.databinding.LayoutValuesItemBinding;
import com.itsvks.layouteditor.databinding.LayoutValuesItemDialogBinding;
import com.itsvks.layouteditor.editor.dialogs.ColorDialog;
import com.itsvks.layouteditor.utils.BitmapUtil;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.NameErrorChecker;
import com.itsvks.layouteditor.utils.SBUtils;
import java.util.ArrayList;
import java.util.List;

public class ColorResourceAdapter extends RecyclerView.Adapter<ColorResourceAdapter.VH> {

  private List<ValuesItem> colorList = new ArrayList<>();
  private ProjectFile project;

  public ColorResourceAdapter(ProjectFile project, List<ValuesItem> colorList) {
    this.project = project;
    this.colorList = colorList;
  }

  public class VH extends RecyclerView.ViewHolder {
    LayoutValuesItemBinding binding;
    TextView colorName;
    TextView colorValue;

    public VH(@NonNull LayoutValuesItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;

      colorName = binding.name;
      colorValue = binding.value;
    }
  }

  @Override
  public VH onCreateViewHolder(ViewGroup parent, int viewType) {
    return new VH(
        LayoutValuesItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    holder.colorName.setText(colorList.get(position).name);
    holder.colorValue.setText(colorList.get(position).value);
    holder
        .binding
        .getRoot()
        .setAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.getContext(), R.anim.project_list_animation));
    holder
        .binding
        .getRoot()
        .setBackgroundColor(Color.parseColor(colorList.get(position).value));
    BitmapUtil.setTextColorAccordingToBackground(holder.binding.getRoot(), holder.colorName);
    BitmapUtil.setImageTintAccordingToBackground(holder.binding.menu, holder.binding.getRoot());
    if (BitmapUtil.getLuminance(holder.binding.getRoot()) >= 0.5) {
      holder.colorValue.setTextColor(Color.parseColor("#FF313131"));
      holder.binding.versions.setTextColor(Color.parseColor("#FF313131"));
    } else {
      holder.colorValue.setTextColor(Color.parseColor("#FFD9D9D9"));
      holder.binding.versions.setTextColor(Color.parseColor("#FFD9D9D9"));
    }
    holder.binding.menu.setOnClickListener(v -> showOptions(v, position));
    holder
        .binding
        .getRoot()
        .setOnClickListener(
            v -> {
              editColor(v, position);
            });
  }

  @Override
  public int getItemCount() {
    return colorList.size();
  }

  public void generateColorsXml() {
    String colorsPath = project.getColorsPath();

    StringBuilder sb = new StringBuilder();
    sb.append("<resources>\n");
    for (ValuesItem colorItem : colorList) {
      // Generate color item code
      sb.append("\t<color name=\"")
          .append(colorItem.name)
          .append("\">")
          .append(colorItem.value)
          .append("</color>\n");
    }
    sb.append("</resources>");

    FileUtil.writeFile(colorsPath, sb.toString().trim());
  }

  private void showOptions(View v, int position) {
    final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
    popupMenu.inflate(R.menu.menu_values);
    popupMenu.setOnMenuItemClickListener(
        new PopupMenu.OnMenuItemClickListener() {

          @Override
          public boolean onMenuItemClick(MenuItem item) {

            var id = item.getItemId();
            switch (id) {
              case R.id.menu_copy_name:
                ClipboardUtils.copyText(colorList.get(position).name);
                SBUtils.make(
                        v,
                        v.getContext().getString(R.string.copied)
                            + " "
                            + colorList.get(position).name)
                    .setSlideAnimation()
                    .showAsSuccess();
                return true;
              case R.id.menu_delete:
                new MaterialAlertDialogBuilder(v.getContext())
                    .setTitle("Remove Color")
                    .setMessage("Do you want to remove " + colorList.get(position).name + "?")
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(
                        R.string.yes,
                        (d, w) -> {
                          var name = colorList.get(position).name;
                          if (name.equals("default_color")) {
                            SBUtils.make(
                                    v,
                                    v.getContext()
                                        .getString(
                                            R.string.msg_cannot_delete_default, "color"))
                                .setFadeAnimation()
                                .setType(SBUtils.Type.INFO)
                                .show();
                          } else {
                            colorList.remove(position);
                            notifyDataSetChanged();
                            generateColorsXml();
                          }
                        })
                    .show();
                return true;
            }
            return false;
          }
        });

    popupMenu.show();
  }

  private void editColor(View v, int pos) {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
    builder.setTitle("Edit Color");

    LayoutValuesItemDialogBinding bind =
        LayoutValuesItemDialogBinding.inflate(builder.create().getLayoutInflater());
    TextInputLayout ilName = bind.textInputLayoutName;
    TextInputLayout ilValue = bind.textInputLayoutValue;
    TextInputEditText etName = bind.textinputName;
    TextInputEditText etValue = bind.textinputValue;

    etName.setText(colorList.get(pos).name);
    etValue.setText(colorList.get(pos).value);
    etValue.setFocusable(false);
    builder.setView(bind.getRoot());
    etValue.setOnClickListener(
        (view) -> {
          // ColorPicker
          ColorDialog dialog = new ColorDialog(v.getContext(), etValue.getText().toString());
          dialog.setOnSaveValueListener(
              value -> {
                etValue.setText(value);
              });
          dialog.show();
        });

    builder.setPositiveButton(
        R.string.okay,
        (dlg, i) -> {
          if (colorList.get(pos).name.equals("default_color") && !etName.getText().toString().equals("default_color")) {
            SBUtils.make(v, v.getContext().getString(R.string.msg_cannot_rename_default, "color"))
                .setFadeAnimation()
                .setType(SBUtils.Type.INFO)
                .show();
          } else {
            // Update position
            colorList.get(pos).name = etName.getText().toString();
          }
          // Update position
          colorList.get(pos).value = etValue.getText().toString();
          notifyDataSetChanged();
          // Generate code from all colors in list
          generateColorsXml();
        });
    builder.setNegativeButton(R.string.cancel, null);

    AlertDialog dialog = builder.create();
    dialog.show();

    etName.addTextChangedListener(
        new TextWatcher() {

          @Override
          public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

          @Override
          public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {}

          @Override
          public void afterTextChanged(Editable p1) {
            NameErrorChecker.checkForValues(
                etName.getText().toString(), ilName, dialog, colorList, pos);
          }
        });
    NameErrorChecker.checkForValues(etName.getText().toString(), ilName, dialog, colorList, pos);
  }
}
