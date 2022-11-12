package com.itsvks.layouteditor.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.itsvks.layouteditor.R.string;

public abstract class FilePicker {
    private ActivityResultLauncher<String> getFile;
    private ActivityResultLauncher<String> reqPermission;
    private AppCompatActivity actvty;
    private View rootView;

    public FilePicker(AppCompatActivity actvty) {
        this.actvty = actvty;
        this.rootView = actvty.getWindow().getDecorView().getRootView();

        this.getFile =
                actvty.registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {
                            if (uri != null) {
                                onResult(FileUtil.convertUriToFilePath(uri));
                            }
                        });
        this.reqPermission =
                actvty.registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted)
                                SBUtils.make(rootView, string.permission_granted)
                                        .setSlideAnimation()
                                        .showAsSuccess();
                            else
                                SBUtils.make(rootView, string.permission_denied)
                                        .setSlideAnimation()
                                        .showAsError();
                        });
    }

    public abstract void onResult(String path);

    public void launch(String type) {
        if (actvty.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            reqPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            return;
        }

        getFile.launch(type);
    }
}
