package com.onyx.android.sample.scribble;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.onyx.android.sdk.utils.Debug;

/**
 * Created by joy on 12/19/17.
 */

public class StrokeRenderer {

    private static final int SMOOTHING_FILTER_WLEN = 6;
    private static final float SMOOTHING_FILTER_POS_DECAY = 0.65f;
    private static final float SMOOTHING_FILTER_PRESSURE_DECAY = 0.9f;

    public static final boolean ASSUME_STYLUS_CALIBRATED = true;

    private final StrokePointFilter filter = new StrokePointFilter(SMOOTHING_FILTER_WLEN,
            SMOOTHING_FILTER_POS_DECAY, SMOOTHING_FILTER_PRESSURE_DECAY);
    private final PressureCooker pressureCooker = new PressureCooker();

    private Paint mPaint = new Paint();
    private Stroke stroke;

    private float mLastX, mLastY, mLastLen;
    private float mLastR = -1;

    private float mPressureExponent = 2.0f;
    private float mRadiusMin = 1.0f;
    private float mRadiusMax = 10.0f;

    public StrokeRenderer(Stroke stroke) {
        this.stroke = stroke;

        mPaint.setColor(Color.BLACK);
    }

    public void draw(Canvas canvas) {
        for (StrokePoint p : stroke.getPoints()) {
            strokeTo(canvas, p);
        }
    }

    public void addPoint(Canvas canvas, StrokePoint point) {
        strokeTo(canvas, filter.doFilter(point));
        stroke.addPoint(point);
    }

    private void strokeTo(Canvas canvas, StrokePoint point) {
        final float pressureNorm;

        if (ASSUME_STYLUS_CALIBRATED && point.toolType == MotionEvent.TOOL_TYPE_STYLUS) {
            pressureNorm = point.pressure;
        } else {
            pressureNorm = pressureCooker.getAdjustedPressure(point.pressure);
        }

        final float radius = lerp(mRadiusMin, mRadiusMax,
                (float) Math.pow(pressureNorm, mPressureExponent));

        strokeTo(canvas, point.x, point.y, radius);
    }

    private void strokeTo(Canvas canvas, float x, float y, float r) {
        if (mLastR < 0) {
            // always draw the first point
            drawStrokePoint(canvas, x,y,r);
        } else {
            // connect the dots, la-la-la
            mLastLen = dist(mLastX, mLastY, x, y);
            float xi, yi, ri, frac;
            float d = 0;
            while (true) {
                if (d > mLastLen) {
                    break;
                }
                frac = d == 0 ? 0 : (d / mLastLen);
                ri = lerp(mLastR, r, frac);
                xi = lerp(mLastX, x, frac);
                yi = lerp(mLastY, y, frac);
                drawStrokePoint(canvas, xi,yi,ri);

                // for very narrow lines we must step (not much more than) one radius at a time
                final float MIN = 1f;
                final float THRESH = 16f;
                final float SLOPE = 0.1f; // asymptote: the spacing will increase as SLOPE*x
                if (ri <= THRESH) {
                    d += MIN;
                } else {
                    d += Math.sqrt(SLOPE * Math.pow(ri - THRESH, 2) + MIN);
                }
            }

        }

        mLastX = x;
        mLastY = y;
        mLastR = r;
    }

    final void drawStrokePoint(Canvas canvas, float x, float y, float r) {
        canvas.drawCircle(x, y, r, mPaint);
    }

    final float dist(float x1, float y1, float x2, float y2) {
        x2-=x1;
        y2-=y1;
        return (float) Math.sqrt(x2*x2 + y2*y2);
    }

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }
}
