package com.itsvks.layouteditor.editor.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;

public class NumberDialog extends AttributeDialog {

  private TextinputlayoutBinding binding;

  private TextInputLayout textInputLayout;
  private TextInputEditText textInputEditText;

  /**
   * Constructor for creating a NumberDialog
   *
   * @param context the context of the activity
   * @param savedValue the saved value
   * @param type the type of number input
   */
  public NumberDialog(Context context, String savedValue, String type) {
    super(context);

    binding = TextinputlayoutBinding.inflate(getDialog().getLayoutInflater());

    textInputLayout = binding.getRoot();
    textInputLayout.setHint("Enter " + type + " value");

    textInputEditText = binding.textinputEdittext;

    if (type.equals("float")) {
      // Set input type to signed float
      textInputEditText.setInputType(
          InputType.TYPE_CLASS_NUMBER
              | InputType.TYPE_NUMBER_FLAG_SIGNED
              | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    } else {
      // Set input type to signed integer
      textInputEditText.setInputType(
          InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    // If no saved value, set to 0
    textInputEditText.setText(savedValue.equals("") ? "0" : savedValue);
    textInputEditText.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

          @Override
          public void onTextChanged(CharSequence text, int p2, int p3, int p4) {}

          // Check for error on text change
          @Override
          public void afterTextChanged(Editable p1) {
            checkError();
          }
        });

    // Set padding of the view
    setView(textInputLayout, 10);
    showKeyboardWhenOpen();
  }

  /** Show the dialog, and request focus in edit text */
  @Override
  public void show() {
    super.show();
    requestEditText(textInputEditText);
  }

  /** On clicking save, invoke listener's onSave method */
  @Override
  protected void onClickSave() {
    listener.onSave(textInputEditText.getText().toString());
  }

  /** Check for error. Set enabled to false if empty and set error message */
  private void checkError() {
    String text = textInputEditText.getText().toString();

    if (text.equals("")) {
      setEnabled(false);
      textInputLayout.setErrorEnabled(true);
      textInputLayout.setError("Field cannot be empty!");
    } else {
      setEnabled(true);
      textInputLayout.setErrorEnabled(false);
      textInputLayout.setError("");
    }
  }
}
