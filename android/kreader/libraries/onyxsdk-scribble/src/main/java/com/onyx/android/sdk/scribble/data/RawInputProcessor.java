package com.onyx.android.sdk.scribble.data;

import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Created by zhuzeng on 6/17/16.
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

    public static abstract class InputCallback {

        public abstract void onBeginHandWriting();

        public abstract void onNewStrokeReceived(final TouchPointList pointList);

        public abstract void onBeginErase();

        public abstract void onEraseReceived(final TouchPointList pointList);

    }

    private int px, py, pressure;
    private boolean erasing = false;
    private boolean lastErasing = false;
    private boolean pressed;
    private boolean lastPressed;
    private volatile boolean stop = false;
    private String systemPath = "/dev/input/event1";
    private Matrix screenMatrix;
    private Matrix viewMatrix;
    private float[] srcPoint = new float[2];
    private float[] dstPoint = new float[2];
    private volatile TouchPointList touchPointList;
    private InputCallback inputCallback;
    private Handler handler = new Handler(Looper.getMainLooper());
    private volatile View parentView;

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

    public void setInputCallback(final InputCallback callback) {
        inputCallback = callback;
    }

    public void start(final View view) {
        parentView = view;
        startThread(parentView);
    }

    public void stop() {
        stop = true;
        resetToNormalState();
    }

    private void startThread(final View view) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    detectInputDevicePath();
                    readLoop(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void readLoop(final View view) throws Exception {
        onDrawing();
        DataInputStream in = new DataInputStream(new FileInputStream(systemPath));
        byte[] data = new byte[16];
        while (!stop) {
            in.readFully(data);
            ByteBuffer wrapped = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            processInputEvent(wrapped.getLong(), wrapped.getShort(), wrapped.getShort(), wrapped.getInt());
        }
        resetToNormalState();
    }

    private void detectInputDevicePath() {
        final int DEVICE_MAX = 3;
        String last = systemPath;
        for(int i = 1; i < DEVICE_MAX; ++i) {
            String path = String.format("/dev/input/event%d", i);
            if (FileUtils.fileExist(path)) {
                last = path;
            }
        }
        systemPath = last;
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
            if (code ==  BTN_TOUCH || code == BTN_TOOL_PENCIL || code == BTN_TOOL_PEN)  {
                erasing = false;
                pressed = value > 0;
                lastPressed = false;
            } else if (code == BTN_TOOL_RUBBER) {
                erasing = true;
                pressed = pressure > 0;
                lastPressed = false;
            }
            onModeChanged();
        }
    }

    private void onModeChanged() {
        if (lastErasing == erasing) {
            return;
        }
        if (!lastErasing && erasing) {
            resetToNormalState();
        } else if (lastErasing && !erasing) {
            onDrawing();
        }
        lastErasing = erasing;
    }

    private void resetToNormalState() {
        EpdController.enablePost(parentView, 1);
        EpdController.setScreenHandWritingPenState(parentView, 0);
    }

    private void onDrawing() {
        EpdController.enablePost(parentView, 0);
        EpdController.setScreenHandWritingPenState(parentView, 1);
    }

    /**
     * Use screen matrix to map from touch device to screen
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

    private TouchPoint mapScreenPointToPage(final TouchPoint touchPoint) {
        dstPoint[0] = touchPoint.x;
        dstPoint[1] = touchPoint.y;
        if (viewMatrix != null) {
            srcPoint[0] = dstPoint[0];
            srcPoint[1] = dstPoint[1];
            viewMatrix.mapPoints(dstPoint, srcPoint);
        }
        touchPoint.x = dstPoint[0];
        touchPoint.y = dstPoint[1];
        return touchPoint;
    }

    private void addToList(final TouchPoint touchPoint, boolean create) {
        if (touchPointList == null) {
            if (!create) {
                return;
            }
            touchPointList = new TouchPointList(600);
        }
        if (touchPoint != null && touchPointList != null) {
            touchPointList.add(touchPoint);
        }
    }

    private void pressReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        mapInputToScreenPoint(touchPoint);
        EpdController.moveTo(touchPoint.x, touchPoint.y, 7.0f);
        mapScreenPointToPage(touchPoint);
        addToList(touchPoint, true);
        Log.d(TAG, "pressed received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void moveReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        mapInputToScreenPoint(touchPoint);
        EpdController.quadTo(touchPoint.x, touchPoint.y, UpdateMode.DU);
        mapScreenPointToPage(touchPoint);
        addToList(touchPoint, false);
        Log.d(TAG, "move received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void releaseReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        invokeCallback(touchPointList, erasing);
        resetPointList();
        Log.d(TAG, "release received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void resetPointList() {
        touchPointList = null;
    }

    private void invokeCallback(final TouchPointList touchPointList, final boolean erasing) {
        if (inputCallback == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (erasing) {
                } else {
                    inputCallback.onNewStrokeReceived(touchPointList);
                }
            }
        });
    }



}
