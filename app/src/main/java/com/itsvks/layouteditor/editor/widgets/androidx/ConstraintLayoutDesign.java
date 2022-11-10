package com.itsvks.layouteditor.editor.widgets.androidx;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.itsvks.layouteditor.R;

public class ConstraintLayoutDesign extends ConstraintLayout {

    private Drawable strokeDrawable;
    private boolean drawStrokeEnabled;

    private Paint linePaint;
    private Paint fillPaint;

    private final int PARENT_ID = ConstraintLayout.LayoutParams.PARENT_ID;

    public ConstraintLayoutDesign(Context context) {
        super(context);

        strokeDrawable = AppCompatResources.getDrawable(context, R.drawable.background_stroke_dash);

        linePaint = new Paint();
        linePaint.setColor(Color.LTGRAY);
        linePaint.setStrokeWidth(2);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);

        fillPaint = new Paint();
        fillPaint.setColor(Color.LTGRAY);
        fillPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        strokeDrawable.setBounds(0, 0, w, h);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (drawStrokeEnabled) {
            drawBindings(canvas);
        }

        super.dispatchDraw(canvas);

        if (drawStrokeEnabled) strokeDrawable.draw(canvas);
    }

    public void setStrokeEnabled(boolean enabled) {
        drawStrokeEnabled = enabled;
        invalidate();
    }

    // Work in progress
    private void drawBindings(Canvas canvas) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            ConstraintLayout.LayoutParams params =
                    (ConstraintLayout.LayoutParams) view.getLayoutParams();

            if (params.leftToLeft == PARENT_ID) {
                drawHorizontalArrow(
                        canvas,
                        0,
                        view.getTop() + view.getHeight() / 2,
                        view.getLeft(),
                        view.getTop() + view.getHeight() / 2);
                drawVerticalArrow(
                        canvas,
                        view.getRight(),
                        view.getTop() + view.getHeight() / 2,
                        getWidth(),
                        view.getTop() + view.getHeight() / 2);
            }
        }
    }

    private void drawHorizontalArrow(Canvas canvas, int x, int y, int x2, int y2) {
        int width = x2 - x;
        int step = 10;
        int height = 10;

        for (int i = 0; i < width; i += step) {
            // line(x + i, y, x + i + step, y + step);
            canvas.drawLine(x + i, y - height / 2, x + i + step, y + height / 2, linePaint);
            canvas.drawLine(x + i + step, y - height / 2, x + i + step, y + height / 2, linePaint);
        }
    }

    private void drawVerticalArrow(Canvas canvas, int x, int y, int x2, int y2) {
        int height = y2 - y;
        int step = 10;
        int width = 10;

        for (int i = 0; i < height; i += step) {
            canvas.drawLine(x - width / 2, y + i, x + width / 2, y + i + step, linePaint);
            canvas.drawLine(x - width / 2, y + i + step, x + width / 2, y + i + step, linePaint);
        }
    }

    private void drawBind(Canvas canvas, View view, View anchor, int rule) {
        int x1 = 0, y1 = 0;
        int x2 = 0, y2 = 0;
        int offset = 100;

        Path path = new Path();

        switch (rule) {
            case ConstraintSet.BOTTOM:
                {
                    x1 = view.getLeft() + view.getWidth() / 2;
                    y1 = view.getTop();
                    x2 = anchor.getLeft() + anchor.getWidth() / 2;
                    y2 = anchor.getBottom();

                    int halfX = (x1 + x2) / 2;
                    int halfY = (y1 + y2) / 2;

                    path.moveTo(x1, y1);
                    path.cubicTo(x1, y1, x1, y1 - offset, halfX, halfY);
                    path.moveTo(x2, y2);
                    path.cubicTo(x2, y2, x2, y2 - offset, halfX, halfY);
                    break;
                }

            case ConstraintSet.TOP:
                {
                    x1 = view.getLeft() + view.getWidth() / 2;
                    y1 = view.getBottom();
                    x2 = anchor.getLeft() + anchor.getWidth() / 2;
                    y2 = anchor.getTop();

                    int halfX = (x1 + x2) / 2;
                    int halfY = (y1 + y2) / 2;

                    path.moveTo(x1, y1);
                    path.cubicTo(x1, y1, x1, y1 + offset, halfX, halfY);
                    path.moveTo(x2, y2);
                    path.cubicTo(x2, y2, x2, y2 - offset, halfX, halfY);
                    break;
                }

            case ConstraintSet.LEFT:
                {
                    x1 = view.getRight();
                    y1 = view.getTop() + view.getHeight() / 2;
                    x2 = anchor.getLeft();
                    y2 = anchor.getTop() + anchor.getHeight() / 2;

                    int halfX = (x1 + x2) / 2;
                    int halfY = (y1 + y2) / 2;

                    path.moveTo(x1, y1);
                    path.cubicTo(x1, y1, x1 + offset, y1, halfX, halfY);
                    path.moveTo(x2, y2);
                    path.cubicTo(x2, y2, x2 - offset, y2, halfX, halfY);
                    break;
                }

            case ConstraintSet.RIGHT:
                {
                    x1 = view.getLeft();
                    y1 = view.getTop() + view.getHeight() / 2;
                    x2 = anchor.getRight();
                    y2 = anchor.getTop() + anchor.getHeight() / 2;

                    int halfX = (x1 + x2) / 2;
                    int halfY = (y1 + y2) / 2;

                    path.moveTo(x1, y1);
                    path.cubicTo(x1, y1, x1 - offset, y1, halfX, halfY);
                    path.moveTo(x2, y2);
                    path.cubicTo(x2, y2, x2 + offset, y2, halfX, halfY);
                    break;
                }
        }

        canvas.drawPath(path, linePaint);
        canvas.drawCircle(x2, y2, 10, fillPaint);
    }
}
