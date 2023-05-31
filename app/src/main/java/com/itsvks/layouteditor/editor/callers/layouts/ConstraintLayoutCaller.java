package com.itsvks.layouteditor.editor.callers.layouts;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.itsvks.layouteditor.managers.IdManager;

public class ConstraintLayoutCaller {

  private static final int PARENT_ID = ConstraintSet.PARENT_ID;
  private static final int LEFT = ConstraintSet.LEFT;
  private static final int RIGHT = ConstraintSet.RIGHT;
  private static final int TOP = ConstraintSet.TOP;
  private static final int BOTTOM = ConstraintSet.BOTTOM;
  private static final int BASELINE = ConstraintSet.BASELINE;
  private static final int START = ConstraintSet.START;
  private static final int END = ConstraintSet.END;

  private static void generateViewId(View view) {
    if (view.getId() == View.NO_ID) view.setId(View.generateViewId());
  }

  private static void setConstraint(
      ConstraintLayout layout, View target, String value, int startSide, int endSide) {
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (value.equals("parent")) {
      set.connect(target.getId(), startSide, PARENT_ID, endSide);
    } else {
      set.connect(target.getId(), startSide, IdManager.getViewId(value), endSide);
    }
    set.applyTo(layout);
  }

  public static void setLeftToLeft(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    generateViewId(target);
    generateViewId(layout);
    setConstraint(layout, target, value, LEFT, LEFT);
  }

  public static void setLeftToRight(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    generateViewId(target);
    generateViewId(layout);
    setConstraint(layout, target, value, LEFT, RIGHT);
  }

  public static void setRightToLeft(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    generateViewId(target);
    generateViewId(layout);
    setConstraint(layout, target, value, RIGHT, LEFT);
  }

  public static void setRightToRight(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    generateViewId(target);
    generateViewId(layout);
    setConstraint(layout, target, value, RIGHT, RIGHT);
  }

  public static void setTopToTop(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    generateViewId(target);
    generateViewId(layout);
    setConstraint(layout, target, value, TOP, TOP);
  }

  public static void setTopToBottom(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    generateViewId(target);
    generateViewId(layout);
    setConstraint(layout, target, value, TOP, BOTTOM);
  }

  public static void setBottomToTop(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    generateViewId(target);
    generateViewId(layout);
    setConstraint(layout, target, value, BOTTOM, TOP);
  }

  public static void setBottomToBottom(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    generateViewId(target);
    generateViewId(layout);
    setConstraint(layout, target, value, BOTTOM, BOTTOM);
  }

  public static void setBaselineToBaseline(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    generateViewId(target);
    generateViewId(layout);
    setConstraint(layout, target, value, BASELINE, BASELINE);
  }

  public static void setStartToStart(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    generateViewId(target);
    generateViewId(layout);
    setConstraint(layout, target, value, START, START);
  }

  public static void setStartToEnd(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    generateViewId(target);
    generateViewId(layout);
    setConstraint(layout, target, value, START, END);
  }

  public static void setEndToStart(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    generateViewId(target);
    generateViewId(layout);
    setConstraint(layout, target, value, END, START);
  }

  public static void setEndToEnd(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    generateViewId(target);
    generateViewId(layout);
    setConstraint(layout, target, value, END, END);
  }
}
