package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * Created by ming on 2016/11/24.
 */

public class BesselCurveView extends View {

    private final Paint drawPaint;
    private int color;
    private Path path;

    public BesselCurveView(Context context) {
        super(context);
        drawPaint = new Paint();
        drawPaint.setColor(color);
        drawPaint.setStyle(Paint.Style.STROKE);
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPaint.reset();
        path.moveTo(100, 320);//设置Path的起点
        path.quadTo(150, 310, 170, 400); //设置贝塞尔曲线的控制点坐标和终点坐标
        canvas.drawPath(path, drawPaint);//画出贝塞尔曲线
    }
}
