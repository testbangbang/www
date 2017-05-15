package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.onyx.android.sdk.ui.R;

/**
 * Created by Onyx-lw on 2017/5/15.
 */

public class IndicatorDividerView extends View {

    private static final int DEFAULT_WIDTH = 400; //dp
    private static final int DEFAULT_HEIGHT = 5;  //dp
    private static final int DEFAULT_ITEM_COUNT = 6;
    private static final int DEFAULT_INDICATOR_POSITION = 0;
    private static final int DEFAULT_LINE_WIDTH = 3;

    private int position;
    private int itemCount;
    private Paint paint;
    private int lineWidth;
    private Context context;

    public IndicatorDividerView(Context context) {
        this(context, null);
    }

    public IndicatorDividerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorDividerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IndicatorDividerView);
        position = typedArray.getInt(R.styleable.IndicatorDividerView_indicatorPosition, DEFAULT_INDICATOR_POSITION);
        itemCount = typedArray.getInt(R.styleable.IndicatorDividerView_itemCount, DEFAULT_ITEM_COUNT);
        lineWidth = typedArray.getDimensionPixelSize(R.styleable.IndicatorDividerView_lineWidth, DEFAULT_LINE_WIDTH);
        typedArray.recycle();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setStrokeWidth(lineWidth);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureDimensions(widthMeasureSpec, DEFAULT_WIDTH),
                measureDimensions(heightMeasureSpec, DEFAULT_HEIGHT));
    }

    private int measureDimensions(int measureSpec, int defaultDpSize) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int defaultSize = dp2px(context, defaultDpSize);
        if (mode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, size);
        } else {
            result = size;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int trilateralWidth = width / itemCount / 6;
        int trilateralHeight = (int) (trilateralWidth * Math.sin(Math.PI * 60 / 180));
        trilateralHeight = Math.min(height, trilateralHeight);
        int px = (width / itemCount / 2) + (width / itemCount) * position;
        float[] pts = {0, 0, px - trilateralWidth / 2, 0,
                px - trilateralWidth / 2, 0, px, trilateralHeight - 1,
                px, trilateralHeight - 1, px + trilateralWidth / 2, 0,
                px + trilateralWidth / 2, 0, width, 0};
        canvas.drawLines(pts, paint);
    }

    public int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        invalidate();
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
        invalidate();
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }
}
