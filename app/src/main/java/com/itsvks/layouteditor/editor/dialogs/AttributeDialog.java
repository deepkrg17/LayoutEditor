package com.itsvks.layouteditor.editor.dialogs;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class AttributeDialog {

    private final AlertDialog dialog;
    protected OnSaveValueListener listener;

    private InputMethodManager inputMethodManager;

    public AttributeDialog(Context context) {
        
        dialog = new MaterialAlertDialogBuilder(context).create();
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (di, which) -> {});
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save", (di, which) -> onClickSave());

        inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void setTitle(String title) {
        dialog.setTitle(title);
    }

    public void setView(View view) {
        dialog.setView(view);
    }

    public void setView(View view, int padding) {
        int pad = getDip(view.getContext(), padding);
        dialog.setView(view, pad, pad, pad, pad);
    }

    public void setView(View view, int left, int top, int right, int bottom) {
        dialog.setView(view, getDip(view.getContext(), left), getDip(view.getContext(), top), getDip(view.getContext(),right), getDip(view.getContext(), bottom));
    }

    public void show() {
        dialog.show();
    }

    public void setEnabled(boolean enabled) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(enabled);
    }

    protected void showKeyboardWhenOpen() {
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    protected void requestEditText(TextInputEditText editText) {
        editText.requestFocus();
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        if (!editText.getText().toString().equals("")) {
            editText.setSelection(0, editText.getText().toString().length());
        }
    }

    private int getDip(Context context, int value) {
        return (int)
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        value,
                        context.getResources().getDisplayMetrics());
    }

    protected String getString(Context context, int id) {
        return context.getString(id);
    }

    public void setOnSaveValueListener(OnSaveValueListener listener) {
        this.listener = listener;
    }

    protected void onClickSave() {}

    public interface OnSaveValueListener {

        public void onSave(String value);
    }

    public AlertDialog getDialog() {
        return this.dialog;
    }
}
