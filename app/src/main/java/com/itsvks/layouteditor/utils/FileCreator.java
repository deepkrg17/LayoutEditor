package com.itsvks.layouteditor.utils;

import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * FileCreator Class is used to create a file with given name and MIME Type.
 *
 * @param actvty Instance of AppCompatActivity
 */
public abstract class FileCreator {

  /** To create a file */
  private ActivityResultLauncher<String> createFile;
  /** Instance of AppCompatActivity */
  private AppCompatActivity actvty;
  /** MIME Type of file */
  private String mimeType;

  /**
   * Constructor of class
   *
   * @param actvty Instance of AppCompatActivity
   */
  public FileCreator(AppCompatActivity actvty) {
    this.actvty = actvty; // Set activity
    this.mimeType = "*/*"; // Set MIME type
    // Register activity result for CreateDocument
    this.createFile =
        actvty.registerForActivityResult(
            new ActivityResultContracts.CreateDocument(mimeType), this::onCreateFile);
  }

  /**
   * Abstract method onCreateFile to call on result
   *
   * @param uri
   */
  public abstract void onCreateFile(@Nullable Uri uri);

  /**
   * Method to create file
   *
   * @param fileName The name of the file
   * @param mimeType The MIME type of the file
   */
  public void create(@NonNull String fileName, String mimeType) {
    this.mimeType = mimeType; // Set MIME type
    createFile.launch(fileName); // Launch file
  }
}
