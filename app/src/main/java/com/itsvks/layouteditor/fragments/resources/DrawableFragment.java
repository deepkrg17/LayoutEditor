package com.itsvks.layouteditor.fragments.resources;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ToastUtils;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.activities.ResourceManagerActivity;
import com.itsvks.layouteditor.adapters.DrawableResourceAdapter;
import com.itsvks.layouteditor.adapters.models.DrawableFile;
import com.itsvks.layouteditor.databinding.FragmentResourcesBinding;
import com.itsvks.layouteditor.utils.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DrawableFragment extends Fragment {

  private FragmentResourcesBinding binding;
  private DrawableResourceAdapter adapter;
  private ProjectFile project;
  private RecyclerView mRecyclerView;
  List<DrawableFile> drawableList = new ArrayList<>();

  public DrawableFragment(ProjectFile project, List<DrawableFile> drawableList) {
    this.drawableList = drawableList;
    this.project = project;
  }
  
  public DrawableFragment() {
    
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentResourcesBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    loadDrawables();
    mRecyclerView = binding.recyclerView;
    // Create the adapter and set it to the RecyclerView
    adapter = new DrawableResourceAdapter(drawableList, project);
    mRecyclerView.setAdapter(adapter);
    mRecyclerView.setLayoutManager(
        new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
  }

  public void addDrawable(Uri imageUri) {
    String drawablePath = project.getDrawablePath();

    String toPath =
        drawablePath + FileUtil.getLastSegmentFromPath(FileUtil.convertUriToFilePath(imageUri));
    FileUtil.copyFile(FileUtil.convertUriToFilePath(imageUri), toPath);
    // Create a list of data to add to the RecyclerView
    drawableList.add(
        new DrawableFile(
            FileUtil.getLastSegmentFromPath(FileUtil.convertUriToFilePath(imageUri)),
            Drawable.createFromPath(FileUtil.convertUriToFilePath(imageUri)),
            FileUtil.convertUriToFilePath(imageUri)));
    adapter.notifyDataSetChanged();
  }

  public void loadDrawables() {
    File[] files = project.getDrawables();

    if (files == null) {
      ToastUtils.showLong("Null");
    } else {

      for (File file : files) {
        Drawable drawable = Drawable.createFromPath(file.getPath());
        String name = file.getName();
        // name = name.substring(0, name.lastIndexOf("."));
        drawableList.add(new DrawableFile(name, drawable, file.getPath()));
      }
    }
  }
}
