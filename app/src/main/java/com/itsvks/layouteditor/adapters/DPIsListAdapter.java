package com.itsvks.layouteditor.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.itsvks.layouteditor.databinding.LayoutSelectDpiItemBinding;
import com.itsvks.layouteditor.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DPIsListAdapter extends RecyclerView.Adapter<DPIsListAdapter.VH> {
  private Drawable image;
  private List<String> dpiList;
  private List<Boolean> mSelectedItems;

  public DPIsListAdapter(Drawable image) {
    this.image = image;
    dpiList = new ArrayList<>();
    dpiList.add("ldpi");
    dpiList.add("mdpi");
    dpiList.add("hdpi");
    dpiList.add("xhdpi");
    dpiList.add("xxhdpi");
    dpiList.add("xxxhdpi");

    mSelectedItems = new ArrayList<>(Collections.nCopies(dpiList.size(), false));
  }

  public class VH extends RecyclerView.ViewHolder {
    public LinearLayout shadowView;
    public ImageView image;
    public CheckBox checkbox;
    public TextView dpiName;

    public VH(LayoutSelectDpiItemBinding binding) {
      super(binding.getRoot());

      shadowView = binding.shadowView;
      image = binding.image;
      checkbox = binding.checkbox;
      dpiName = binding.dpiName;
    }
  }

  @Override
  public VH onCreateViewHolder(ViewGroup parent, int viewType) {
    return new VH(
        LayoutSelectDpiItemBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    holder.image.setLayoutParams(
        new RelativeLayout.LayoutParams(Utils.getScreenWidth() / 2, Utils.getScreenWidth() / 2));
    holder.shadowView.setLayoutParams(
        new RelativeLayout.LayoutParams(Utils.getScreenWidth() / 2, Utils.getScreenWidth() / 2));
    String dpi = dpiList.get(position);
    holder.image.setImageDrawable(image);
    holder.dpiName.setText("drawable-".concat(dpi));
    holder.checkbox.setChecked(mSelectedItems.get(position));
    holder.shadowView.setVisibility(mSelectedItems.get(position) ? View.VISIBLE : View.INVISIBLE);
    holder.itemView.setOnClickListener(
        view -> {
          boolean isChecked = !mSelectedItems.get(position);
          mSelectedItems.set(position, isChecked);
          holder.checkbox.setChecked(isChecked);
        });
  }

  @Override
  public int getItemCount() {
    return dpiList.size();
  }

  public List<String> getSelectedItems() {
    List<String> selectedItems = new ArrayList<>();
    for (int i = 0; i < mSelectedItems.size(); i++) {
      if (mSelectedItems.get(i)) {
        selectedItems.add(dpiList.get(i));
      }
    }
    return selectedItems;
  }
}
