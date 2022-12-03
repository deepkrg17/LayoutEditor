package com.itsvks.layouteditor.utils;

import android.view.View;

import androidx.annotation.StringRes;

import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.Snackbar;

public class SBUtils {

    public enum Type {
        ERROR,
        SUCCESS,
        INFO
    }

    private Snackbar snackbar;
    private Type type = null;

    private SBUtils(Snackbar snackbar) {
        this.snackbar = snackbar;
    }

    public static SBUtils make(View v, CharSequence msg) {
        return new SBUtils(Snackbar.make(v, msg, Snackbar.LENGTH_SHORT));
    }

    public static SBUtils make(View v, @StringRes int msgResId) {
        return new SBUtils(Snackbar.make(v, msgResId, Snackbar.LENGTH_SHORT));
    }

    public SBUtils setType(SBUtils.Type type) {
        this.type = type;
        return this;
    }

    public Snackbar getSnackbar() {
        return this.snackbar;
    }

    public SBUtils setColors(int colorBg, int colorTxt) {
        this.snackbar
                .setBackgroundTint(colorBg)
                .setTextColor(colorTxt)
                .setActionTextColor(colorTxt);
        return this;
    }

    public SBUtils setFadeAnimation() {
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_FADE);
        return this;
    }

    public SBUtils setSlideAnimation() {
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
        return this;
    }

    public SBUtils setAnchorView(View anchorView) {
        snackbar.setAnchorView(anchorView);
        return this;
    }

    public void show() {
        if (type != null) {
            switch (type) {
                case ERROR:
                    setColors(
                            MaterialColors.getColor(
                                    snackbar.getContext(), R.attr.colorErrorContainer, ""),
                            MaterialColors.getColor(
                                    snackbar.getContext(), R.attr.colorOnErrorContainer, ""));
                    break;
                case SUCCESS:
                    setColors(
                            MaterialColors.getColor(
                                    snackbar.getContext(), R.attr.colorPrimaryContainer, ""),
                            MaterialColors.getColor(
                                    snackbar.getContext(), R.attr.colorOnPrimaryContainer, ""));
                    break;
                case INFO:
                    setColors(
                            MaterialColors.getColor(
                                    snackbar.getContext(), R.attr.colorTertiaryContainer, ""),
                            MaterialColors.getColor(
                                    snackbar.getContext(), R.attr.colorOnTertiaryContainer, ""));
            }
        }
        snackbar.show();
    }

    public void showAsError() {
        this.setType(Type.ERROR).show();
    }

    public void showAsSuccess() {
        this.setType(Type.SUCCESS).show();
    }

    public void showLongAsError() {
        snackbar.setDuration(Snackbar.LENGTH_LONG);
        this.setType(Type.ERROR).show();
    }

    public void showLongAsSuccess() {
        snackbar.setDuration(Snackbar.LENGTH_LONG);
        this.setType(Type.SUCCESS).show();
    }
}
