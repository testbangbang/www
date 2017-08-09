package com.onyx.android.sample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxu on 17-8-8.
 */

public class RectangleSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private UpdateMode currentMode = UpdateMode.A;
    private Handler handler = new Handler(Looper.getMainLooper());
    private SurfaceHolder holder;
    private Paint paint;
    private List<Rect> src = new ArrayList<>();
    com.onyx.android.sample.utils.RectUtils.RectResult rectResult = new com.onyx.android.sample.utils.RectUtils.RectResult();

    public enum UpdateMode {
        A, B, C, D, E
    }

    public RectangleSurfaceView(Context context) {
        this(context, null);
    }

    public RectangleSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        holder = getHolder();
        holder.addCallback(this);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3.0f);
        setFocusable(true);
    }

    public void setUpdateMode(final UpdateMode mode) {
        currentMode = mode;
        startUpdate();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startUpdate();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void startUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                generateRectangles();
                drawRectangle();
                screenUpdate();
                startUpdate();
            }
        }, 8000);
    }

    private void screenUpdate() {
        TestUtils.sleep(1000);
        {
            Rect rect = rectResult.first;
            EpdController.refreshScreenRegion(this, rect.left, rect.top, rect.width(), rect.height(), com.onyx.android.sdk.api.device.epd.UpdateMode.GC);
            TestUtils.sleep(1000);
        }

        for (Rect rect : rectResult.inSecond) {
            EpdController.refreshScreenRegion(this, rect.left, rect.top, rect.width(), rect.height(), com.onyx.android.sdk.api.device.epd.UpdateMode.GC);

        }
        TestUtils.sleep(1000);

        for (Rect rect : rectResult.inFirst) {
            EpdController.refreshScreenRegion(this, rect.left, rect.top, rect.width(), rect.height(), com.onyx.android.sdk.api.device.epd.UpdateMode.GC);

        }
        TestUtils.sleep(1000);


    }

    private void generateRectangles() {

        switch (currentMode) {
            case A:
                generateRectanglesTypeA();
                break;
            case B:
                generateRectanglesTypeB();
                break;
            case C:
                generateRectanglesTypeC();
                break;
            case D:
                generateRectanglesTypeD();
                break;
            case E:
                generateRectanglesTypeE();
                break;
            default:
                break;
        }
    }

    private void generateRectanglesTypeAA() {
        int div = 3;
        int left = TestUtils.randInt(0, getWidth() / div);
        int top = TestUtils.randInt(0, getHeight() / div);
        int width = TestUtils.randInt(getWidth() /  (div * 2), getWidth() /  div);
        int height = TestUtils.randInt(getWidth() /  (div * 2), getHeight() / div);
        Rect r1 = new Rect(left, top, left + width, top + height);

        int left2 = left + width / 2;
        int top2 = top + height / 2;
        Rect r2 = new Rect(left2, top2, left2 + width, top2 + height);
        rectResult.first = r1;
        rectResult.second = r2;
        rectResult.generate();
    }

    private void generateRectanglesTypeA() {
        int div = 3;
        int left = TestUtils.randInt(0, getWidth() / div);
        int top = TestUtils.randInt(0, getHeight() / div);
        int width = TestUtils.randInt(getWidth() /  (div * 2), getWidth() /  div);
        int height = TestUtils.randInt(getWidth() /  (div * 2), getHeight() / div);
        Rect r1 = com.onyx.android.sample.utils.RectUtils.createRect(left, top, left + width, top + height);

        int left2 = left + width / 2;
        int top2 = top + height / 2;
        Rect r2 = com.onyx.android.sample.utils.RectUtils.createRect(left2, top2, left2 + width, top2 + height);

        postGenerate(r1, r2);
    }

    private void postGenerate(final Rect r1, final Rect r2) {
        src.clear();
        src.add(r1);
        src.add(r2);

        rectResult.first = r1;
        rectResult.second = r2;
        rectResult.reset();
        rectResult.generate();
    }

    private void generateRectanglesTypeB() {
        int div = 3;
        int left = TestUtils.randInt(getWidth() /  (div * 2), getWidth() / div);
        int top = TestUtils.randInt(getWidth() /  (div * 2), getHeight() / div);
        int width = TestUtils.randInt(getWidth() /  (div * 2), getWidth() /  div);
        int height = TestUtils.randInt(getWidth() /  (div * 2), getHeight() / div);
        Rect r1 = com.onyx.android.sample.utils.RectUtils.createRect(left, top, left + width, top + height);

        int left2 = TestUtils.randInt(left + 1, left + width - 1);
        int top2 = TestUtils.randInt(top + 1, top + height - 1);
        int right2 = TestUtils.randInt(left + 1, left + width - 1);
        int bottom2 = TestUtils.randInt(top + 1, top + height - 1);
        Rect r2 = com.onyx.android.sample.utils.RectUtils.createRect(left2, top2, right2, bottom2);

        postGenerate(r1, r2);
    }

    private void generateRectanglesTypeC() {
        int div = 4;
        int left = TestUtils.randInt(getWidth() /  (div * 2), getWidth() / div);
        int top = TestUtils.randInt(getWidth() /  (div * 2), getHeight() / div);
        int width = TestUtils.randInt(getWidth() /  (div * 2), getWidth() /  div);
        int height = TestUtils.randInt(getWidth() /  (div * 2), getHeight() / div);
        Rect r1 = new Rect(left, top, left + width, top + height);

        int left2 = left;
        int top2 = top + height - 1;
        Rect r2 = new Rect(left2, top2, left2 + width, top2 + height);
        postGenerate(r1, r2);
    }

    private void generateRectanglesTypeD() {
        int div = 3;
        int left = TestUtils.randInt(getWidth() /  (div * 2), getWidth() / div);
        int top = TestUtils.randInt(getWidth() /  (div * 2), getHeight() / div);
        int width = TestUtils.randInt(getWidth() /  (div * 2), getWidth() /  div);
        int height = TestUtils.randInt(getWidth() /  (div * 2), getHeight() / div);
        Rect r1 = com.onyx.android.sample.utils.RectUtils.createRect(left, top, left + width, top + height);

        int lowLimit = width / 3;
        int upLimit = width / 2;
        int left2 = left + TestUtils.randInt(lowLimit, upLimit);
        int top2 = top - TestUtils.randInt(lowLimit, upLimit);
        int right2 = r1.right - TestUtils.randInt(lowLimit, upLimit);
        int bottom2 = r1.bottom - TestUtils.randInt(lowLimit, upLimit);
        Rect r2 = com.onyx.android.sample.utils.RectUtils.createRect(left2, top2, right2, bottom2);
        postGenerate(r1, r2);
    }

    private void generateRectanglesTypeE() {
        int div = 3;
        int left = TestUtils.randInt(getWidth() /  (div * 2), getWidth() / div);
        int top = TestUtils.randInt(getWidth() /  (div * 2), getHeight() / div);
        int width = TestUtils.randInt(getWidth() /  (div * 2), getWidth() /  div);
        int height = TestUtils.randInt(getWidth() /  (div * 2), getHeight() / div);
        Rect r1 = com.onyx.android.sample.utils.RectUtils.createRect(left, top, left + width, top + height);

        int lowLimit = width / 3;
        int upLimit = width / 2;
        int left2 = left - TestUtils.randInt(lowLimit, upLimit);
        int top2 = top - TestUtils.randInt(lowLimit, upLimit);
        int right2 = r1.right + TestUtils.randInt(lowLimit, upLimit);
        int bottom2 = r1.bottom - TestUtils.randInt(lowLimit, upLimit);
        Rect r2 = com.onyx.android.sample.utils.RectUtils.createRect(left2, top2, right2, bottom2);
        postGenerate(r1, r2);
    }

    private void drawRectangle() {
        Canvas canvas = holder.lockCanvas();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);


        for(Rect rect: rectResult.inSecond) {
            int value = 128;
            paint.setColor(Color.rgb(value, value, value));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6.0f);
            canvas.drawRect(rect, paint);
        }

        for(Rect rect: src) {
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(2.0f);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(rect, paint);
        }

        holder.unlockCanvasAndPost(canvas);
    }
}
