package com.itsvks.layouteditor.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

/** Utility class for manipulating bitmaps */
public class BitmapUtil {

  /**
   * Sets a tint on an {@link ImageView} according to the background color of a {@link View}
   *
   * @param imageView the ImageView whose tint will be set
   * @param background the view whose background color will be used to determine the tint
   */
  public static void setImageTintAccordingToBackground(ImageView imageView, View background) {
    int backgroundColor = Color.WHITE;
    // Check if the background View is a CardView
    if (background instanceof CardView) {
      // Get the color from the CardView
      CardView cardView = (CardView) background;
      backgroundColor = cardView.getCardBackgroundColor().getDefaultColor();
    // Check if the background View is a ColorDrawable
    } else if (background.getBackground() instanceof ColorDrawable) {
      backgroundColor = ((ColorDrawable) background.getBackground()).getColor();
    } else {
      // Throw an exception if the background View is not a ColorDrawable
      throw new IllegalArgumentException("Background must be a ColorDrawable");
    }

    // Calculate the luminance from the background color
    double luminance = ColorUtils.calculateLuminance(backgroundColor);
    // Set the image color to black or white depending on the luminance
    if (luminance >= 0.5) {
      imageView.setColorFilter(Color.BLACK);
    } else {
      imageView.setColorFilter(Color.WHITE);
    }
  }

  /**
   * Sets a background color on a {@link View} according to an {@link ImageView}
   *
   * @param context the context
   * @param view the view whose background color will be set
   * @param image the ImageView whose color will be used to determine the background color
   */
  public static void setBackgroundAccordingToImage(Context context, View view, ImageView image) {
    Drawable drawable = image.getDrawable();
    setBackgroundAccordingToImage(context, view, drawable);
  }

  /**
   * Inverts a color by subtracting each color channel from 255
   *
   * @param color the color to invert
   * @return the inverted color
   */
  private static int invertColor(int color) {
    int r = Color.red(color);
    int g = Color.green(color);
    int b = Color.blue(color);
    return Color.rgb(255 - r, 255 - g, 255 - b);
  }

  /**
   * Sets the text color of the TextView according to the background color provided.
   *
   * @param background The background color to use to determine the text color.
   * @param textView The TextView whose text color needs to be set.
   */
  public static void setTextColorAccordingToBackground(View background, TextView textView) {
    int backgroundColor = Color.WHITE;
    if (background instanceof CardView) {
      // Get the color from the CardView
      CardView cardView = (CardView) background;
      backgroundColor = cardView.getCardBackgroundColor().getDefaultColor();
    }
    // Check if the background View is a ColorDrawable
    else if (background.getBackground() instanceof ColorDrawable) {
      backgroundColor = ((ColorDrawable) background.getBackground()).getColor();
    } else {
      // Throw an exception if the background View is not a ColorDrawable
      throw new IllegalArgumentException("Background must be a ColorDrawable");
    }

    // Calculate the luminance from the background color
    double luminance = ColorUtils.calculateLuminance(backgroundColor);
    // Set the text color to black or white depending on the luminance
    if (luminance >= 0.5) {
      textView.setTextColor(Color.BLACK);
    } else {
      textView.setTextColor(Color.WHITE);
    }
  }

  /**
   * Merges two bitmaps into a single bitmap
   *
   * @param background the background bitmap
   * @param foreground the foreground bitmap
   * @return the merged bitmap
   */
  public static Bitmap mergeBitmaps(Bitmap background, Bitmap foreground) {
    // Create a new Bitmap with the width and height of the background
    int width = background.getWidth();
    int height = background.getHeight();
    Bitmap mergedBitmap = Bitmap.createBitmap(width, height, background.getConfig());

    // Create a Canvas object with the new Bitmap
    Canvas canvas = new Canvas(mergedBitmap);

    // Draw the background on the Canvas
    canvas.drawBitmap(background, 0, 0, null);

    // Draw the foreground on the Canvas in the center of the background
    canvas.drawBitmap(
        foreground,
        (width - foreground.getWidth()) / 2,
        (height - foreground.getHeight()) / 2,
        null);

    // Return the merged Bitmap
    return mergedBitmap;
  }

  /**
   * Sets a background color on a {@link View} according to a {@link Drawable}
   *
   * @param context the context
   * @param view the view whose background color will be set
   * @param drawable the Drawable whose color will be used to determine the background color
   */
  public static void setBackgroundAccordingToImage(Context context, View view, Drawable drawable) {
    Bitmap bitmap;
    // Check the drawable type
    if (drawable instanceof BitmapDrawable) {
      // Get the bitmap from the BitmapDrawable
      bitmap = ((BitmapDrawable) drawable).getBitmap();
    } else if (drawable instanceof AdaptiveIconDrawable) {
      // Get the background and foreground drawables from the AdaptiveIconDrawable
      Drawable backgroundDr = ((AdaptiveIconDrawable) drawable).getBackground();
      Drawable foregroundDr = ((AdaptiveIconDrawable) drawable).getForeground();
      // Get the bitmaps from the drawables
      Bitmap background = ((BitmapDrawable) backgroundDr).getBitmap();
      Bitmap foreground = ((BitmapDrawable) foregroundDr).getBitmap();
      // Merge the bitmaps
      bitmap = mergeBitmaps(background, foreground);
    } else {
      // Handle other drawable types as needed
      return;
    }

    // Generate a palette from the bitmap
    Palette palette = Palette.from(bitmap).generate();

    // Get the background color from the palette
    int backgroundColor;
    if (palette.getDarkVibrantColor(0) != 0) {
      backgroundColor = palette.getDarkVibrantColor(0);
    } else if (palette.getDarkMutedColor(0) != 0) {
      backgroundColor = palette.getDarkMutedColor(0);
    } else if (palette.getLightVibrantColor(0) != 0) {
      backgroundColor = palette.getLightVibrantColor(0);
    } else if (palette.getLightMutedColor(0) != 0) {
      backgroundColor = palette.getLightMutedColor(0);
    } else if (palette.getVibrantColor(0) != 0) {
      backgroundColor = palette.getVibrantColor(0);
    } else if (palette.getMutedColor(0) != 0) {
      backgroundColor = palette.getMutedColor(0);
    } else {
      backgroundColor = ContextCompat.getColor(context, android.R.color.white);
    }

    // Set the background color of the view
    if (view instanceof CardView) {
      ((CardView) view).setCardBackgroundColor(backgroundColor);
    } else {
      view.setBackgroundColor(backgroundColor);
    }
  }
}
