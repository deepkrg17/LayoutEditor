package com.itsvks.layouteditor.fragments.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.itsvks.layouteditor.databinding.FragmentAboutBinding;
import com.itsvks.layouteditor.fragments.ui.about.AboutViewModel;

public class AboutFragment extends Fragment {

    FragmentAboutBinding binding;

    @Override
    public android.view.View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AboutViewModel aboutViewModel = new ViewModelProvider(this).get(AboutViewModel.class);

        binding = FragmentAboutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAbout;
        aboutViewModel.getMText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
