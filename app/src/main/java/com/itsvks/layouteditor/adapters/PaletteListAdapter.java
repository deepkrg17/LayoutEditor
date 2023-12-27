package com.itsvks.layouteditor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.VibrateUtils;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.databinding.LayoutPaletteItemBinding;
import com.itsvks.layouteditor.utils.InvokeUtil;
import java.util.HashMap;
import java.util.List;

public class PaletteListAdapter extends RecyclerView.Adapter<PaletteListAdapter.ViewHolder> {

  private List<HashMap<String, Object>> tab;
  private DrawerLayout drawerLayout;

  public PaletteListAdapter(DrawerLayout drawerLayout) {
    this.drawerLayout = drawerLayout;
  }

  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(
        LayoutPaletteItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    HashMap<String, Object> widgetItem = tab.get(position);

    var binding = holder.binding;

    binding.icon.setImageResource(InvokeUtil.getMipmapId(widgetItem.get("iconName").toString()));
    binding.name.setText(widgetItem.get("name").toString());
    binding.className.setText(InvokeUtil.getSuperClassName(widgetItem.get("className").toString()));

    binding
        .getRoot()
        .setOnLongClickListener(
            v -> {
              if (ViewCompat.startDragAndDrop(
                  v, null, new View.DragShadowBuilder(v), widgetItem, 0)) {
                drawerLayout.closeDrawers();
              }
              return true;
            });

    binding
        .getRoot()
        .setAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.getContext(), R.anim.project_list_animation));
  }

  @Override
  public int getItemCount() {
    return tab != null ? tab.size() : 0;
  }

  public void submitPaletteList(List<HashMap<String, Object>> tab) {
    this.tab = tab;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    LayoutPaletteItemBinding binding;

    public ViewHolder(@NonNull LayoutPaletteItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }
  }
}
