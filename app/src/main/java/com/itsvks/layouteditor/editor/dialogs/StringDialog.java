package com.itsvks.layouteditor.editor.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;
import com.itsvks.layouteditor.managers.DrawableManager;

import java.util.regex.Pattern;

public class StringDialog extends AttributeDialog {

  /** Binding object for textInputLayout layout */
  private TextinputlayoutBinding binding;

  /** TextInputLayout object */
  private TextInputLayout textInputLayout;

  /** TextInputEditText object */
  private TextInputEditText textInputEditText;

  /** Boolean flag to check if the dialog is for drawable */
  boolean isDrawable;

  /**
   * Constructor for StringDialog class
   *
   * @param context     The Activity context
   * @param savedValue  The saved value
   * @param isDrawable  Boolean flag to check for drawable
   */
  public StringDialog(Context context, String savedValue, boolean isDrawable) {
    super(context);
    this.isDrawable = isDrawable;
    binding = TextinputlayoutBinding.inflate(getDialog().getLayoutInflater());

    textInputLayout = binding.getRoot();
    textInputLayout.setHint("Enter string value");

    textInputEditText = binding.textinputEdittext;
    textInputEditText.setText(savedValue);

    if (isDrawable) {
      textInputLayout.setHint("Enter drawable name");
      textInputLayout.setPrefixText("@drawable/");
      textInputEditText.addTextChangedListener(
          new TextWatcher() {

            /**
             * Invoked before text is changed
             *
             * @param arg0 CharSequence
             * @param arg1 int
             * @param arg2 int
             * @param arg3 int
             */
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            /**
             * Invoked when the text is changed
             *
             * @param arg0 CharSequence
             * @param arg1 int
             * @param arg2 int
             * @param arg3 int
             */
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            /**
             * Invoked after text is changed
             *
             * @param arg0 Editable
             */
            @Override
            public void afterTextChanged(Editable arg0) {
              checkErrors();
            }
          });
    }

    setView(textInputLayout, 10);
    showKeyboardWhenOpen();
  }

  /** Method to check for errors */
  private void checkErrors() {
    String text = textInputEditText.getText().toString();

    if (text.equals("")) {
      textInputLayout.setErrorEnabled(true);
      textInputLayout.setError("Field cannot be empty!");
      setEnabled(false);
      return;
    }

    if (!Pattern.matches("[a-z_][a-z0-9_]*", text)) {
      textInputLayout.setErrorEnabled(true);
      textInputLayout.setError("Only small letters(a-z) and numbers!");
      setEnabled(false);
      return;
    }

    if (isDrawable && !DrawableManager.contains(textInputEditText.getText().toString())) {
      textInputLayout.setErrorEnabled(true);
      textInputLayout.setError("No Drawable found");
      setEnabled(false);
      return;
    }

    textInputLayout.setErrorEnabled(false);
    textInputLayout.setError("");
    setEnabled(true);
  }

  /** Method to show the dialog */
  @Override
  public void show() {
    super.show();
    requestEditText(textInputEditText);
    if (isDrawable) checkErrors();
  }

  /** Method to be invoked when the save button is clicked */
  @Override
  protected void onClickSave() {
    super.onClickSave();
    listener.onSave(
        isDrawable
            ? "@drawable/" + textInputEditText.getText().toString()
            : textInputEditText.getText().toString());
  }
}
