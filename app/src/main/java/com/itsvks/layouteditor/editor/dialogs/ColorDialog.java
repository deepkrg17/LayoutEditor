package com.itsvks.layouteditor.editor.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.databinding.LayoutColorDialogBinding;
import com.itsvks.layouteditor.views.ColorView;

import java.util.regex.Pattern;

public class ColorDialog extends AttributeDialog
        implements AppCompatSeekBar.OnSeekBarChangeListener {

    private ColorView colorPreview;
    private MaterialTextView textColorPreview;
    private LayoutColorDialogBinding binding;

    private AppCompatSeekBar seekAlpha;
    private AppCompatSeekBar seekRed;
    private AppCompatSeekBar seekGreen;
    private AppCompatSeekBar seekBlue;

    private TextInputLayout inputLayout;
    private TextInputEditText editText;

    public ColorDialog(Context context, String savedValue) {
        super(context);

        binding = LayoutColorDialogBinding.inflate(getDialog().getLayoutInflater());

        final View dialogView = binding.getRoot();

        colorPreview = binding.colorPreview;
        textColorPreview = binding.textColorPreview;
        

        seekAlpha = binding.seekAlpha;
        seekAlpha.setOnSeekBarChangeListener(this);
        seekAlpha.setMax(255);
        seekAlpha.setProgress(255);

        seekRed = binding.seekRed;
        seekRed.setOnSeekBarChangeListener(this);
        seekRed.setMax(255);
        seekRed.setProgress(255);

        seekGreen = binding.seekGreen;
        seekGreen.setOnSeekBarChangeListener(this);
        seekGreen.setMax(255);
        seekGreen.setProgress(255);

        seekBlue = binding.seekBlue;
        seekBlue.setOnSeekBarChangeListener(this);
        seekBlue.setMax(255);
        seekBlue.setProgress(255);

        inputLayout = dialogView.findViewById(R.id.textinput_layout);
        inputLayout.setHint("Enter custom HEX code");
        inputLayout.setPrefixText("#");

        editText = dialogView.findViewById(R.id.textinput_edittext);
        updateEditText();

        if (!savedValue.equals("")) {
            colorPreview.setColor(Color.parseColor(savedValue));
            updateText(colorPreview.getColor());
            updateSeekbars(colorPreview.getColor());
            updateEditText();
        }
        
        // editText.setText("FFFFFFFF");
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});

        editText.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

                    @Override
                    public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {}

                    @Override
                    public void afterTextChanged(Editable p1) {
                        checkHexErrors(editText.getText().toString());
                    }
                });

        setView(dialogView, 10);
    }

    @Override
    public void onClickSave() {
        listener.onSave("#" + colorPreview.getHexColor());
    }

    private void checkHexErrors(String hex) {
        if (Pattern.matches("[a-fA-F0-9]{6}", hex) || Pattern.matches("[a-fA-F0-9]{8}", hex)) {
            colorPreview.setColor(Color.parseColor("#" + hex));
            updateSeekbars(colorPreview.getColor());
            updateText(colorPreview.getColor());

            inputLayout.setErrorEnabled(false);
            inputLayout.setError("");
            setEnabled(true);
            return;
        }

        inputLayout.setErrorEnabled(true);
        inputLayout.setError("Invalid HEX value");
        setEnabled(false);
    }

    private void updateText(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        textColorPreview.setText(a + ", " + r + ", " + g + ", " + b);
        textColorPreview.setTextColor(Color.luminance(color) < 0.5f ? Color.WHITE : Color.DKGRAY);
    }

    private void updateSeekbars(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        seekAlpha.setProgress(a);
        seekRed.setProgress(r);
        seekGreen.setProgress(g);
        seekBlue.setProgress(b);
    }

    private void updateEditText() {
        editText.setText(colorPreview.getHexColor());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onProgressChanged(SeekBar seek, int progress, boolean fromUser) {
        if (fromUser) {
            switch (seek.getId()) {
                case R.id.seek_alpha:
                    colorPreview.setAlpha(progress);
                    updateText(colorPreview.getColor());
                    updateEditText();
                    break;

                case R.id.seek_red:
                    colorPreview.setRed(progress);
                    updateText(colorPreview.getColor());
                    updateEditText();
                    break;

                case R.id.seek_green:
                    colorPreview.setGreen(progress);
                    updateText(colorPreview.getColor());
                    updateEditText();
                    break;

                case R.id.seek_blue:
                    colorPreview.setBlue(progress);
                    updateText(colorPreview.getColor());
                    updateEditText();
                    break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar p1) {}

    @Override
    public void onStopTrackingTouch(SeekBar p1) {}
}
