package com.itsvks.layouteditor.editor.dialogs;

import android.content.Context;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;

public class StringDialog extends AttributeDialog {

    private TextinputlayoutBinding binding;

    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;

    public StringDialog(Context context, String savedValue) {
        super(context);

        binding = TextinputlayoutBinding.inflate(getDialog().getLayoutInflater());

        textInputLayout = binding.getRoot();
        textInputLayout.setHint("Enter string value");

        textInputEditText = binding.textinputEdittext;
        textInputEditText.setText(savedValue);

        setView(textInputLayout, 10);
        showKeyboardWhenOpen();
    }

    @Override
    public void show() {
        super.show();
        requestEditText(textInputEditText);
    }

    @Override
    protected void onClickSave() {
        super.onClickSave();
        listener.onSave(textInputEditText.getText().toString());
    }
}
