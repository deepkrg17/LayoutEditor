package com.itsvks.layouteditor.editor.widgets.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.TextureView;

import androidx.appcompat.content.res.AppCompatResources;

import com.itsvks.layouteditor.R;

public class TextureViewDesign extends TextureView {

    private Drawable strokeDrawable;
    private boolean drawStrokeEnabled;

    public TextureViewDesign(Context ctx) {
        super(ctx);

        strokeDrawable = AppCompatResources.getDrawable(ctx, R.drawable.background_stroke_dash);
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

    public void setStrokeEnabled(boolean enabled) {
        drawStrokeEnabled = enabled;
        invalidate();
    }
}
