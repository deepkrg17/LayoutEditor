package com.itsvks.layouteditor.fragments.resources;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.itsvks.layouteditor.ProjectFile;
import com.itsvks.layouteditor.adapters.ColorResourceAdapter;
import com.itsvks.layouteditor.adapters.models.ColorItem;
import com.itsvks.layouteditor.databinding.FragmentResourcesBinding;
import com.itsvks.layouteditor.tools.ColorResourceParser;
import com.itsvks.layouteditor.utils.SBUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ColorFragment extends Fragment {

  private FragmentResourcesBinding binding;
  private ColorResourceAdapter adapter;
  private RecyclerView mRecyclerView;
  private ProjectFile project;
  private List<ColorItem> colorList = new ArrayList<>();
  ColorResourceParser colorParser;

  public ColorFragment(ProjectFile project, List<ColorItem> colorList) {
    this.project = project;
    this.colorList = colorList;
  }

  @Override
  public android.view.View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentResourcesBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    try {
      loadColorsFromXML(project.getColorsPath());
    } catch (FileNotFoundException e) {
      SBUtils.make(view, "An error occured: " + e.getMessage())
          .setFadeAnimation()
          .setType(SBUtils.Type.INFO)
          .show();
    }
    mRecyclerView = binding.recyclerView;
    adapter = new ColorResourceAdapter(colorList);
    mRecyclerView.setAdapter(adapter);
    mRecyclerView.setLayoutManager(
        new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
  }

  public void loadColorsFromXML(String filePath) throws FileNotFoundException {
    InputStream stream = new FileInputStream(filePath);
    colorParser = new ColorResourceParser(stream);
    colorList = colorParser.getColorList();
  }
}
