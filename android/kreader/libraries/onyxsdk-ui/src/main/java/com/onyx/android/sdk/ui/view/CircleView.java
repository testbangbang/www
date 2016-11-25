package com.onyx.android.sdk.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by ming on 2016/11/24.
 */

public class CircleView extends View {

    private int color = Color.BLACK;
    private Paint drawPaint;
    private float size = 1;

    private int viewWidth, viewHeight;

    public CircleView(Context context, int color, final float size) {
        super(context);
        this.color = color;
        this.size = size;
        init();
    }

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drawPaint = new Paint();
        drawPaint.setColor(this.color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float y = size + viewHeight/2 - size;
        canvas.drawCircle(size, y, size, drawPaint);
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
