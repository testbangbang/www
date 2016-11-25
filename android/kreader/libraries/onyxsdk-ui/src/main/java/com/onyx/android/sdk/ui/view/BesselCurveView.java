package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ming on 2016/11/24.
 */

public class BesselCurveView extends View {

    private Paint drawPaint;
    private int color = Color.BLACK;
    private float size = 1;
    private Path path;

    private int viewWidth, viewHeight;

    public BesselCurveView(Context context) {
        super(context);
        init();
    }

    public BesselCurveView(Context context, int color, int size) {
        super(context);
        init();
        this.color = color;
        this.size = size;
    }

    public BesselCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BesselCurveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drawPaint = new Paint();
        path = new Path();
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
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
        drawPaint.setStrokeWidth(size);
        drawPaint.setColor(color);

        canvas.translate(viewWidth / 2, viewHeight / 2);
        path.moveTo(-viewWidth / 2, 0);
        path.rQuadTo(viewWidth / 4, -viewHeight / 2, viewWidth / 2, 0);
        path.rQuadTo(viewWidth * 3 / 4, viewHeight, viewWidth, -viewHeight * 2);
        canvas.drawPath(path, drawPaint);
        path.rewind();
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
