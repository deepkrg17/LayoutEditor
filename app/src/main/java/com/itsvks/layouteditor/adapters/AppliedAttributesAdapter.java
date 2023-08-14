package com.itsvks.layouteditor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.itsvks.layouteditor.databinding.ShowAttributeItemBinding;
import com.itsvks.layouteditor.interfaces.AppliedAttributeClickListener;
import com.itsvks.layouteditor.utils.Constants;
import java.util.HashMap;
import java.util.List;

public class AppliedAttributesAdapter extends RecyclerView.Adapter<AppliedAttributesAdapter.VH> {

  private List<HashMap<String, Object>> attrs;
  private List<String> values;
  private AppliedAttributeClickListener clickListener;

  public AppliedAttributesAdapter(
      List<HashMap<String, Object>> attrs,
      List<String> values,
      AppliedAttributeClickListener listener) {
    this.attrs = attrs;
    this.values = values;
    this.clickListener = listener;
  }

  public class VH extends RecyclerView.ViewHolder {
    public ShowAttributeItemBinding binding;
    public ImageView btnRemove;
    public TextView attributeName;
    public TextView attributeValue;

    public VH(ShowAttributeItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;

      btnRemove = binding.btnRemoveAttribute;
      attributeName = binding.attributeName;
      attributeValue = binding.attributeValue;
    }
  }

  @Override
  public VH onCreateViewHolder(ViewGroup parent, int viewType) {
    return new VH(
        ShowAttributeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    holder.attributeName.setText(attrs.get(position).get("name").toString());
    holder.attributeValue.setText(values.get(position));
    
    TooltipCompat.setTooltipText(holder.btnRemove, "Remove");
    TooltipCompat.setTooltipText(holder.binding.getRoot(), attrs.get(position).get("name").toString());

    if (attrs.get(position).containsKey(Constants.KEY_CAN_DELETE))
      holder.btnRemove.setVisibility(View.GONE);
    holder.binding.getRoot().setOnClickListener(v -> clickListener.onClick(position));
    holder.btnRemove.setOnClickListener(v -> clickListener.onRemoveButtonClick(position));
  }

  @Override
  public int getItemCount() {
    return attrs.size();
  }
}
