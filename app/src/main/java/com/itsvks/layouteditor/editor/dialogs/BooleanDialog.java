package com.itsvks.layouteditor.editor.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.itsvks.layouteditor.R.id;
import com.itsvks.layouteditor.databinding.LayoutBooleanDialogBinding;

public class BooleanDialog extends AttributeDialog {

    private LayoutBooleanDialogBinding binding;

    private AppCompatRadioButton rbTrue;
    private AppCompatRadioButton rbFalse;

    @SuppressLint("ResourceType")
    public BooleanDialog(Context context, String savedValue) {
        super(context);
        binding = LayoutBooleanDialogBinding.inflate(getDialog().getLayoutInflater());
        rbTrue = binding.rbTrue;
        rbFalse = binding.rbFalse;
        setView(binding.getRoot(), 10, 20, 10, 0);

        if (!savedValue.equals("")) {
            if (savedValue.equals("true")) rbTrue.setChecked(true);
            else rbFalse.setChecked(true);
        }
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onClickSave() {
        super.onClickSave();

        String value = binding.getRoot().getCheckedRadioButtonId() == id.rbTrue ? "true" : "false";
        listener.onSave(value);
    }
}
