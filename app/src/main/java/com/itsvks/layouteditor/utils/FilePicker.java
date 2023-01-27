package com.itsvks.layouteditor.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Class for FilePicker
 *
 * @param actvty AppCompatActivity instance
 */
public abstract class FilePicker {
  private ActivityResultLauncher<String> getFile;
  private ActivityResultLauncher<String> reqPermission;
  private AppCompatActivity actvty;

  /**
   * Constructor for FilePicker, takes in an AppCompatActivity as a parameter
   *
   * @param actvty AppCompatActivity instance
   */
  public FilePicker(AppCompatActivity actvty) {
    this.actvty = actvty;

    // Create an instance of ActivityResultContracts.GetContent and register it with actvty
    // when the result is returned, call the onPickFile method with the returned uri
    this.getFile =
        actvty.registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
              onPickFile(uri);
            });

    // Create an instance of ActivityResultContracts.RequestPermission and register it with actvty
    // when the result is returned, call the onRequestPermission method with the granted boolean
    this.reqPermission =
        actvty.registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), this::onRequestPermission);
  }

  /**
   * Abstract method called onRequestPermission, takes in a boolean as a parameter
   *
   * @param isGranted boolean
   */
  public abstract void onRequestPermission(boolean isGranted);

  /**
   * Abstract method called onPickFile, takes in a Nullable Uri as a parameter
   *
   * @param uri Nullable Uri
   */
  public abstract void onPickFile(@Nullable Uri uri);

  /**
   * Method launch, takes in a String MIME type as a parameter
   *
   * @param type String
   */
  public void launch(String type) {

    // Check if the app has the READ_EXTERNAL_STORAGE permission, if not launch the reqPermission
    if (actvty.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_DENIED) {
      reqPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
      return;
    }

    // If the app has the permission, launch the getFile instance
    getFile.launch(type);
  }
}
