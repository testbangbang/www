package com.onyx.android.sdk.scribble.touch;

import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;

import java.util.List;
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
public class RawInputReader {

    static{
        System.loadLibrary("touch_reader");
    }

    private static final String TAG = RawInputReader.class.getSimpleName();

    private static final int PEN_SIZE = 0;

    private static final int ON_PRESS = 0;
    private static final int ON_MOVE = 1;
    private static final int ON_RELEASE = 2;
    private static final int ON_RELEASE_OUT_LIMIT_REGION = 3;

    private native void nativeRawReader();
    private native void nativeRawClose();
    private native void nativeSetStrokeWidth(float strokeWidth);
    private native void nativeSetLimitRegion(float[] limitRegion);
    private native void nativeSetExcludeRegion(float[] excludeRegion);

    public static abstract class RawInputCallback {

        // when received pen down or stylus button
        public abstract void onBeginRawData(boolean shortcutDrawing, TouchPoint point);

        public abstract void onEndRawData(boolean releaseOutLimitRegion, TouchPoint point);

        // when pen moving.
        public abstract void onRawTouchPointMoveReceived(final TouchPoint point);

        // when pen released.
        public abstract void onRawTouchPointListReceived(final TouchPointList pointList);

        // caller should render the page here.
        public abstract void onBeginErasing(boolean shortcutErasing, TouchPoint point);

        public abstract void onEndErasing(boolean releaseOutLimitRegion, TouchPoint point);

        // when eraser moving
        public abstract void onEraseTouchPointMoveReceived(final TouchPoint point);

        // caller should do hit test in current page, remove shapes hit-tested.
        public abstract void onEraseTouchPointListReceived(final TouchPointList pointList);

    }

    private volatile boolean erasing = false;
    private volatile boolean stop = false;
    private volatile boolean reportData = false;
    private volatile float[] srcPoint = new float[2];
    private volatile float[] dstPoint = new float[2];
    private volatile TouchPointList touchPointList;
    private boolean postCallbackOnUiThread;
    private RawInputCallback rawInputCallback;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ExecutorService singleThreadPool = null;
    private volatile View hostView;

    public void setRawInputCallback(final RawInputCallback callback) {
        setRawInputCallback(callback, true);
    }

