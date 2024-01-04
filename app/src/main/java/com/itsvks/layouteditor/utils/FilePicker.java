package com.itsvks.layouteditor.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.itsvks.layouteditor.LayoutEditor;
import com.itsvks.layouteditor.R;

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
            new ActivityResultContracts.GetContent(), this::onPickFile);

    // Create an instance of ActivityResultContracts.RequestPermission and register it with actvty
    // when the result is returned, call the onRequestPermission method with the granted boolean
    this.reqPermission =
        actvty.registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), this::onRequestPermission);
  }

  public void onRequestPermission(boolean isGranted) {
    if (isGranted)
      SBUtils.make(actvty.findViewById(android.R.id.content), R.string.permission_granted)
          .setSlideAnimation()
          .showAsSuccess();
    else
      SBUtils.make(actvty.findViewById(android.R.id.content), R.string.permission_denied)
          .setSlideAnimation()
          .showAsError();
  }

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
  public void launch(String mimeType) {
    checkPermissions(mimeType);
    // If the app has the permission, launch the getFile instance
    getFile.launch(mimeType);
  }
  
  private void checkPermissions(String mimeType) {
    boolean isImageType =
        mimeType.equals("image/*")
            || mimeType.equals("image/png")
            || mimeType.equals("image/jpg")
            || mimeType.equals("image/jpeg");

    if (isImageType) {
      if (LayoutEditor.Companion.getInstance().isAtLeastTiramisu()) {
        if (actvty.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
            == PackageManager.PERMISSION_DENIED) {
          reqPermission.launch(Manifest.permission.READ_MEDIA_IMAGES);
          return;
        }
      } else if (actvty.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
          == PackageManager.PERMISSION_DENIED) {
        reqPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        return;
      }
    }
  }
}
