package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.onyx.android.sdk.ui.R;


/**
 * Created by wangxu on 16-7-8.
 */
public class PieChartView extends View {

    private Paint paint;
    private int roundColor;
    private int roundProgressColor;
    private int textColor;
    private float textSize;
    private float roundWidth;
    private float max;
    private float progress;
    private boolean showText;
    private int style;
    private RectF oval = new RectF();

    public static final int STROKE = 0;
    public static final int FILL = 1;

    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.pieChartView);
        roundColor = typedArray.getColor(R.styleable.pieChartView_roundColor, Color.RED);
        roundProgressColor = typedArray.getColor(R.styleable.pieChartView_roundProgressColor, Color.GREEN);
        textColor = typedArray.getColor(R.styleable.pieChartView_progressTextColor, Color.GREEN);
        textSize = typedArray.getDimension(R.styleable.pieChartView_progressTextSize, 15);
        roundWidth = typedArray.getDimension(R.styleable.pieChartView_roundWidth, 5);
        max = typedArray.getInteger(R.styleable.pieChartView_max, 100);
        showText = typedArray.getBoolean(R.styleable.pieChartView_textIsDisplayable, true);
        style = typedArray.getInt(R.styleable.pieChartView_style, 0);

        typedArray.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int center = getWidth() / 2;
        int radius = (int) (center - roundWidth / 2);
        paint.setColor(roundColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roundWidth);
        paint.setAntiAlias(true);
        canvas.drawCircle(center, center, radius, paint);

        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        int percent = (int) ((progress / max) * 100);
        float textWidth = paint.measureText(percent + "%");

        if (showText && percent != 0 && style == STROKE) {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(percent + "%", center - textWidth / 2, center + textSize / 2, paint);
            paint.setStyle(Paint.Style.STROKE);
        }

        paint.setStrokeWidth(roundWidth);
        paint.setColor(roundProgressColor);
        oval.set(center - radius, center - radius, center
                + radius, center + radius);
        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 270, 360 * progress / max, false, paint);
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(oval, 270, 360 * progress / max, true, paint);
                break;
            }
        }

    }

    public synchronized void setProgress(float progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }

    }

    public synchronized float getMax() {
        return max;
    }

    public synchronized void setMax(float max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

}
