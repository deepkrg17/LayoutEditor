package com.itsvks.layouteditor.editor.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.layouteditor.databinding.TextinputlayoutBinding;
import com.itsvks.layouteditor.utils.DimensionUtil;

public class DimensionDialog extends AttributeDialog {

    private TextinputlayoutBinding binding;

    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;

    private String unit;

    public DimensionDialog(Context context, String savedValue, String unit) {
        super(context);

        this.unit = unit;

        binding = TextinputlayoutBinding.inflate(getDialog().getLayoutInflater());

        textInputLayout = binding.getRoot();
        textInputLayout.setHint("Enter dimension value");
        textInputLayout.setSuffixText(unit);
        textInputEditText = binding.textinputEdittext;
        textInputEditText.setInputType(
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        textInputEditText.setText(
                savedValue.equals("") ? "0" : DimensionUtil.getDimenWithoutSuffix(savedValue));

        textInputEditText.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

                    @Override
                    public void onTextChanged(CharSequence text, int p2, int p3, int p4) {}

                    @Override
                    public void afterTextChanged(Editable p1) {
                        checkError();
                    }
                });

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
        listener.onSave(textInputEditText.getText().toString() + unit);
    }

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
