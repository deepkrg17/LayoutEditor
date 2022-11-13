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

    private TextinputlayoutBinding binding;

    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;

    boolean isDrawable;

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

                        @Override
                        public void beforeTextChanged(
                                CharSequence arg0, int arg1, int arg2, int arg3) {}

                        @Override
                        public void onTextChanged(
                                CharSequence arg0, int arg1, int arg2, int arg3) {}

                        @Override
                        public void afterTextChanged(Editable arg0) {
                            checkErrors();
                        }
                    });
        }

        setView(textInputLayout, 10);
        showKeyboardWhenOpen();
    }

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

        if (!DrawableManager.contains(textInputEditText.getText().toString())) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("No Drawable found");
            setEnabled(false);
            return;
        }

        textInputLayout.setErrorEnabled(false);
        textInputLayout.setError("");
        setEnabled(true);
    }

    @Override
    public void show() {
        super.show();
        requestEditText(textInputEditText);
        if (isDrawable) checkErrors();
    }

    @Override
    protected void onClickSave() {
        super.onClickSave();
        listener.onSave(
                isDrawable
                        ? "@drawable/" + textInputEditText.getText().toString()
                        : textInputEditText.getText().toString());
    }
}
