package com.onyx.android.sample.scribble;

import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by joy on 12/19/17.
 */

public class StrokePointFilter {
    public static boolean PRECISE_STYLUS_INPUT = false;

    private int bufferSize;
    private float posDecay;
    private float pressureDecay;
    private ArrayList<StrokePoint> bufferedPoints = new ArrayList<>();

    public StrokePointFilter(int size, float posDecay, float pressureDecay) {
        bufferSize = size;
        this.posDecay = (posDecay >= 0 && posDecay <= 1) ? posDecay : 1f;
        this.pressureDecay = (pressureDecay >= 0 && pressureDecay <= 1) ? pressureDecay : 1f;
    }

    public StrokePoint doFilter(StrokePoint point) {
        addToBuffer(point);
        return filterPoint();
    }

    private void addToBuffer(StrokePoint point) {
        if (bufferedPoints.size() >= bufferSize) {
            bufferedPoints.remove(0);
        }
        bufferedPoints.add(point);
    }

    private StrokePoint filterPoint() {
        float wi = 1, w = 0;
        float wi_press = 1, w_press = 0;
        float x = 0, y = 0, pressure = 0, size = 0;
        long time = 0;

        for (int i = bufferedPoints.size() - 1; i >= 0; i--) {
            StrokePoint p = bufferedPoints.get(i);

            x += p.x * wi;
            y += p.y * wi;
            time += p.time * wi;

            pressure += p.pressure * wi_press;
            size += p.size * wi_press;

            w += wi;
            wi *= posDecay; // exponential backoff

            w_press += wi_press;
            wi_press *= pressureDecay;

            if (PRECISE_STYLUS_INPUT && p.toolType == MotionEvent.TOOL_TYPE_STYLUS) {
                // just take the top one, no need to average
                break;
            }
        }

        StrokePoint point = new StrokePoint();
        point.x = x / w;
        point.y = y / w;
        point.pressure = pressure / w_press;
        point.size = size / w_press;
        point.time = time;
        point.toolType = bufferedPoints.get(0).toolType;
        return point;
    }
}
