package com.itsvks.layouteditor.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.GetContent;
import androidx.appcompat.app.AppCompatActivity;

public abstract class FilePicker {
    private ActivityResultLauncher<String> getFile;
    private AppCompatActivity actvty;

    public FilePicker(AppCompatActivity actvty) {
        this.actvty = actvty;

        this.getFile =
                actvty.registerForActivityResult(
                        new GetContent(),
                        new ActivityResultCallback<Uri>() {

                            @Override
                            public void onActivityResult(Uri uri) {
                                if (uri != null) {
                                    onResult(FileUtil.convertUriToFilePath(uri));
                                }
                            }
                        });
    }

    public abstract void onResult(String path);

    public void launch(String type) {
        if (actvty.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED
                || actvty.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
            actvty.requestPermissions(
                    new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    10);
            return;
        }

        getFile.launch(type);
    }
}
