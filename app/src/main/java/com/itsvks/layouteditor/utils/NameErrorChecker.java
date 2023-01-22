package com.itsvks.layouteditor.utils;

import androidx.appcompat.app.AlertDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.adapters.models.DrawableFile;
import com.itsvks.layouteditor.R;
import java.util.List;
import java.util.regex.Pattern;

public class NameErrorChecker {
  public static void checkForDrawable(
      String name,
      TextInputLayout inputLayout,
      AlertDialog dialog,
      List<DrawableFile> drawableList) {
    if (!name.isEmpty()) {
      if (Character.isDigit(name.charAt(0))) {
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(dialog.getContext().getString(R.string.msg_first_letter_not_number));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        return;
      }
      if (name.contains(" ")) {
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(dialog.getContext().getString(R.string.msg_space_not_allowed));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        return;
      }
      if (!Pattern.matches("[a-z][a-z0-9_]*", name)) {
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(dialog.getContext().getString(R.string.msg_only_letters_and_numbers));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        return;
      }
    } else {
      inputLayout.setErrorEnabled(true);
      inputLayout.setError(dialog.getContext().getString(R.string.msg_cannnot_empty));
      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
      return;
    }

    for (DrawableFile item : drawableList) {
      if (item.name.equals(name)) {
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(dialog.getContext().getString(R.string.msg_current_name_unavailable));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        return;
      }
    }

    inputLayout.setErrorEnabled(false);
    inputLayout.setError("");
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
  }
}
