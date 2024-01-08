package com.itsvks.layouteditor.utils;

import android.view.View;

import androidx.annotation.StringRes;

import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.Snackbar;

/**
 * SBUtils is a utility class for creating and displaying snackbars.
 */
public class SBUtils {

  /**
   * Enum for the types of snackbars that can be created.
   */
  public enum Type {
    ERROR,
    SUCCESS,
    INFO
  }

  private Snackbar snackbar;
  private Type type = null;

  /**
   * Constructor for SBUtils with the given snackbar.
   */
  private SBUtils(Snackbar snackbar) {
    this.snackbar = snackbar;
  }

  /**
   * Creates and returns a new instance of SBUtils with a Snackbar with the given message for the
   * given view.
   * 
   * @param v The view where the snackbar should be displayed
   * @param msg The message to display in the snackbar
   * @return a new instance of SBUtils
   */
  public static SBUtils make(View v, CharSequence msg) {
    return new SBUtils(Snackbar.make(v, msg, Snackbar.LENGTH_SHORT));
  }

  /**
   * Creates and returns a new instance of SBUtils with a Snackbar with the given string resource
   * ID for the given view.
   * 
   * @param v The view where the snackbar should be displayed
   * @param msgResId The string resource ID to display in the snackbar
   * @return a new instance of SBUtils
   */
  public static SBUtils make(View v, @StringRes int msgResId) {
    return new SBUtils(Snackbar.make(v, msgResId, Snackbar.LENGTH_SHORT));
  }

  /**
   * Sets the type of the snackbar.
   * 
   * @param type The type of snackbar to set
   * @return The SBUtils instance
   */
  public SBUtils setType(SBUtils.Type type) {
    this.type = type;
    return this;
  }

  /**
   * Returns the snackbar instance.
   * 
   * @return The snackbar instance
   */
  public Snackbar getSnackbar() {
    return this.snackbar;
  }

  /**
   * Sets the background color and text color of the snackbar.
   * 
   * @param colorBg The background color of the snackbar
   * @param colorTxt The text color of the snackbar
   * @return The SBUtils instance
   */
  public SBUtils setColors(int colorBg, int colorTxt) {
    this.snackbar.setBackgroundTint(colorBg).setTextColor(colorTxt).setActionTextColor(colorTxt);
    return this;
  }

  /**
   * Sets the animation mode of the snackbar to Fade.
   * 
   * @return The SBUtils instance
   */
  public SBUtils setFadeAnimation() {
    snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_FADE);
    return this;
  }

  /**
   * Sets the animation mode of the snackbar to Slide.
   * 
   * @return The SBUtils instance
   */
  public SBUtils setSlideAnimation() {
    snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
    return this;
  }

  /**
   * Sets the anchor view for the snackbar.
   * 
   * @param anchorView The anchor view for the snackbar
   * @return The SBUtils instance
   */
  public SBUtils setAnchorView(View anchorView) {
    snackbar.setAnchorView(anchorView);
    return this;
  }

  /**
   * Shows the snackbar. The colors will be set based on the type if a type has been set.
   */
  public void show() {
    if (type != null) {
      switch (type) {
        case ERROR:
          setColors(
              MaterialColors.getColor(snackbar.getContext(), R.attr.colorErrorContainer, ""),
              MaterialColors.getColor(snackbar.getContext(), R.attr.colorOnErrorContainer, ""));
          break;
        case SUCCESS:
          setColors(
              MaterialColors.getColor(snackbar.getContext(), R.attr.colorPrimaryContainer, ""),
              MaterialColors.getColor(snackbar.getContext(), R.attr.colorOnPrimaryContainer, ""));
          break;
        case INFO:
          setColors(
              MaterialColors.getColor(snackbar.getContext(), R.attr.colorTertiaryContainer, ""),
              MaterialColors.getColor(snackbar.getContext(), R.attr.colorOnTertiaryContainer, ""));
      }
    }
    snackbar.show();
  }

  /**
   * Sets the type of snackbar to ERROR and shows it.
   */
  public void showAsError() {
    this.setType(Type.ERROR).show();
  }

  /**
   * Sets the type of snackbar to SUCCESS and shows it.
   */
  public void showAsSuccess() {
    this.setType(Type.SUCCESS).show();
  }

  /**
   * Sets the duration of snackbar to LONG and sets the type of snackbar to ERROR and shows it.
   */
  public void showLongAsError() {
    snackbar.setDuration(Snackbar.LENGTH_LONG);
    this.setType(Type.ERROR).show();
  }

  /**
   * Sets the duration of snackbar to LONG and sets the type of snackbar to SUCCESS and shows it.
   */
  public void showLongAsSuccess() {
    snackbar.setDuration(Snackbar.LENGTH_LONG);
    this.setType(Type.SUCCESS).show();
  }
}