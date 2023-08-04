package com.itsvks.editor.adapters;

import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.SizeUtils;
import com.itsvks.editor.databinding.LayoutProjectItemBinding;
import com.itsvks.editor.models.Project;
import com.itsvks.editor.R;
import com.itsvks.editor.utils.ColorUtils;
import com.itsvks.editor.utils.TintingUtils;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.VH> {

  private List<Project> projects;
  private OnItemOptionsClickListener onItemOptionsClickListener;

  public ProjectAdapter(List<Project> projects) {
    this.projects = projects;
  }

  public class VH extends RecyclerView.ViewHolder {
    public TextView projectName, projectCreateTime;
    public ImageView projectIcon, projectOptions;
    public LayoutProjectItemBinding binding;

    public VH(LayoutProjectItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;

      projectName = binding.projectName;
      projectCreateTime = binding.projectCreateTime;
      projectIcon = binding.projectIcon;
      projectOptions = binding.projectOptions;
    }
  }

  @Override
  public VH onCreateViewHolder(ViewGroup parent, int viewType) {
    return new VH(
        LayoutProjectItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    var root = holder.binding.getRoot();

    holder.projectName.setText(projects.get(position).getName());
    holder.projectCreateTime.setText(projects.get(position).getTime());
    // holder.projectIcon.setImageDrawable(projects.get(position).getIcon());

    root.setOnClickListener(
        v -> {
          if (onItemOptionsClickListener != null) {
            onItemOptionsClickListener.onOpenProjectClick(position);
          }
        });
    createContextMenu(root, position);
    holder.projectOptions.setOnClickListener(v -> showOptions(v, position));
  }

  @Override
  public int getItemCount() {
    return projects.size();
  }

  private void showOptions(View v, int position) {
    final PopupMenu popup = new PopupMenu(v.getContext(), v);
    popup.inflate(R.menu.menu_project_item);
    if (popup.getMenu() instanceof MenuBuilder) {
      MenuBuilder menuBuilder = (MenuBuilder) popup.getMenu();
      menuBuilder.setOptionalIconsVisible(true);

      for (MenuItem item : menuBuilder.getVisibleItems()) {
        if (item.getIcon() != null) {
          var iconMarginPx = SizeUtils.px2dp(8);
          TintingUtils.tintMenuIcon(item, ColorUtils.getOnPrimaryContainerColor(v.getContext()));
          if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            item.setIcon(new InsetDrawable(item.getIcon(), iconMarginPx, 0, iconMarginPx, 0));
          } else {
            item.setIcon(
                new InsetDrawable(item.getIcon(), iconMarginPx, 0, iconMarginPx, 0) {
                  @Override
                  public int getIntrinsicWidth() {
                    return getIntrinsicHeight() + iconMarginPx + iconMarginPx;
                  }
                });
          }
        }
      }
    }
    popup.setOnMenuItemClickListener(
        item -> {
          if (onItemOptionsClickListener != null) {
            var id = item.getItemId();
            if (id == R.id.menu_edit) {
              onItemOptionsClickListener.onEditProjectClick(position);
            } else if (id == R.id.menu_preview) {
              onItemOptionsClickListener.onPreviewLayoutClick(position);
            } else if (id == R.id.menu_delete) {
              onItemOptionsClickListener.onDeleteProjectClick(position);
            } else if (id == R.id.menu_open) {
              onItemOptionsClickListener.onOpenProjectClick(position);
            }
            return true;
          }
          return false;
        });

    popup.show();
  }

  public void createContextMenu(View v, int position) {
    v.setOnCreateContextMenuListener(
        (menu, view, menuInfo) -> {
          createMenuItem(
              menu, R.string.open, () -> onItemOptionsClickListener.onOpenProjectClick(position));
          createMenuItem(
              menu, R.string.edit, () -> onItemOptionsClickListener.onEditProjectClick(position));
          createMenuItem(
              menu,
              R.string.preview,
              () -> onItemOptionsClickListener.onPreviewLayoutClick(position));
          createMenuItem(
              menu,
              R.string.delete,
              () -> onItemOptionsClickListener.onDeleteProjectClick(position));
        });
  }

  private void createMenuItem(Menu menu, int stringResId, Runnable clickListener) {
    menu.add(0, stringResId, menu.size(), stringResId)
        .setOnMenuItemClickListener(
            item -> {
              if (onItemOptionsClickListener != null) {
                clickListener.run();
              }
              return true;
            });
  }

  public interface OnItemOptionsClickListener {
    void onDeleteProjectClick(int position);

    void onEditProjectClick(int position);

    void onPreviewLayoutClick(int position);

    void onOpenProjectClick(int position);
  }

  public void setOnItemOptionsClickListener(OnItemOptionsClickListener onItemOptionsClickListener) {
    this.onItemOptionsClickListener = onItemOptionsClickListener;
  }
}
