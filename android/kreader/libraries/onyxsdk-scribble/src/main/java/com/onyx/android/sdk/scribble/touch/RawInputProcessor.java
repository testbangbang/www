package com.onyx.android.sdk.scribble.touch;

import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhuzeng on 6/17/16.
 * Manage all epd control in this class. Caller or callback does not need to manage epd anymore.
 * Only when state is explicitly changed by caller, this class always collect data.
 * - collect data from input device
 * - change state according to pen button state
 * - framework automatically change epd controller state when it detects pen state changed.
 */
public class RawInputProcessor {

    static{
        System.loadLibrary("touch_reader");
    }

    private static final String TAG = RawInputProcessor.class.getSimpleName();

    private static final int PEN_SIZE = 0;

    private static final int ON_PRESS = 0;
    private static final int ON_MOVE = 1;
    private static final int ON_RELEASE = 2;

    private native void nativeRawReader();
    private native void nativeRawClose();
    private native void nativeSetStrokeWidth(float strokeWidth);
    private native void nativeSetLimitRegion(float[] limitRegion);

    public static abstract class RawInputCallback {

        // when received pen down or stylus button
        public abstract void onBeginRawData();

        public abstract void onEndRawData();

        // when pen released.
        public abstract void onRawTouchPointListReceived(final Shape shape, final TouchPointList pointList);

        // caller should render the page here.
        public abstract void onBeginErasing();

        public abstract void onEndErasing();

        // caller should do hit test in current page, remove shapes hit-tested.
        public abstract void onEraseTouchPointListReceived(final TouchPointList pointList);

    }

    private volatile boolean erasing = false;
    private volatile boolean stop = false;
    private volatile boolean reportData = false;
    private volatile boolean moveFeedback = false;
    private volatile float[] srcPoint = new float[2];
    private volatile float[] dstPoint = new float[2];
    private volatile TouchPointList touchPointList;
    private RawInputCallback rawInputCallback;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ExecutorService singleThreadPool = null;
    private volatile View hostView;

    public void setRawInputCallback(final RawInputCallback callback) {
        rawInputCallback = callback;
    }

    public void setHostView(final View view) {
        hostView = view;
    }

    public View getHostView() {
        return hostView;
    }

    public void start() {
        closeRawInput();
        stop = false;
        reportData = false;
        submitJob();
    }

    public void resume() {
        reportData = true;
    }

    public void pause() {
        reportData = false;
    }

    public void quit() {
        rawInputCallback = null;
        hostView = null;
        closeRawInput();
        reportData = false;
        stop = true;
        shutdown();
    }

    private void closeRawInput() {
        nativeRawClose();
    }

    public void setLimitRect(final RectF rect) {
        nativeSetLimitRegion(mapToRawTouchRect(rect));
    }

    private float[] mapToRawTouchRect(final RectF rect) {
        RectF dst = EpdController.mapToRawTouchPoint(hostView, rect);
        float[] limit = new float[] {dst.left, dst.top, dst.right, dst.bottom};
        return limit;
    }

    private void shutdown() {
        getSingleThreadPool().shutdown();
        singleThreadPool = null;
    }

    private ExecutorService getSingleThreadPool()   {
        if (singleThreadPool == null) {
            singleThreadPool = Executors.newSingleThreadExecutor();
        }
        return singleThreadPool;
    }

    private void submitJob() {
        getSingleThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    nativeRawReader();
                } catch (Exception e) {
                } finally {
                    closeRawInput();
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public void onTouchPointReceived(int x, int y, int pressure, boolean erasing, int state, long ts) {
//        Log.d(TAG, "x:" + x + "y:" + y + "pressure:" +pressure + "ts:" + ts + "erasing:" + erasing + "state:" + state);
        if (!isReportData()) {
            return;
        }
        this.erasing = erasing;
        if (state == ON_PRESS) {
            pressReceived(x, y, pressure, PEN_SIZE, ts, erasing);
        }else if (state == ON_MOVE) {
            moveReceived(x, y, pressure, PEN_SIZE, ts, erasing);
        }else if (state == ON_RELEASE) {
            releaseReceived(x, y, pressure, PEN_SIZE, ts, erasing);
        }
    }

    private TouchPoint mapToView(final TouchPoint touchPoint) {
        srcPoint[0] = touchPoint.x;
        srcPoint[1] = touchPoint.y;

        EpdController.mapToView(hostView, srcPoint, dstPoint);
        touchPoint.x = dstPoint[0];
        touchPoint.y = dstPoint[1];
        return touchPoint;
    }


    private boolean addToList(final TouchPoint touchPoint, boolean create) {
        if (touchPointList == null) {
            if (!create) {
                return false;
            }
            touchPointList = new TouchPointList(600);
        }

        if (touchPoint != null && touchPointList != null) {
            touchPointList.add(touchPoint);
        }
        return true;
    }

    private boolean isReportData() {
        return reportData;
    }

    private void pressReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        resetPointList();
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        mapToView(touchPoint);
        if (addToList(touchPoint, true)) {
            invokeTouchPointListBegin(erasing);
        }
        // Log.d(TAG, "pressed received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void moveReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        mapToView(touchPoint);
        addToList(touchPoint, isMoveFeedback());
        if (isMoveFeedback() && touchPointList != null && touchPointList.size() > 0) {
            invokeTouchPointListReceived(touchPointList, erasing);
        }
//         Log.d(TAG, "move received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void releaseReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        if (touchPointList != null && touchPointList.size() > 0) {
            invokeTouchPointListReceived(touchPointList, erasing);
        }
        resetPointList();
        invokeTouchPointListEnd(erasing);
        //Log.d(TAG, "release received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void resetPointList() {
        touchPointList = null;
    }

    private void invokeTouchPointListBegin(final boolean erasing) {
        if (rawInputCallback == null || (!isReportData() && !erasing)) {
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (erasing) {
                    rawInputCallback.onBeginErasing();
                } else {
                    rawInputCallback.onBeginRawData();
                }
            }
        });
    }

    private void invokeTouchPointListEnd(final boolean erasing) {
        if (rawInputCallback == null || (!isReportData() && !erasing)) {
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (erasing) {
                    rawInputCallback.onEndErasing();
                } else {
                    rawInputCallback.onEndRawData();
                }
            }
        });
    }

    private void invokeTouchPointListReceived(final TouchPointList touchPointList, final boolean erasing) {
        if (rawInputCallback == null || touchPointList == null || (!isReportData() && !erasing)) {
            return;
        }
        if (isMoveFeedback()) {
            resetPointList();
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (erasing) {
                    rawInputCallback.onEraseTouchPointListReceived(touchPointList);
                } else {
                    rawInputCallback.onRawTouchPointListReceived(null, touchPointList);
                }
            }
        });
    }

    public TouchPointList detachTouchPointList() {
        TouchPointList detachTouchPointList = touchPointList;
        resetPointList();
        return detachTouchPointList;
    }

    public boolean isErasing() {
        return erasing;
    }

    public boolean isMoveFeedback() {
        return moveFeedback;
    }

    public void setMoveFeedback(boolean moveFeedback) {
        this.moveFeedback = moveFeedback;
    }
}
