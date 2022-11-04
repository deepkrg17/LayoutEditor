package com.itsvks.layouteditor.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;

import com.itsvks.layouteditor.LayoutEditor;
import com.itsvks.layouteditor.R;

import java.util.Locale;

public class ColorView extends View {
    private int a = 255, r = 255, g = 255, b = 255;

    private Drawable transparent;
    private Paint bitmapPaint;
    private Paint colorPaint;

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        transparent =
                AppCompatResources.getDrawable(
                        LayoutEditor.getContext(), R.drawable.transparent_background);

        colorPaint = new Paint();
        colorPaint.setARGB(a, r, g, b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        transparent.setBounds(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        transparent.draw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), colorPaint);
    }

    public void setColor(int color) {
        a = Color.alpha(color);
        r = Color.red(color);
        g = Color.green(color);
        b = Color.blue(color);

        colorPaint.setARGB(a, r, g, b);
        invalidate();
    }

    public void setAlpha(int value) {
        a = value;
        colorPaint.setARGB(a, r, g, b);
        invalidate();
    }

    public void setRed(int value) {
        r = value;
        colorPaint.setARGB(a, r, g, b);
        invalidate();
    }

    public void setGreen(int value) {
        g = value;
        colorPaint.setARGB(a, r, g, b);
        invalidate();
    }

    public void setBlue(int value) {
        b = value;
        colorPaint.setARGB(a, r, g, b);
        invalidate();
    }

    public int getColor() {
        return Color.argb(a, r, g, b);
    }

    public String getHexColor() {
        return getHex(Color.argb(a, r, g, b));
    }

    private String getHex(int c) {
        return String.format(
                        "%02x%02x%02x%02x",
                        Color.alpha(c), Color.red(c), Color.green(c), Color.blue(c))
                .toUpperCase(Locale.US);
    }
    
    
}
