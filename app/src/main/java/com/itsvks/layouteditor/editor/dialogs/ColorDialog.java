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

  // Declaring Views
  private ColorView colorPreview;
  private MaterialTextView textColorPreview;
  private LayoutColorDialogBinding binding;
  private AppCompatSeekBar seekAlpha;
  private AppCompatSeekBar seekRed;
  private AppCompatSeekBar seekGreen;
  private AppCompatSeekBar seekBlue;
  private TextInputLayout inputLayout;
  private TextInputEditText editText;

  /**
   * Constructor of ColorDialog
   *
   * @param context Application Context
   * @param savedValue Saved Color Value
   */
  public ColorDialog(Context context, String savedValue) {
    super(context);

    // Inflate Layout Binding
    binding = LayoutColorDialogBinding.inflate(getDialog().getLayoutInflater());

    // Getting View from binding
    final View dialogView = binding.getRoot();

    // Initializing Views
    colorPreview = binding.colorPreview;
    textColorPreview = binding.textColorPreview;
    seekAlpha = binding.seekAlpha;
    seekRed = binding.seekRed;
    seekGreen = binding.seekGreen;
    seekBlue = binding.seekBlue;
    inputLayout = dialogView.findViewById(R.id.textinput_layout);
    editText = dialogView.findViewById(R.id.textinput_edittext);

    // Setting Seekbar Progress and Listener
    setSeekbarProgressAndListener(seekAlpha, 255);
    setSeekbarProgressAndListener(seekRed, 255);
    setSeekbarProgressAndListener(seekGreen, 255);
    setSeekbarProgressAndListener(seekBlue, 255);

    // Setting UI Values
    setUIValues(savedValue);

    // Setting TextWatcher on EditText
    setTextWatcherOnEditText();

    setView(dialogView, 10);
  }

  /**
   * Sets Seekbar Progress and Listener
   *
   * @param seekBar SeekBar to set
   * @param progress Initial Progress
   */
  private void setSeekbarProgressAndListener(AppCompatSeekBar seekBar, int progress) {
    seekBar.setOnSeekBarChangeListener(this);
    seekBar.setMax(255);
    seekBar.setProgress(progress);
  }

  /**
   * Sets UI Values
   *
   * @param savedValue Saved Color Value
   */
  private void setUIValues(String savedValue) {
    inputLayout.setHint("Enter custom HEX code");
    inputLayout.setPrefixText("#");
    editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
    if (!savedValue.equals("")) {
      colorPreview.setColor(Color.parseColor(savedValue));
      updateText(colorPreview.getColor());
      updateSeekbars(colorPreview.getColor());
      updateEditText();
    }
  }

  /** Sets TextWatcher on EditText */
  private void setTextWatcherOnEditText() {
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
  }

  /** Called when Save button is clicked */
  @Override
  public void onClickSave() {
    listener.onSave("#" + colorPreview.getHexColor());
  }

  /**
   * Checks for Hex Errors
   *
   * @param hex user entered HEX value
   */
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

  /**
   * Updates TextView with Color Values
   *
   * @param color Color to be set
   */
  private void updateText(int color) {
    int a = Color.alpha(color);
    int r = Color.red(color);
    int g = Color.green(color);
    int b = Color.blue(color);
    textColorPreview.setText(a + ", " + r + ", " + g + ", " + b);
    textColorPreview.setTextColor(Color.luminance(color) < 0.5f ? Color.WHITE : Color.DKGRAY);
  }

  /**
   * Updates Seekbars with Color Values
   *
   * @param color Color to be set
   */
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

  /** Updates EditText with Color Values */
  private void updateEditText() {
    editText.setText(colorPreview.getHexColor());
  }

  /**
   * Called when Seekbar progress is changed
   *
   * @param seek Seekbar which is changed
   * @param progress Progress of Seekbar
   * @param fromUser True if changed by user
   */
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
