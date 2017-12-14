package com.onyx.jdread.shop.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hehai on 17-5-9.
 */

public class CustomScrollBar extends View {

    private Paint paint;
    private int total = 1;
    private int focusPosition;

    public CustomScrollBar(Context context) {
        this(context, null);
    }

    public CustomScrollBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomScrollBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int perTextHeight = getHeight() / total;
        RectF rectF = new RectF(0, perTextHeight * focusPosition, getWidth(), perTextHeight * (focusPosition + 1));
        canvas.drawRoundRect(rectF, 5f, 5f, paint);
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setFocusPosition(int focusPosition) {
        this.focusPosition = focusPosition;
        invalidate();
    }
}

