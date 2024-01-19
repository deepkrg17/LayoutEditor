package com.itsvks.layouteditor.fragments.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.itsvks.layouteditor.BuildConfig;
import com.itsvks.layouteditor.R.string;
import com.itsvks.layouteditor.databinding.FragmentAboutBinding;
import com.itsvks.layouteditor.utils.Constants;

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

        binding.details.setText(getString(string.share_description, Constants.GITHUB_URL));
        binding.version.setText("v" + BuildConfig.VERSION_NAME);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
