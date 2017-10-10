package com.onyx.android.sample.view;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.utils.TestUtils;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by wangxu on 17-8-4.
 */

public class OverlaySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder holder;
    private Thread thread;

    private boolean threadFlag;
    private List<Rect> rectList = new ArrayList<>();

    public OverlaySurfaceView(Activity context) {
        this(context, null);
    }

    public OverlaySurfaceView(Activity context, AttributeSet attrs) {
        super(context, attrs);

        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        threadFlag = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        threadFlag = false;
    }

    @Override
    public void run() {
        while (threadFlag) {
            update();
            TestUtils.sleep(5 * 1000);
        }
    }

    private void generateRects() {
        rectList.clear();
        int rows = TestUtils.randInt(2, 5);
        int cols = TestUtils.randInt(2, 5);
        int width = getWidth() / cols;
        int height = getHeight() / rows;
        int tweak = TestUtils.randInt(1, 1);
        int range = 50;
        for(int r = 0; r < rows; ++r) {
            for(int c = 0; c < cols; ++c) {
                int left = c * width - tweak * TestUtils.randInt(0, range);
                int top = r * height - tweak * TestUtils.randInt(0, range);
                int right = left + width + tweak * TestUtils.randInt(0, range);
                int bottom = top + height + tweak * TestUtils.randInt(0, range);
                Rect rect = new Rect(
                        Math.max(0, left),
                        Math.max(0, top),
                        Math.min(right, getWidth()),
                        Math.min(bottom, getHeight()));
                rectList.add(rect);
            }
        }

    }

    private void renderRects() {
        Canvas canvas = holder.lockCanvas();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3.0f);
        for(Rect rect : rectList) {
            canvas.drawRect(rect, paint);
        }
        holder.unlockCanvasAndPost(canvas);
        TestUtils.sleep(1200);
    }

    private void update() {
        generateRects();
        renderRects();
        screenUpdate();
    }

    private void screenUpdate() {
        int order = TestUtils.randInt(0, 1);
        for(int i = 0; i < rectList.size(); ++i) {
            int pos = i;
            if (order > 0) {
                pos = rectList.size() - 1 - pos;
            }
            Rect rect = rectList.get(pos);
            EpdController.refreshScreenRegion(this, rect.left, rect.top, rect.width(), rect.height(), UpdateMode.GC);
            TestUtils.sleep(100);
        }
    }

    public void stop() {
        threadFlag = false;
        thread.interrupt();
    }

}
