package com.itsvks.layouteditor.utils;

import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class FileCreator {
    private ActivityResultLauncher<String> createFile;
    private AppCompatActivity actvty;

    public FileCreator(AppCompatActivity actvty) {
        this.actvty = actvty;

        this.createFile =
                actvty.registerForActivityResult(
                        new ActivityResultContracts.CreateDocument("text/xml"), this::onCreateFile);
    }

    public abstract void onCreateFile(@Nullable Uri uri);

    public void createXML(@NonNull String fileName) {
        createFile.launch(fileName);
    }
}
