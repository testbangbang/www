package com.onyx.android.sun.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.onyx.android.sun.R;
import com.onyx.android.sun.utils.DensityUtils;

/**
 * Created by hehai on 17-10-26.
 */

public class CircleProgressChart extends View {

    private Paint paint;
    private int width;
    private float progress = 0;
    private int padding = 0;
    private float textSize = 20;
    private float ringWidth;

    public CircleProgressChart(Context context) {
        super(context);
        init();
    }

    public CircleProgressChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleProgressChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setRingWidth(int ringWidth) {
        this.ringWidth = ringWidth;
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(getResources().getColor(R.color.black));
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        width = measuredWidth < measuredHeight ? measuredWidth : measuredHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = width / 2;
        int centerY = width / 2;
        int radius = width / 2 - padding;
        float startC = 0;
        float swap = 360 * progress;

        RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawArc(rectF, startC, swap, true, paint);
        paint.setColor(getResources().getColor(R.color.chart_gray));
        canvas.drawArc(rectF, startC + swap, 360 - swap, true, paint);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        if (ringWidth == 0) {
            ringWidth = radius * 0.1f;
        }
        canvas.drawCircle(centerX, centerY, radius - ringWidth, paint);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(DensityUtils.px2dp(getContext(), textSize));
        canvas.drawText(progress * 100 + "%", centerX, centerY, paint);
    }
}
