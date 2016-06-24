package com.onyx.android.sdk.scribble.data;

import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by zhuzeng on 6/17/16.
 */
public class RawInputReader {

    private static final String TAG = RawInputReader.class.getSimpleName();
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

    public void setScreenMatrix(final Matrix sm) {
        screenMatrix = sm;
    }

    public void setViewMatrix(final Matrix vm) {
        viewMatrix = vm;
    }

    public void setInputCallback(final InputCallback callback) {
        inputCallback = callback;
    }

    public void start() {
        startThread();
    }

    public void stop() {
        stop = true;
    }

    private void startThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    detectInputDevicePath();
                    readLoop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void readLoop() throws Exception {
        DataInputStream in = new DataInputStream(new FileInputStream(systemPath));
        byte[] data = new byte[16];
        while (!stop) {
            in.readFully(data);
            ByteBuffer wrapped = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            processInputEvent(wrapped.getLong(), wrapped.getShort(), wrapped.getShort(), wrapped.getInt());
        }
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
        }
    }

    /**
     * Use screen matrix to map from touch device to screen
     * Use view matrix to map from screen to view.
     * finally we get points inside view. we may need the page matrix
     * to map points from view to page.
     * @param x
     * @param y
     * @param pressure
     * @param size
     * @param ts
     * @return
     */
    private TouchPoint mapPoint(int x, int y, int pressure, int size, long ts) {
        dstPoint[0] = x;
        dstPoint[1] = y;
        if (screenMatrix != null) {
            srcPoint[0] = x;
            srcPoint[1] = y;
            screenMatrix.mapPoints(dstPoint, srcPoint);
        }
        if (viewMatrix != null) {
            srcPoint[0] = dstPoint[0];
            srcPoint[1] = dstPoint[1];
            viewMatrix.mapPoints(dstPoint, srcPoint);
        }
        TouchPoint touchPoint = new TouchPoint(dstPoint[0], dstPoint[1], pressure, size, ts);
        return touchPoint;
    }

    private void pressReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        touchPointList = new TouchPointList(600);
        touchPointList.add(mapPoint(x, y, pressure, size, ts));
        Log.d(TAG, "pressed received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void moveReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        if (touchPointList != null) {
            touchPointList.add(mapPoint(x, y, pressure, size, ts));
        }
        Log.d(TAG, "move received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void releaseReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        if (touchPointList != null) {
            touchPointList.add(mapPoint(x, y, pressure, size, ts));
        }
        invokeCallback(touchPointList, erasing);
        Log.d(TAG, "release received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
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
