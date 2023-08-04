package com.itsvks.editor.utils;

import android.content.Context;
import android.widget.Button;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.editor.app.LayoutEditor;
import com.itsvks.editor.models.Project;
import com.itsvks.editor.R;
import java.util.List;

public class NameErrorChecker {
  private static Context context = LayoutEditor.getInstance().getApplicationContext();

  public static void checkForCreateOrRenameProject(
      String name,
      String currentName,
      List<Project> projects,
      TextInputLayout textField,
      Button createButton) {
    if (name.equals("")) {
      textField.setErrorEnabled(true);
      textField.setError(context.getString(R.string.msg_cannnot_empty));
      createButton.setEnabled(false);
      return;
    }

    for (Project file : projects) {
      if (name.equals(currentName)) break;

      if (file.getName().equals(name)) {
        textField.setErrorEnabled(true);
        textField.setError(context.getString(R.string.msg_current_name_unavailable));
        createButton.setEnabled(false);
        return;
      }
    }

    textField.setErrorEnabled(false);
    textField.setError("");
    createButton.setEnabled(true);
  }
}
