package com.itsvks.editor.view;

import android.content.Context;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CustomBottomSheetDialog extends BottomSheetDialog {
  private static CustomBottomSheetDialog currentDialog;

  public CustomBottomSheetDialog(Context context) {
    super(context);
  }

  public static CustomBottomSheetDialog getCurrentDialog() {
    return currentDialog;
  }

  @Override
  public void show() {
    currentDialog = this;
    super.show();
  }

  @Override
  public void dismiss() {
    currentDialog = null;
    super.dismiss();
  }
}
