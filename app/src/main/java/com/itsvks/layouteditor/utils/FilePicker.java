package com.itsvks.layouteditor.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public abstract class FilePicker {
  private ActivityResultLauncher<String> getFile;
  private ActivityResultLauncher<String> reqPermission;
  private AppCompatActivity actvty;

  public FilePicker(AppCompatActivity actvty) {
    this.actvty = actvty;

    this.getFile =
        actvty.registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
              if (uri != null) onPickFile(uri);
            });
    this.reqPermission =
        actvty.registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), this::onRequestPermission);
  }

  public abstract void onRequestPermission(boolean isGranted);

  public abstract void onPickFile(@NonNull Uri uri);

  public void launch(String type) {
    if (actvty.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_DENIED) {
      reqPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
      return;
    }

    getFile.launch(type);
  }
}
