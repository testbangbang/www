package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import com.onyx.android.sdk.ui.R;

/**
 * Created by ming on 2017/2/13.
 */

public class DashedLine extends View {

    private enum Orientation {
        horizontal, vertical
    }

    private Orientation orientation = Orientation.horizontal;

    public DashedLine(Context context) {
        super(context);
    }

    public DashedLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DashedLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DashedLine);
        orientation = orientation.values()[typedArray.getInt(R.styleable.DashedLine_dashLine_orientation, 1)];
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(android.R.color.black));
        Path path = new Path();
        path.moveTo(0, 0);
        if (orientation == Orientation.horizontal) {
            path.lineTo(getMeasuredWidth(), 0);
        }else {
            path.lineTo(0, getMeasuredHeight());
        }
        PathEffect effects = new DashPathEffect(new float[]{4, 4, 4, 4}, 2);
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }
}
