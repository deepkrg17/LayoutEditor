package com.itsvks.editor.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileContentReader {

  @FunctionalInterface
  public interface OnFileContentReadListener {
    void onFileContentRead(String fileContents);
  }

  public static void readFileContent(
      final String fileUrl, final OnFileContentReadListener listener) {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<String> future =
        executorService.submit(
            new Callable<String>() {
              @Override
              public String call() throws Exception {
                String fileContents = "";
                try {
                  URL url = new URL(fileUrl);
                  URLConnection connection = url.openConnection();
                  connection.connect();

                  InputStream inputStream = connection.getInputStream();
                  BufferedReader bufferedReader =
                      new BufferedReader(new InputStreamReader(inputStream));
                  String line;
                  while ((line = bufferedReader.readLine()) != null) {
                    fileContents += line;
                  }
                  bufferedReader.close();
                  inputStream.close();

                } catch (IOException e) {
                  e.printStackTrace();
                }
                return fileContents;
              }
            });

    executorService.shutdown();

    try {
      String fileContents = future.get();
      if (listener != null) {
        listener.onFileContentRead(fileContents);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
