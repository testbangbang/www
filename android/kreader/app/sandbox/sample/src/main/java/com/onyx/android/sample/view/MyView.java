package com.onyx.android.sample.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;

/**
 * Created by john on 17/9/2017.
 */

public class MyView extends View {

    private int bkColor = Color.WHITE;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint(Color.BLACK);
        paint.setStrokeWidth(3.0f);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawColor(bkColor);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }

    public void setBkColor(int color) {
        bkColor = color;
    }
}
