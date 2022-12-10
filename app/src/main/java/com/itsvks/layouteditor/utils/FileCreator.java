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
  private String mimeType;

  public FileCreator(AppCompatActivity actvty) {
    this.actvty = actvty;
    this.mimeType = "*/*";

    this.createFile =
        actvty.registerForActivityResult(
            new ActivityResultContracts.CreateDocument(mimeType), this::onCreateFile);
  }

  public abstract void onCreateFile(@Nullable Uri uri);

  public void create(@NonNull String fileName, String mimeType) {
    this.mimeType = mimeType;
    createFile.launch(fileName);
  }
}
