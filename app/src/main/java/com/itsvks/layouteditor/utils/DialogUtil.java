package com.itsvks.layouteditor.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.ListAdapter;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogUtil {
    private Context context;
    private MaterialAlertDialogBuilder builder;

    public DialogUtil(Context context) {
        this.context = context;
        builder = new MaterialAlertDialogBuilder(context);
    }

    public DialogUtil setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public DialogUtil setMessage(String message) {
        builder.setMessage(message);
        return this;
    }

    public DialogUtil setAdapter(ListAdapter adapter, DialogInterface.OnClickListener listener) {
        builder.setAdapter(adapter, listener);
        return this;
    }

    public DialogUtil setPositiveButton(String title, DialogInterface.OnClickListener listener) {
        builder.setPositiveButton(title, listener);
        return this;
    }

    public DialogUtil setNegativeButton(String title, DialogInterface.OnClickListener listener) {
        builder.setNegativeButton(title, listener);
        return this;
    }

    public DialogUtil setNeutralButton(String title, DialogInterface.OnClickListener listener) {
        builder.setNeutralButton(title, listener);
        return this;
    }

    public void show() {
        builder.create().show();
    }
}
