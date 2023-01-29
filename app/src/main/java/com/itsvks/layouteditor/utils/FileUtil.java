package com.itsvks.layouteditor.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.itsvks.layouteditor.LayoutEditor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;

public class FileUtil {
  /**
   * Reads from an asset file and returns its content as a String.
   *
   * @param path  The path to the asset file
   * @param ctx  The context from which the asset should be read
   * @return  The content of the asset file as a String
   */
  public static String readFromAsset(String path, Context ctx) {
    try {
      // Get the input stream from the asset
      InputStream inputStream = ctx.getAssets().open(path);

      // Create a byte array output stream to store the read bytes
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      // Create a buffer of 1024 bytes
      byte[] _buf = new byte[1024];
      int i;

      // Read the bytes from the input stream, write them to the output stream and close the streams
      while ((i = inputStream.read(_buf)) != -1) {
        outputStream.write(_buf, 0, i);
      }
      outputStream.close();
      inputStream.close();

      // Return the content of the output stream as a String
      return outputStream.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // If an exception occurred, return an empty String
    return "";
  }

  /**
   * This method is used to copy file from assets folder to target path.
   *
   * @param filename - File name with extension to copy from assets folder.
   * @param outPath - Target path where you want to copy the file.
   */
  public static void copyFileFromAsset(String filename, String outPath) {
    // Get asset manager instance from application context
    AssetManager assetManager = LayoutEditor.getContext().getAssets();

    // Create streams for read and write
    InputStream in;
    OutputStream out;

    try {
      // Create InputStream from assets folder
      in = assetManager.open(filename);
      // Create OutputStream to target path
      String newFileName = outPath + "/" + filename;
      out = new FileOutputStream(newFileName);

      // Buffer for read and write
      byte[] buffer = new byte[1024];
      int read;

      // Read from InputStream and write to OutputStream
      while ((read = in.read(buffer)) != -1) {
        out.write(buffer, 0, read);
      }

      // Close input and output streams
      in.close();
      out.flush();
      out.close();
    } catch (IOException e) {
      // Print exception stack trace
      e.printStackTrace();
    }
  }

  /**
   * Creates a new file in the specified directory path if it does not already exist
   *
   * @param path  the directory path in which to create the new file
   */
  private static void createNewFile(String path) {
    // Get the last index of the file separator
    int lastSep = path.lastIndexOf(File.separator);
    // If there is a path, call makeDir to create the directory
    if (lastSep > 0) {
      String dirPath = path.substring(0, lastSep);
      makeDir(dirPath);
    }

    // Create a new file in the specified path
    File file = new File(path);

    try {
      // Only create the file if it does not already exist
      if (!file.exists()) file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Reads the contents of a file in the specified path
   *
   * @param path  the directory path of the file to read
   * @return  the contents of the file as a string
   */
  public static String readFile(String path) {
    // Create the file if it does not exist
    createNewFile(path);

    StringBuilder sb = new StringBuilder();
    FileReader fr = null;
    try {
      fr = new FileReader(new File(path));

      char[] buff = new char[1024];
      int length = 0;

      // Read the contents of the file and append them to the StringBuilder
      while ((length = fr.read(buff)) > 0) {
        sb.append(new String(buff, 0, length));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (fr != null) {
        try {
          fr.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    // Return the contents of the file
    return sb.toString();
  }

  /**
   * Method to write a file with the given path and string.
   *
   * @param path  Path of the file to write.
   * @param str  String to write in the file.
   */
  public static void writeFile(String path, String str) {
    // Create a new file.
    createNewFile(path);
    FileWriter fileWriter = null;

    try {
      // Create a filewriter object with given path
      // and false to overwrite the existing file.
      fileWriter = new FileWriter(new File(path), false);
      // Write the given string in file.
      fileWriter.write(str);
      // Flush the filewriter object.
      fileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        // Close the filewriter object.
        if (fileWriter != null) fileWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Method to copy a file from source to destination.
   *
   * @param sourcePath  Path of the file to copy from.
   * @param destPath  Path of the file to copy to.
   */
  public static void copyFile(String sourcePath, String destPath) {
    // Check if file exist in source path.
    if (!isExistFile(sourcePath)) return;
    // Create a new file in destination path.
    createNewFile(destPath);

    FileInputStream fis = null;
    FileOutputStream fos = null;

    try {
      // Create input and output stream objects.
      fis = new FileInputStream(sourcePath);
      fos = new FileOutputStream(destPath, false);

      byte[] buff = new byte[1024];
      int length = 0;

      // Read and write the bytes from source path to destination path.
      while ((length = fis.read(buff)) > 0) {
        fos.write(buff, 0, length);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      // Close the input and output stream objects.
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void copyFile(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int read;
    while ((read = in.read(buffer)) != -1) {
      out.write(buffer, 0, read);
    }
  }

  /**
   * copyDir() Copies a directory from one path to another
   *
   * @param oldPath the path of the directory to be copied
   * @param newPath the path of the directory to be created
   */
  public static void copyDir(String oldPath, String newPath) {
    File oldFile = new File(oldPath);
    File[] files = oldFile.listFiles();
    File newFile = new File(newPath);
    if (!newFile.exists()) {
      newFile.mkdirs();
    }
    for (File file : files) {
      if (file.isFile()) {
        copyFile(file.getPath(), newPath + "/" + file.getName());
      } else if (file.isDirectory()) {
        copyDir(file.getPath(), newPath + "/" + file.getName());
      }
    }
  }

  /**
   * moveFile() Moves a file from one path to another
   *
   * @param sourcePath  the path of the file to be moved
   * @param destPath  the path of the destination
   */
  public static void moveFile(String sourcePath, String destPath) {
    copyFile(sourcePath, destPath);
    deleteFile(sourcePath);
  }

  /**
   * deleteFile() Deletes a file with the given path
   *
   * @param path  the path of the file to be deleted
   */
  public static void deleteFile(String path) {
    File file = new File(path);

    if (!file.exists()) return;

    if (file.isFile()) {
      file.delete();
      return;
    }

    File[] fileArr = file.listFiles();

    if (fileArr != null) {
      for (File subFile : fileArr) {
        if (subFile.isDirectory()) {
          deleteFile(subFile.getAbsolutePath());
        }

        if (subFile.isFile()) {
          subFile.delete();
        }
      }
    }

    file.delete();
  }

  /**
   * Checks if a file exists at the given path.
   *
   * @param path  the path to check
   * @return  true if the file exists, false otherwise
   */
  public static boolean isExistFile(String path) {
    File file = new File(path);
    return file.exists();
  }

  /**
   * Creates a directory at the given path, if it doesn't exist.
   *
   * @param path  the path at which the directory should be created
   */
  public static void makeDir(String path) {
    if (!isExistFile(path)) {
      File file = new File(path);
      file.mkdirs();
    }
  }

  /**
   * Lists all the files in the given directory, and adds them to the given list.
   *
   * @param path  the directory to list the files from
   * @param list  the list to which the files should be added
   */
  public static void listDir(String path, ArrayList<String> list) {
    File dir = new File(path);
    if (!dir.exists() || dir.isFile()) return;

    File[] listFiles = dir.listFiles();
    if (listFiles == null || listFiles.length <= 0) return;

    if (list == null) return;
    list.clear();
    for (File file : listFiles) {
      list.add(file.getAbsolutePath());
    }
  }

  /**
   * This method is used to convert the Uri to File Path.
   *
   * @param uri  Uri of the file
   * @return  Returns the File Path of Uri
   */
  public static String convertUriToFilePath(final Uri uri) {
    String path = null;
    // Check if the Uri is provided by documents contract
    if (DocumentsContract.isDocumentUri(LayoutEditor.getContext(), uri)) {
      // Check if Uri is External Storage Document
      if (isExternalStorageDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        // Split the document Id into two parts
        final String[] split = docId.split(":");
        final String type = split[0];

        // Check if it is primary storage
        if ("primary".equalsIgnoreCase(type)) {
          // Append the split[1] to Environment.getExternalStorageDirectory() to get File Path
          path = Environment.getExternalStorageDirectory() + "/" + split[1];
        }
      } else if (isDownloadsDocument(uri)) {
        final String id = DocumentsContract.getDocumentId(uri);

        // Check if the Id is empty
        if (!TextUtils.isEmpty(id)) {
          // Replace 'raw:' from Id to get File Path
          if (id.startsWith("raw:")) {
            return id.replaceFirst("raw:", "");
          }
        }

      } else if (isMediaDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];

        Uri contentUri = null;
        // Check the type of Media
        if ("image".equals(type)) {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        final String selection = "_id=?";
        final String[] selectionArgs = new String[] {split[1]};

        // Get Data Column from content Uri
        path = getDataColumn(contentUri, selection, selectionArgs);
      }
    } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
      // Get Data Column from content Uri
      path = getDataColumn(uri, null, null);
    } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
      // Get File Path from Uri
      path = uri.getPath();
    }

    // Check if path is not empty
    if (path != null) {
      try {
        // Decode the File Path using 'UTF-8'
        return URLDecoder.decode(path, "UTF-8");
      } catch (Exception e) {
        return null;
      }
    }
    return null;
  }

  /**
   * This method is used to get the data column of a particular URI.
   *
   * @param uri  The URI of the data column to be retrieved.
   * @param selection  The selection argument for the query.
   * @param selectionArgs  The selection arguments for the query.
   * @return  The data column retrieved from the specified URI.
   */
  @SuppressLint("Recycle")
  private static String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
    final String column =
        MediaStore.Images.Media.DATA; // Column name of the data column to be retrieved
    final String[] projection = {column}; // Projection of the data column to be retrieved

    try {
      Cursor cursor =
          LayoutEditor.getContext() // Get the application context
              .getContentResolver() // Get the content resolver
              .query(uri, projection, selection, selectionArgs, null); // Query the content resolver
      if (cursor != null && cursor.moveToFirst()) {
        final int column_index =
            cursor.getColumnIndexOrThrow(column); // Get the index of the data column
        return cursor.getString(column_index); // Return the data column
      }
    } catch (Exception e) {

    }
    return null;
  }

  /**
   * Checks whether the Uri authority is ExternalStorageProvider.
   *
   * @param uri  The Uri to check.
   * @return  Whether the Uri authority is ExternalStorageProvider.
   */
  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  /**
   * Checks whether the Uri authority is DownloadsProvider.
   *
   * @param uri  The Uri to check.
   * @return  Whether the Uri authority is DownloadsProvider.
   */
  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  /**
   * Checks whether the Uri authority is MediaProvider.
   *
   * @param uri  The Uri to check.
   * @return  Whether the Uri authority is MediaProvider.
   */
  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  /**
   * Checks whether the file is a directory.
   *
   * @param path  The path of the file.
   * @return  Whether the file is a directory.
   */
  public static boolean isDirectory(String path) {
    if (!isExistFile(path)) return false;
    return new File(path).isDirectory();
  }

  /**
   * Checks whether the file is a file.
   *
   * @param path  The path of the file.
   * @return  Whether the file is a file.
   */
  public static boolean isFile(String path) {
    if (!isExistFile(path)) return false;
    return new File(path).isFile();
  }

  /**
   * Gets the file length.
   *
   * @param path  The path of the file.
   * @return  The file length.
   */
  public static long getFileLength(String path) {
    if (!isExistFile(path)) return 0;
    return new File(path).length();
  }

  /**
   * Gets the absolute path of the external storage directory.
   *
   * @return  The absolute path of the external storage directory.
   */
  public static String getExternalStorageDir() {
    return Environment.getExternalStorageDirectory().getAbsolutePath();
  }

  /**
   * Gets the absolute path of the application-specific directory.
   *
   * @param ctx  The application context.
   * @return  The absolute path of the application-specific directory.
   */
  public static String getPackageDataDir(Context ctx) {
    return ctx.getExternalFilesDir("").getAbsolutePath();
  }

  /**
   * Gets the absolute path of the public directory.
   *
   * @param type  The type of the public directory.
   * @return  The absolute path of the public directory.
   */
  public static String getPublicDir(String type) {
    return Environment.getExternalStoragePublicDirectory(type).getAbsolutePath();
  }

  /**
   * Gets the last segment from the path.
   *
   * @param path  The path to get the last segment.
   * @return  The last segment from the path.
   */
  public static String getLastSegmentFromPath(String path) {
    if (path == null) return "";
    return path.substring(path.lastIndexOf("/") + 1, path.length());
  }

  /**
   * Saves the file with given Uri and data.
   *
   * @param uri  URI of the file to be saved
   * @param data  Data to be written in the file
   * @return boolean  True if file is saved successfully, else false
   */
  public static boolean saveFile(Uri uri, String data) {
    try {
      // Open the file descriptor in read-write-truncate mode
      ParcelFileDescriptor pfd =
          LayoutEditor.getContext().getContentResolver().openFileDescriptor(uri, "rwt");
      // Initialize file output stream with the file descriptor
      FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
      // Write the data in the file
      fos.write(data.getBytes());
      // Close the output stream
      fos.close();
      // Close the file descriptor
      pfd.close();
      return true;
    } catch (IOException e) {
      // Print the stacktrace for the exception
      e.printStackTrace();
      return false;
    }
  }
}
