package com.itsvks.layouteditor.tools;

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

import java.util.HashMap;

public class StructureView extends LinearLayoutCompat implements View.OnClickListener {
    private LayoutInflater inflater;
    private Paint paint;

    private int pointRadius;

    private HashMap<TextView, View> textViewMap = new HashMap<>();
    private HashMap<View, TextView> viewTextMap = new HashMap<>();

    private OnItemClickListener listener =
            new OnItemClickListener() {

                @Override
                public void onItemClick(View view) {}
            };

    public StructureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);

        paint = new Paint();
        paint.setColor(Color.DKGRAY);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(getDip(1));

        pointRadius = getDip(3);

        setOrientation(VERTICAL);
    }

    public void clear() {
        removeAllViews();
        textViewMap.clear();
        viewTextMap.clear();
    }

    public void setView(View view) {
        textViewMap.clear();
        viewTextMap.clear();
        removeAllViews();
        peek(view, 1);
    }

    private void peek(View view, int depth) {
        int nextDepth = depth;
        TextView text =
                (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null, false);
        text.setTextSize(16);
        text.setText(view.getClass().getSuperclass().getSimpleName());
        text.setOnClickListener(this);
        text.setBackgroundColor(android.R.attr.selectable);

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

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        for (TextView text : textViewMap.keySet()) {
            View view = textViewMap.get(text);

            if (view instanceof ViewGroup && ((ViewGroup) view).getChildCount() > 0) {
                float x = text.getX();
                float y = text.getY() + text.getHeight() / 2;
                canvas.drawRect(
                        x - pointRadius, y - pointRadius, x + pointRadius, y + pointRadius, paint);

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
                canvas.drawCircle(
                        text.getX(), text.getY() + text.getHeight() / 2, pointRadius, paint);
            }
        }
    }

    @Override
    public void onClick(View v) {
        listener.onItemClick(textViewMap.get((TextView) v));
    }

    private int getDip(int input) {
        return (int)
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        input,
                        getContext().getResources().getDisplayMetrics());
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public abstract static class OnItemClickListener {
        public abstract void onItemClick(View view);
    }
}