    public void setRawInputCallback(final RawInputCallback callback, boolean postCallbackOnUiThread) {
        rawInputCallback = callback;
        this.postCallbackOnUiThread = postCallbackOnUiThread;
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

    public void setExcludeRect(final List<RectF> rectList) {
        nativeSetExcludeRegion(mapToRawTouchRect(rectList));
    }

    private float[] mapToRawTouchRect(final RectF rect) {
        RectF dst = EpdController.mapToRawTouchPoint(hostView, rect);
        float[] limit = new float[] {dst.left, dst.top, dst.right, dst.bottom};
        return limit;
    }

    private float[] mapToRawTouchRect(final List<RectF> rectList) {
        float[] result = new float[rectList.size() * 4];
        for (int i = 0; i < rectList.size(); i++) {
            RectF dst = EpdController.mapToRawTouchPoint(hostView, rectList.get(i));
            int index = 4 * i;
            result[index] = dst.left;
            result[index + 1] = dst.top;
            result[index + 2] = dst.right;
            result[index + 3] = dst.bottom;
        }
        return result;
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
    public void onTouchPointReceived(int x, int y, int pressure, boolean erasing, boolean shortcutDrawing, boolean shortcutErasing, int state, long ts) {
//        Log.d(TAG, "x:" + x + "y:" + y + "pressure:" +pressure + "ts:" + ts + "erasing:" + erasing + "state:" + state);
        if (!isReportData()) {
            return;
        }
        this.erasing = erasing;
        if (state == ON_PRESS) {
            pressReceived(x, y, pressure, PEN_SIZE, ts, erasing, shortcutDrawing, shortcutErasing);
        }else if (state == ON_MOVE) {
            moveReceived(x, y, pressure, PEN_SIZE, ts, erasing);
        }else if (state == ON_RELEASE || state == ON_RELEASE_OUT_LIMIT_REGION) {
            releaseReceived(x, y, pressure, PEN_SIZE, ts, erasing,
                    state == ON_RELEASE_OUT_LIMIT_REGION);
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

    private void pressReceived(int x, int y, int pressure, int size, long ts, boolean erasing, boolean shortcutDrawing, boolean shortcutErasing) {
        resetPointList();
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        mapToView(touchPoint);
        if (addToList(touchPoint, true)) {
            invokeTouchPointListBegin(touchPoint, erasing, shortcutDrawing, shortcutErasing);
        }
        // Log.d(TAG, "pressed received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void moveReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        mapToView(touchPoint);
        addToList(touchPoint, true);

        invokeTouchPointMoveReceived(touchPoint, erasing);
//         Log.d(TAG, "move received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void releaseReceived(int x, int y, int pressure, int size, long ts, boolean erasing, boolean releaseOutLimitRegion) {
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        if (touchPointList != null && touchPointList.size() > 0) {
            invokeTouchPointListReceived(touchPointList, erasing);
        }
        resetPointList();
        invokeTouchPointListEnd(touchPoint, erasing, releaseOutLimitRegion);
        //Log.d(TAG, "release received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void resetPointList() {
        touchPointList = null;
    }

    private void invokeTouchPointListBegin(final TouchPoint point, final boolean erasing, final boolean shortcutDrawing, final boolean shortcutErasing) {
        if (rawInputCallback == null || (!isReportData() && !erasing)) {
            return;
        }

        if (!postCallbackOnUiThread) {
            onTouchPointListBegin(point, erasing, shortcutDrawing, shortcutErasing);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onTouchPointListBegin(point, erasing, shortcutDrawing, shortcutErasing);
                }
            });
        }
    }

    private void onTouchPointListBegin(TouchPoint point, final boolean erasing, final boolean shortcutDrawing, final boolean shortcutErasing) {
        if (erasing) {
            rawInputCallback.onBeginErasing(shortcutErasing, point);
        } else {
            rawInputCallback.onBeginRawData(shortcutDrawing, point);
        }
    }

    private void invokeTouchPointListEnd(final TouchPoint touchPoint, final boolean erasing, final boolean releaseOutLimitRegion) {
        if (rawInputCallback == null || (!isReportData() && !erasing)) {
            return;
        }

        if (!postCallbackOnUiThread) {
            onTouchPointListEnd(touchPoint, erasing, releaseOutLimitRegion);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onTouchPointListEnd(touchPoint, erasing, releaseOutLimitRegion);
                }
            });
        }
    }

    private void onTouchPointListEnd(TouchPoint touchPoint, final boolean erasing, final boolean releaseOutLimitRegion) {
        if (erasing) {
            rawInputCallback.onEndErasing(releaseOutLimitRegion, touchPoint);
        } else {
            rawInputCallback.onEndRawData(releaseOutLimitRegion, touchPoint);
        }
    }

    private void invokeTouchPointMoveReceived(final TouchPoint touchPoint, final boolean erasing) {
        if (rawInputCallback == null || (!isReportData() && !erasing)) {
            return;
        }

        if (!postCallbackOnUiThread) {
            onTouchPointMoveReceived(touchPoint, erasing);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onTouchPointMoveReceived(touchPoint, erasing);
                }
            });
        }
    }

    private void onTouchPointMoveReceived(final TouchPoint touchPoint, final boolean erasing) {
        if (erasing) {
            rawInputCallback.onEraseTouchPointMoveReceived(touchPoint);
        } else {
            rawInputCallback.onRawTouchPointMoveReceived(touchPoint);
        }
    }

    private void invokeTouchPointListReceived(final TouchPointList touchPointList, final boolean erasing) {
        if (rawInputCallback == null || touchPointList == null || (!isReportData() && !erasing)) {
            return;
        }

        if (!postCallbackOnUiThread) {
            onTouchPointListReceived(touchPointList, erasing);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onTouchPointListReceived(touchPointList, erasing);
                }
            });
        }
    }

    private void onTouchPointListReceived(final TouchPointList touchPointList, final boolean erasing) {
        if (erasing) {
            rawInputCallback.onEraseTouchPointListReceived(touchPointList);
        } else {
            rawInputCallback.onRawTouchPointListReceived(touchPointList);
        }
    }

    public TouchPointList detachTouchPointList() {
        TouchPointList detachTouchPointList = touchPointList;
        resetPointList();
        return detachTouchPointList;
    }

    public boolean isErasing() {
        return erasing;
    }

}
