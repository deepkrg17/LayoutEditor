package com.itsvks.layouteditor.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ClipboardUtils;
import com.itsvks.layouteditor.adapters.models.ColorItem;
import com.itsvks.layouteditor.databinding.LayoutColorItemBinding;
import com.itsvks.layouteditor.utils.BitmapUtil;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.utils.SBUtils;
import java.util.ArrayList;
import java.util.List;

public class ColorResourceAdapter extends RecyclerView.Adapter<ColorResourceAdapter.VH> {

  private List<ColorItem> colorList = new ArrayList<>();

  public ColorResourceAdapter(List<ColorItem> colorList) {
    this.colorList = colorList;
  }

  public class VH extends RecyclerView.ViewHolder {
    LayoutColorItemBinding binding;
    AppCompatTextView colorName;
    AppCompatTextView colorValue;

    public VH(@NonNull LayoutColorItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;

      colorName = binding.colorName;
      colorValue = binding.colorValue;
    }
  }

  @Override
  public VH onCreateViewHolder(ViewGroup parent, int viewType) {
    return new VH(
        LayoutColorItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    holder.colorName.setText(colorList.get(position).colorName);
    holder.colorValue.setText(colorList.get(position).colorValue);
    holder
        .binding
        .getRoot()
        .setAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.getContext(), R.anim.project_list_animation));
    holder
        .binding
        .getRoot()
        .setCardBackgroundColor(Color.parseColor(colorList.get(position).colorValue));
    BitmapUtil.setTextColorAccordingToBackground(holder.binding.getRoot(), holder.colorName);
    BitmapUtil.setImageTintAccordingToBackground(holder.binding.menu, holder.binding.getRoot());
    if (BitmapUtil.getLuminance(holder.binding.getRoot()) >= 0.5) {
      holder.colorValue.setTextColor(Color.parseColor("#FF313131"));
    } else {
      holder.colorValue.setTextColor(Color.parseColor("#FFD9D9D9"));
    }
    holder.binding.menu.setOnClickListener(v -> showOptions(v, position));
    holder
        .binding
        .getRoot()
        .setOnClickListener(
            v -> {
              ClipboardUtils.copyText(colorList.get(position).colorValue);
              SBUtils.make(
                      holder.binding.getRoot(),
                      v.getContext().getString(R.string.copied)
                          + " "
                          + colorList.get(position).colorValue)
                  .setSlideAnimation()
                  .showAsSuccess();
            });
  }

  @Override
  public int getItemCount() {
    return colorList.size();
  }
  
  private void showOptions(View v, int position) {
    final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
    popupMenu.inflate(R.menu.menu_project_file_options);
    popupMenu.setOnMenuItemClickListener(
        new PopupMenu.OnMenuItemClickListener() {

          @Override
          public boolean onMenuItemClick(MenuItem item) {

            var id = item.getItemId();
            switch (id) {
              case R.id.menu_delete:
                // TODO: Delete color
                return true;
              case R.id.menu_rename:
                // TODO: Rename color
                return true;
            }
            return false;
          }
        });

    popupMenu.show();
  }
}
