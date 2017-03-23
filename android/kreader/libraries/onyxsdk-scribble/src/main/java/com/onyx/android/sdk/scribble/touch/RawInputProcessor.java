package com.onyx.android.sdk.scribble.touch;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;

import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.DetectInputDeviceUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    private static final String TAG = RawInputProcessor.class.getSimpleName();
    private static final int EV_SYN = 0x00;
    private static final int EV_KEY = 0x01;
    private static final int EV_ABS = 0x03;

    private static final int ABS_X = 0x00;
    private static final int ABS_Y = 0x01;
    private static final int ABS_PRESSURE = 0x18;

    private static final int BTN_TOUCH = 0x14a;
    private static final int BTN_TOOL_PEN = 0x140;
    private static final int BTN_TOOL_RUBBER = 0x141;
    private static final int BTN_TOOL_PENCIL = 0x143;

    private static final int PEN_SIZE = 0;

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

    private volatile int px, py, pressure;
    private volatile boolean erasing = false;
    private volatile boolean lastErasing = false;
    private volatile boolean pressed = false;
    private volatile boolean lastPressed = false;
    private volatile boolean stop = false;
    private volatile boolean reportData = false;
    private volatile boolean moveFeedback = false;
    private String systemPath = "/dev/input/event1";
    private volatile Matrix screenMatrix;
    private volatile Matrix viewMatrix;
    private volatile float[] srcPoint = new float[2];
    private volatile float[] dstPoint = new float[2];
    private volatile TouchPointList touchPointList;
    private RawInputCallback rawInputCallback;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ExecutorService singleThreadPool = null;
    private volatile RectF limitRect = new RectF();
    private volatile DataInputStream dataInputStream;

    /**
     * matrix used to map point from input device to screen display.
     * @param sm
     */
    public void setScreenMatrix(final Matrix sm) {
        screenMatrix = sm;
    }

    /**
     * Matrix used to map point from screen to view with normalized.
     * @param vm
     */
    public void setViewMatrix(final Matrix vm) {
        viewMatrix = vm;
    }

    public void setRawInputCallback(final RawInputCallback callback) {
        rawInputCallback = callback;
    }

    public void start() {
        closeInputDevice();
        stop = false;
        reportData = false;
        clearInternalState();
        submitJob();
    }

    public void resume() {
        reportData = true;
    }

    public void pause() {
        reportData = false;
    }

    public void quit() {
        closeInputDevice();
        reportData = false;
        stop = true;
        clearInternalState();
        shutdown();
    }

    private void closeInputDevice() {
        FileUtils.closeQuietly(dataInputStream);
        dataInputStream = null;
    }

    public void setLimitRect(final Rect rect) {
        limitRect.set(rect);
    }

    private void clearInternalState() {
        pressed = false;
        lastErasing = false;
        lastPressed = false;
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
                    detectInputDevicePath();
                    readLoop();
                } catch (Exception e) {
                } finally {
                    closeInputDevice();
                }
            }
        });
    }

    private void readLoop() throws Exception {
        dataInputStream = new DataInputStream(new FileInputStream(systemPath));
        byte[] data = new byte[16];
        while (!stop) {
            dataInputStream.readFully(data);
            ByteBuffer wrapped = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            if (!stop) {
                processInputEvent(wrapped.getLong(), wrapped.getShort(), wrapped.getShort(), wrapped.getInt());
            }
        }
    }

    private void detectInputDevicePath() {
        String index = DetectInputDeviceUtil.detectInputDevicePath();
        if (StringUtils.isNotBlank(index)) {
            systemPath = String.format("/dev/input/event%s", index);
        }
    }

    private void processInputEvent(long ts, int type, int code, int value) {
        if (type == EV_ABS) {
            if (code == ABS_X) {
                px = value;
            } else if (code == ABS_Y) {
                py = value;
            } else if (code == ABS_PRESSURE) {
                pressure = value;
            }
        } else if (type == EV_SYN) {
            if (pressed) {
                if (!lastPressed) {
                    lastPressed = pressed;
                    pressReceived(px, py, pressure, PEN_SIZE, ts, erasing);
                } else {
                    moveReceived(px, py, pressure, PEN_SIZE, ts, erasing);
                }
            } else {
                releaseReceived(px, py, pressure, PEN_SIZE, ts, erasing);
            }
        } else if (type == EV_KEY) {
            if (code ==  BTN_TOUCH)  {
                erasing = false;
                lastErasing = false;
                pressed = value > 0;
                lastPressed = false;
            } else if (code == BTN_TOOL_PENCIL || code == BTN_TOOL_PEN) {
                erasing = false;
            } else if (code == BTN_TOOL_RUBBER) {
                pressed = value > 0;
                erasing = true;
                lastErasing = true;
            }
        }
    }

    /**
     * Use screen matrix to map from touch device to screen with correct orientation.
     * Use view matrix to map from screen to view.
     * finally we get points inside view. we may need the page matrix
     * to map points from view to page.
     * @param touchPoint
     * @return
     */
    private TouchPoint mapInputToScreenPoint(final TouchPoint touchPoint) {
        dstPoint[0] = touchPoint.x;
        dstPoint[1] = touchPoint.y;
        if (screenMatrix != null) {
            srcPoint[0] = touchPoint.x;
            srcPoint[1] = touchPoint.y;
            screenMatrix.mapPoints(dstPoint, srcPoint);
        }
        touchPoint.x = dstPoint[0];
        touchPoint.y = dstPoint[1];
        return touchPoint;
    }

    /**
     * map points from screen to view.
     * @param touchPoint
     * @return
     */
    private TouchPoint mapScreenPointToPage(final TouchPoint touchPoint) {
        dstPoint[0] = touchPoint.x;
        dstPoint[1] = touchPoint.y;
        if (viewMatrix != null) {
            srcPoint[0] = touchPoint.x;
            srcPoint[1] = touchPoint.y;
            viewMatrix.mapPoints(dstPoint, srcPoint);
        }
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

        if (!limitRect.contains(touchPoint.x, touchPoint.y)) {
            return false;
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
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        mapInputToScreenPoint(touchPoint);
        mapScreenPointToPage(touchPoint);
        if (addToList(touchPoint, true)) {
            invokeTouchPointListBegin(erasing);
        }
        // Log.d(TAG, "pressed received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void moveReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        mapInputToScreenPoint(touchPoint);
        mapScreenPointToPage(touchPoint);
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
