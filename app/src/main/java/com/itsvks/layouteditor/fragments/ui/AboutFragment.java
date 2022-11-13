package com.itsvks.layouteditor.fragments.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.itsvks.layouteditor.databinding.FragmentAboutBinding;
import com.itsvks.layouteditor.fragments.ui.AboutViewModel;

public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AboutViewModel aboutViewModel = new ViewModelProvider(this).get(AboutViewModel.class);
        
        final TextView textView = binding.textAbout;
        aboutViewModel.getMText().observe(getViewLifecycleOwner(), textView::setText);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
