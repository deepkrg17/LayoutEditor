package com.itsvks.layouteditor.editor.callers.layouts;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.itsvks.layouteditor.managers.IdManager;

public class ConstraintLayoutCaller {

  static final String tag = "ConstraintLayoutCaller";

  private static final int PARENT = ConstraintLayout.LayoutParams.PARENT_ID;

  public static void setLeftToLeft(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());
    if (layout.getId() == View.NO_ID) layout.setId(View.generateViewId());
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (value.equals("parent")) {
      set.connect(target.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
    } else {
      set.connect(
          target.getId(), ConstraintSet.LEFT, IdManager.getViewId(value), ConstraintSet.LEFT);
    }
    set.applyTo(layout);
  }

  public static void setLeftToRight(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());
    if (layout.getId() == View.NO_ID) layout.setId(View.generateViewId());
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (value.equals("parent")) {
      set.connect(target.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
    } else {
      set.connect(
          target.getId(), ConstraintSet.LEFT, IdManager.getViewId(value), ConstraintSet.RIGHT);
    }

    set.applyTo(layout);
  }

  public static void setRightToLeft(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());
    if (layout.getId() == View.NO_ID) layout.setId(View.generateViewId());
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (value.equals("parent")) {
      set.connect(target.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
    } else {
      set.connect(
          target.getId(), ConstraintSet.RIGHT, IdManager.getViewId(value), ConstraintSet.LEFT);
    }

    set.applyTo(layout);
  }

  public static void setRightToRight(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());
    if (layout.getId() == View.NO_ID) layout.setId(View.generateViewId());
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (value.equals("parent")) {
      set.connect(
          target.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
    } else {
      set.connect(
          target.getId(), ConstraintSet.RIGHT, IdManager.getViewId(value), ConstraintSet.RIGHT);
    }

    set.applyTo(layout);
  }

  public static void setTopToTop(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());
    if (layout.getId() == View.NO_ID) layout.setId(View.generateViewId());
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (value.equals("parent")) {
      set.connect(target.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
    } else {
      set.connect(target.getId(), ConstraintSet.TOP, IdManager.getViewId(value), ConstraintSet.TOP);
    }

    set.applyTo(layout);
  }

  public static void setTopToBottom(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());
    if (layout.getId() == View.NO_ID) layout.setId(View.generateViewId());
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (value.equals("parent")) {
      set.connect(target.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
    } else {
      set.connect(
          target.getId(), ConstraintSet.TOP, IdManager.getViewId(value), ConstraintSet.BOTTOM);
    }

    set.applyTo(layout);
  }

  public static void setBottomToTop(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());
    if (layout.getId() == View.NO_ID) layout.setId(View.generateViewId());
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());

    if (value.equals("parent")) {
      set.connect(target.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
    } else {
      set.connect(
          target.getId(), ConstraintSet.BOTTOM, IdManager.getViewId(value), ConstraintSet.TOP);
    }

    set.applyTo(layout);
  }

  public static void setBottomToBottom(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());
    if (layout.getId() == View.NO_ID) layout.setId(View.generateViewId());
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (value.equals("parent")) {
      set.connect(
          target.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
    } else {
      set.connect(
          target.getId(), ConstraintSet.BOTTOM, IdManager.getViewId(value), ConstraintSet.BOTTOM);
    }

    set.applyTo(layout);
  }

  public static void setBaselineToBaseline(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());
    if (layout.getId() == View.NO_ID) layout.setId(View.generateViewId());
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (value.equals("parent")) {
      set.connect(
          target.getId(), ConstraintSet.BASELINE, ConstraintSet.PARENT_ID, ConstraintSet.BASELINE);
    } else {
      set.connect(
          target.getId(),
          ConstraintSet.BASELINE,
          IdManager.getViewId(value),
          ConstraintSet.BASELINE);
    }

    set.applyTo(layout);
  }

  public static void setStartToStart(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());
    if (layout.getId() == View.NO_ID) layout.setId(View.generateViewId());
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (value.equals("parent")) {
      set.connect(
          target.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
    } else {
      set.connect(
          target.getId(), ConstraintSet.START, IdManager.getViewId(value), ConstraintSet.START);
    }

    set.applyTo(layout);
  }

  public static void setStartToEnd(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());
    if (layout.getId() == View.NO_ID) layout.setId(View.generateViewId());
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (value.equals("parent")) {
      set.connect(target.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END);
    } else {
      set.connect(
          target.getId(), ConstraintSet.START, IdManager.getViewId(value), ConstraintSet.END);
    }

    set.applyTo(layout);
  }

  public static void setEndToStart(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());
    if (layout.getId() == View.NO_ID) layout.setId(View.generateViewId());
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (value.equals("parent")) {
      set.connect(target.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START);
    } else {
      set.connect(
          target.getId(), ConstraintSet.END, IdManager.getViewId(value), ConstraintSet.START);
    }

    set.applyTo(layout);
  }

  public static void setEndToEnd(View target, String value, Context context) {
    ConstraintLayout layout = (ConstraintLayout) target.getParent();
    if (target.getId() == View.NO_ID) target.setId(View.generateViewId());
    if (layout.getId() == View.NO_ID) layout.setId(View.generateViewId());
    ConstraintSet set = new ConstraintSet();
    set.clone(layout);

    if (value.equals("parent")) {
      set.connect(target.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
    } else {
      set.connect(target.getId(), ConstraintSet.END, IdManager.getViewId(value), ConstraintSet.END);
    }

    set.applyTo(layout);
  }
}
