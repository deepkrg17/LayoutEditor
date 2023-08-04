package com.itsvks.editor.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {
  private static final String TAG = "FileUtil";

  public static void unzipFromAssets(Context context, String zipFileName, String outputDirectory) {
    try {
      AssetManager assetManager = context.getAssets();
      InputStream inputStream = assetManager.open(zipFileName);
      File outputDir = new File(outputDirectory);

      if (!outputDir.exists()) {
        outputDir.mkdirs();
      }

      ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
      ZipEntry zipEntry;

      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
        String fileName = zipEntry.getName();
        File outputFile = new File(outputDirectory, fileName);

        if (zipEntry.isDirectory()) {
          outputFile.mkdirs();
        } else {
          File parentDir = outputFile.getParentFile();
          if (!parentDir.exists()) {
            parentDir.mkdirs();
          }

          int BUFFER_SIZE = 4096;
          BufferedOutputStream bufferedOutputStream =
              new BufferedOutputStream(new FileOutputStream(outputFile));
          byte[] buffer = new byte[BUFFER_SIZE];
          int count;

          while ((count = zipInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
            bufferedOutputStream.write(buffer, 0, count);
          }

          bufferedOutputStream.flush();
          bufferedOutputStream.close();
        }

        zipInputStream.closeEntry();
      }

      zipInputStream.close();
    } catch (IOException e) {
      Log.e(TAG, "Error while unzipping file from assets: " + e.getMessage());
    }
  }

  /**
   * Gets the last segment from the path.
   *
   * @param path The path to get the last segment.
   * @return The last segment from the path.
   */
  public static String getLastSegmentFromPath(String path) {
    if (path == null) return "";
    return path.substring(path.lastIndexOf("/") + 1, path.length());
  }

  /**
   * Checks whether the Uri authority is DownloadsProvider.
   *
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }
}
