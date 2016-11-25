package com.onyx.android.sdk.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by ming on 2016/11/24.
 */

public class CircleView extends View {

    private int color;
    private final Paint drawPaint;
    private float size;

    public CircleView(Context context, int color, final float size) {
        super(context);
        this.color = color;
        this.size = size;
        drawPaint = new Paint();
        drawPaint.setColor(this.color);
        setOnMeasureCallback();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(size, size, size, drawPaint);
    }

    private void setOnMeasureCallback() {
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeOnGlobalLayoutListener(this);
                size = getMeasuredWidth() / 2;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void removeOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    public void setSize(float size) {
        this.size = size;
        invalidate();
    }
}
