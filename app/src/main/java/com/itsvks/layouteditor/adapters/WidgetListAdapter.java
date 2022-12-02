package com.itsvks.layouteditor.adapters;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;

import com.itsvks.layouteditor.databinding.WidgetsListBinding;
import com.itsvks.layouteditor.utils.InvokeUtil;
import java.util.HashMap;
import java.util.List;

public class WidgetListAdapter extends RecyclerView.Adapter<WidgetListAdapter.ViewHolder> {
    
  private List<HashMap<String, Object>> list;
  private WidgetListClickListener listener;

  public WidgetListAdapter(List<HashMap<String, Object>> list, WidgetListClickListener listener) {
    this.list = list;
    this.listener = listener;
  }

  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(
        WidgetsListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    HashMap<String, Object> widgetItem = list.get(position);
    holder.widgetName.setText(widgetItem.get("name").toString());

    final float density = holder.widgetName.getContext().getResources().getDisplayMetrics().density;
    final int width = Math.round(20 * density);
    final int height = Math.round(20 * density);

    Drawable icon =
        AppCompatResources.getDrawable(
            holder.widgetName.getContext(),
            InvokeUtil.getMipmapId(widgetItem.get("iconName").toString()));
    icon.setBounds(0, 0, width, height);

    var iconColor =
        MaterialColors.getColor(
            holder.widgetName, com.google.android.material.R.attr.colorOnSurface);

    icon.setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN));
    holder.widgetName.setCompoundDrawables(icon, null, null, null);

    holder
        .binding
        .getRoot()
        .setOnLongClickListener(
            new View.OnLongClickListener() {

              @Override
              public boolean onLongClick(View view) {
                return listener.onWidgetLongClicked(view, widgetItem);
              }
            });

    holder.binding.getRoot().setTag(position);
    holder
        .binding
        .getRoot()
        .post(
            () -> {
              holder.binding.getRoot().setTranslationX(-holder.binding.getRoot().getWidth());
              holder.binding.getRoot().setAlpha(0);
              holder
                  .binding
                  .getRoot()
                  .animate()
                  .alpha(1)
                  .translationX(0)
                  .setStartDelay(position * 50)
                  .setDuration(500)
                  .start();
            });
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    WidgetsListBinding binding;
    TextView widgetName;

    public ViewHolder(@NonNull WidgetsListBinding binding) {
      super(binding.getRoot());
      this.binding = binding;

      widgetName = binding.name;
    }
  }

  public interface WidgetListClickListener {
    boolean onWidgetLongClicked(View view, HashMap<String, Object> widgetItem);
  }
}
