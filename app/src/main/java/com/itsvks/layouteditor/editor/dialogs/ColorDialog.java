package com.itsvks.layouteditor.editor.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
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
  private LayoutColorDialogBinding binding;
  private AppCompatSeekBar seekAlpha;
  private AppCompatSeekBar seekRed;
  private AppCompatSeekBar seekGreen;
  private AppCompatSeekBar seekBlue;
  private TextInputLayout inputLayout, aInputLayout, rInputLayout, gInputLayout, bInputLayout;
  private TextInputEditText editText, aInputEditText, rInputEditText, gInputEditText, bInputEditText;

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
    seekAlpha = binding.seekAlpha;
    seekRed = binding.seekRed;
    seekGreen = binding.seekGreen;
    seekBlue = binding.seekBlue;
    
    inputLayout = dialogView.findViewById(R.id.textinput_layout);
    aInputLayout = dialogView.findViewById(R.id.ainput_layout);
    rInputLayout = dialogView.findViewById(R.id.rinput_layout);
    gInputLayout = dialogView.findViewById(R.id.ginput_layout);
    bInputLayout = dialogView.findViewById(R.id.binput_layout);
    editText = dialogView.findViewById(R.id.textinput_edittext);
    aInputEditText = dialogView.findViewById(R.id.ainput_edittext);
    rInputEditText = dialogView.findViewById(R.id.rinput_edittext);
    gInputEditText = dialogView.findViewById(R.id.ginput_edittext);
    bInputEditText = dialogView.findViewById(R.id.binput_edittext);

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
    aInputEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
    rInputEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
    gInputEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
    bInputEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
    if (!savedValue.equals("")) {
      colorPreview.setColor(Color.parseColor(savedValue));
      updateARGB(colorPreview.getColor());
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
    aInputEditText.addTextChangedListener(
        new TextWatcher() {

          @Override
          public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

          @Override
          public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
              final String str = p1.toString();
              if (!TextUtils.isEmpty(str)) {
                  final Integer inte = (!(str != "") && Integer.parseInt(str) > 255) ? 255 : Integer.parseInt(str);
                  //aInputEditText.setText(Integer.parseInt(str) > 255 ? "255" : str);
                  colorPreview.setAlpha(inte);
              }
              updateARGB(colorPreview.getColor());
              updateEditText();
          }

          @Override
          public void afterTextChanged(Editable p1) {}
        });
    rInputEditText.addTextChangedListener(
        new TextWatcher() {

          @Override
          public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

          @Override
          public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
              final String str = p1.toString();
              if (!TextUtils.isEmpty(str)) {
                  final Integer inte = (!(str != "") && Integer.parseInt(str) > 255) ? 255 : Integer.parseInt(str);
                  //rInputEditText.setText(Integer.parseInt(str) > 255 ? "255" : str);
                  colorPreview.setRed(inte);
              }
              updateARGB(colorPreview.getColor());
              updateEditText();
          }

          @Override
          public void afterTextChanged(Editable p1) {}
        });
    gInputEditText.addTextChangedListener(
        new TextWatcher() {

          @Override
          public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

          @Override
          public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
              final String str = p1.toString();
              if (!TextUtils.isEmpty(str)) {
                  final Integer inte = (!(str != "") && Integer.parseInt(str) > 255) ? 255 : Integer.parseInt(str);
                  //gInputEditText.setText(Integer.parseInt(str) > 255 ? "255" : str);
                  colorPreview.setGreen(inte);
              }
              updateARGB(colorPreview.getColor());
              updateEditText();
          }

          @Override
          public void afterTextChanged(Editable p1) {}
        });
    bInputEditText.addTextChangedListener(
        new TextWatcher() {

          @Override
          public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

          @Override
          public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
              final String str = p1.toString();
              if (!TextUtils.isEmpty(str)) {
                  final Integer inte = (!(str != "") && Integer.parseInt(str) > 255) ? 255 : Integer.parseInt(str);
                  //bInputEditText.setText(Integer.parseInt(str) > 255 ? "255" : str);
                  colorPreview.setBlue(inte);
              }
              updateARGB(colorPreview.getColor());
              updateEditText();
          }

          @Override
          public void afterTextChanged(Editable p1) {}
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
      updateARGB(colorPreview.getColor());
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
   * Updates ARGB with Color Values
   *
   * @param color Color to be set
   */
  private void updateARGB(int color) {
    int a = Color.alpha(color);
    int r = Color.red(color);
    int g = Color.green(color);
    int b = Color.blue(color);
//    aInputEditText.setText(String.valueOf(a));
//    aInputEditText.setTextColor(Color.luminance(a) < 0.5f ? Color.WHITE : Color.DKGRAY);
//    rInputEditText.setText(String.valueOf(r));
//    rInputEditText.setTextColor(Color.luminance(r) < 0.5f ? Color.WHITE : Color.DKGRAY);
//    gInputEditText.setText(String.valueOf(g));
//    gInputEditText.setTextColor(Color.luminance(g) < 0.5f ? Color.WHITE : Color.DKGRAY);
//    bInputEditText.setText(String.valueOf(b));
//    bInputEditText.setTextColor(Color.luminance(b) < 0.5f ? Color.WHITE : Color.DKGRAY);
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
          updateARGB(colorPreview.getColor());
          updateEditText();
          break;

        case R.id.seek_red:
          colorPreview.setRed(progress);
          updateARGB(colorPreview.getColor());
          updateEditText();
          break;

        case R.id.seek_green:
          colorPreview.setGreen(progress);
          updateARGB(colorPreview.getColor());
          updateEditText();
          break;

        case R.id.seek_blue:
          colorPreview.setBlue(progress);
          updateARGB(colorPreview.getColor());
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
