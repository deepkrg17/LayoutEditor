package com.itsvks.layouteditor.interfaces;

import android.widget.ImageView;
import androidx.appcompat.app.ActionBar;

public interface PreviewDrawableListener {
  void showInImage(ImageView imageView);
  void setSubtitle(ActionBar actionBar);
}
