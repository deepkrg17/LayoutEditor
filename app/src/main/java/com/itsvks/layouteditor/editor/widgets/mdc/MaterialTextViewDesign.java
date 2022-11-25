package com.itsvks.layouteditor.editor.widgets.mdc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.textview.MaterialTextView;
import com.itsvks.layouteditor.R;

public class MaterialTextViewDesign extends MaterialTextView {

    private Drawable strokeDrawable;
    private boolean drawStrokeEnabled;

    public MaterialTextViewDesign(Context context) {
        super(context);

        strokeDrawable = AppCompatResources.getDrawable(context, R.drawable.background_stroke_dash);
    }

    public void setStrokeEnabled(boolean enabled) {
        drawStrokeEnabled = enabled;
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (drawStrokeEnabled) strokeDrawable.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        strokeDrawable.setBounds(0, 0, w, h);
    }
}
