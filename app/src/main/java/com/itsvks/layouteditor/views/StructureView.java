package com.itsvks.layouteditor.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import androidx.core.content.ContextCompat;
import java.util.HashMap;
import com.itsvks.layouteditor.R;

/**
 * StructureView class extends from LinearLayoutCompat and implements View.OnClickListener. It
 * creates two hashmaps for storing TextViews and Views, which are the textViewMap and viewTextMap.
 * This class also contains an abstract OnItemClickListener class.
 */
public class StructureView extends LinearLayoutCompat implements View.OnClickListener {
  private LayoutInflater inflater;
  private Paint paint;
  private int pointRadius;
  private HashMap<TextView, View> textViewMap = new HashMap<>();
  private HashMap<View, TextView> viewTextMap = new HashMap<>();
  private OnItemClickListener listener;

  /**
   * This is the constructor of the StructureView class which takes context and attributeSet as
   * parameters. It creates a new Paint object, sets its color and anti-alias. It also sets the
   * orientation of this view to VERTICAL and sets the default OnItemClickListener.
   */
  public StructureView(Context context, AttributeSet attrs) {
    super(context, attrs);
    inflater = LayoutInflater.from(context);

    paint = new Paint();
    paint.setColor(Color.DKGRAY);
    paint.setAntiAlias(true);
    paint.setStrokeWidth(getDip(1));

    pointRadius = getDip(3);

    setOrientation(VERTICAL);
    listener =
        new OnItemClickListener() {
          @Override
          public void onItemClick(View view) {}
        };
  }

  /** This method clears all Views and HashMaps stored in this view. */
  public void clear() {
    removeAllViews();
    textViewMap.clear();
    viewTextMap.clear();
  }

  /**
   * This method sets a View to this view. It clears all the stored views and hashmaps, and then
   * calls the peek() method to peek into the View.
   */
  public void setView(View view) {
    textViewMap.clear();
    viewTextMap.clear();
    removeAllViews();
    peek(view, 1);
  }

  /**
   * This method recursively calls itself to add TextViews for each View inside the ViewGroup. It
   * also stores the TextViews and Views in their respective hashmaps.
   */
  private void peek(View view, int depth) {
    int nextDepth = depth;
    TextView text = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null, false);
    text.setTextSize(16);
    text.setText(view.getClass().getSuperclass().getSimpleName());
    text.setOnClickListener(this);

    int pad = getDip(8);
    text.setPadding(getDip(16), pad, pad, pad);
    addView(text);

    LinearLayoutCompat.LayoutParams params =
        (LinearLayoutCompat.LayoutParams) text.getLayoutParams();
    params.leftMargin = depth * getDip(15);

    textViewMap.put(text, view);
    viewTextMap.put(view, text);

    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      nextDepth++;

      for (int i = 0; i < group.getChildCount(); i++) {
        View child = group.getChildAt(i);
        peek(child, nextDepth);
      }
    }
  }

  /** This method is called to draw rectangles, lines, and circles for each TextView in the view. */
  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);

    for (TextView text : textViewMap.keySet()) {
      View view = textViewMap.get(text);

      if (view instanceof ViewGroup && ((ViewGroup) view).getChildCount() > 0) {
        float x = text.getX();
        float y = text.getY() + text.getHeight() / 2;
        canvas.drawRect(x - pointRadius, y - pointRadius, x + pointRadius, y + pointRadius, paint);

        ViewGroup group = (ViewGroup) view;

        for (int i = 0; i < group.getChildCount(); i++) {
          TextView current = viewTextMap.get(group.getChildAt(i));
          canvas.drawLine(
              text.getX(),
              text.getY() + text.getHeight() / 2,
              text.getX(),
              current.getY() + current.getHeight() / 2,
              paint);
          canvas.drawLine(
              text.getX(),
              current.getY() + current.getHeight() / 2,
              current.getX(),
              current.getY() + current.getHeight() / 2,
              paint);
        }
      } else {
        canvas.drawCircle(text.getX(), text.getY() + text.getHeight() / 2, pointRadius, paint);
      }
    }
  }

  /**
   * This method is called when a TextView is clicked, and it calls the OnItemClickListener's
   * onItemClick method.
   */
  @Override
  public void onClick(View v) {
    if (listener != null) {
      listener.onItemClick(textViewMap.get((TextView) v));
    }
  }

  /** This method is used to convert the input into the equivalent dip value. */
  private int getDip(int input) {
    return (int)
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, input, getContext().getResources().getDisplayMetrics());
  }

  /** This method sets the OnItemClickListener for this view. */
  public void setOnItemClickListener(OnItemClickListener listener) {
    this.listener = listener;
  }

  /**
   * This is an abstract OnItemClickListener class which contains an abstract onItemClick method.
   */
  public abstract static class OnItemClickListener {
    public abstract void onItemClick(View view);
  }
}
