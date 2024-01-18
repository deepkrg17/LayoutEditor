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
import com.itsvks.layouteditor.utils.BitmapUtil;
import com.itsvks.layouteditor.utils.FileUtil;
import com.itsvks.layouteditor.utils.NameErrorChecker;
import com.itsvks.layouteditor.utils.SBUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;

public class StringResourceAdapter extends RecyclerView.Adapter<StringResourceAdapter.VH> {

  private List<ValuesItem> stringList = new ArrayList<>();
  private ProjectFile project;

  public StringResourceAdapter(ProjectFile project, List<ValuesItem> stringList) {
    this.project = project;
    this.stringList = stringList;
  }

  public class VH extends RecyclerView.ViewHolder {
    LayoutValuesItemBinding binding;
    TextView stringName;
    TextView stringValue;

    public VH(@NonNull LayoutValuesItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;

      stringName = binding.name;
      stringValue = binding.value;
    }
  }

  @Override
  public VH onCreateViewHolder(ViewGroup parent, int viewType) {
    return new VH(
        LayoutValuesItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    holder.stringName.setText(stringList.get(position).name);
    holder.stringValue.setText(stringList.get(position).value);
    holder
        .binding
        .getRoot()
        .setAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.getContext(), R.anim.project_list_animation));

    holder.binding.menu.setOnClickListener(v -> showOptions(v, position));
    holder
        .binding
        .getRoot()
        .setOnClickListener(
            v -> {
              editString(v, position);
            });
  }

  @Override
  public int getItemCount() {
    return stringList.size();
  }

  public void generateStringsXml() {
    String stringsPath = project.getStringsPath();

    StringBuilder sb = new StringBuilder();
    sb.append("<resources>\n");
    for (ValuesItem stringItem : stringList) {
      // Generate string item code
      sb.append("\t<string name=\"")
          .append(stringItem.name)
          .append("\">")
          .append(StringEscapeUtils.escapeXml11(stringItem.value))
          .append("</string>\n");
    }
    sb.append("</resources>");

    FileUtil.writeFile(stringsPath, sb.toString().trim());
  }

  private void showOptions(View v, int position) {
    final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
    popupMenu.inflate(R.menu.menu_values);
    popupMenu.setOnMenuItemClickListener(
        item -> {
          var id = item.getItemId();
          if (id == R.id.menu_copy_name) {
            ClipboardUtils.copyText(stringList.get(position).name);
            SBUtils.make(
                v,
                v.getContext().getString(R.string.copied)
                  + " "
                  + stringList.get(position).name)
              .setSlideAnimation()
              .showAsSuccess();
            return true;
          } else if (id == R.id.menu_delete) {
            new MaterialAlertDialogBuilder(v.getContext())
              .setTitle("Remove String")
              .setMessage(
                String.format("Do you want to remove %s?", stringList.get(position).name))
              .setNegativeButton(R.string.no, null)
              .setPositiveButton(
                R.string.yes,
                (d, w) -> {
                  var name = stringList.get(position).name;
                  if (name.equals("default_string")) {
                    SBUtils.make(
                        v,
                        v.getContext()
                          .getString(R.string.msg_cannot_delete_default, "string"))
                      .setFadeAnimation()
                      .setType(SBUtils.Type.INFO)
                      .show();
                  } else {
                    stringList.remove(position);
                    notifyDataSetChanged();
                    generateStringsXml();
                  }
                })
              .show();
            return true;
          }
          return false;
        });

    popupMenu.show();
  }

  private void editString(View v, int pos) {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
    builder.setTitle("Edit String");

    LayoutValuesItemDialogBinding bind =
        LayoutValuesItemDialogBinding.inflate(builder.create().getLayoutInflater());
    TextInputLayout ilName = bind.textInputLayoutName;
    TextInputLayout ilValue = bind.textInputLayoutValue;
    TextInputEditText etName = bind.textinputName;
    TextInputEditText etValue = bind.textinputValue;

    etName.setText(stringList.get(pos).name);
    etValue.setText(stringList.get(pos).value);
    builder.setView(bind.getRoot());
    builder.setPositiveButton(
        R.string.okay,
        (dlg, i) -> {
          if (stringList.get(pos).name.equals("default_string") && !etName.getText().toString().equals("default_string")) {
            SBUtils.make(v, v.getContext().getString(R.string.msg_cannot_rename_default, "string"))
                .setFadeAnimation()
                .setType(SBUtils.Type.INFO)
                .show();
          } else {
            // Update position
            stringList.get(pos).name = etName.getText().toString();
          }
          // Update position
          stringList.get(pos).value = etValue.getText().toString();
          notifyDataSetChanged();
          // Generate code from all strings in list
          generateStringsXml();
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
                etName.getText().toString(), ilName, dialog, stringList, pos);
          }
        });
    NameErrorChecker.checkForValues(etName.getText().toString(), ilName, dialog, stringList, pos);
  }
}
