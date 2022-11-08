package com.itsvks.layouteditor.utils;

import android.view.View;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.R;

public class SBUtils {

    public enum Type {
        ERROR,
        SUCCESS,
        INFO
    }

    private View view;
    private Snackbar snackbar;
    private Type type = null;

    private SBUtils(View v, Snackbar snackbar) {
        this.view = v;
        this.snackbar = snackbar;
    }

    public static SBUtils make(View v, CharSequence msg) {
        return new SBUtils(v, Snackbar.make(v, msg, Snackbar.LENGTH_SHORT));
    }

    public static SBUtils make(View v, int msgResId) {
        return new SBUtils(v, Snackbar.make(v, msgResId, Snackbar.LENGTH_SHORT));
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

    public void show() {
        switch (type) {
            case ERROR:
                setColors(
                        MaterialColors.getColor(view, R.attr.colorError),
                        MaterialColors.getColor(view, R.attr.colorOnError));
                break;
            case SUCCESS:
                setColors(0xff4caf50, 0xffffffff);
                break;
            case INFO:
                setColors(0xff17a2b8, 0xffffffff);
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
