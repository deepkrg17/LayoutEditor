package com.itsvks.layouteditor.editor.widgets.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import android.view.View;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.itsvks.layouteditor.R;

public class ConstraintLayoutDesign extends ConstraintLayout {

    private Drawable strokeDrawable;
    private boolean drawStrokeEnabled;

    public ConstraintLayoutDesign(Context context) {
        super(context);

        strokeDrawable = AppCompatResources.getDrawable(context, R.drawable.background_stroke_dash);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        strokeDrawable.setBounds(0, 0, w, h);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (drawStrokeEnabled) strokeDrawable.draw(canvas);
        
        for (int i =0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) child.getLayoutParams();
            
            if (params.leftToLeft == ConstraintLayout.LayoutParams.PARENT_ID) {
                strokeDrawable.draw(canvas);
            }
        }
    }

    public void setStrokeEnabled(boolean enabled) {
        drawStrokeEnabled = enabled;
        invalidate();
    }
}
