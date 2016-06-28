package com.onyx.android.sdk.scribble.data;

import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceView;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

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

    public static abstract class InputCallback {

        // when received pen down or stylus button
        public abstract void onBeginHandWriting();

        // when pen released.
        public abstract void onNewTouchPointListReceived(final TouchPointList pointList);

        // caller should render the page here.
        public abstract void onBeginErasing();

        // caller should draw erase indicator
        public abstract void onErasing(final TouchPoint touchPoint);

        // caller should do hit test in current page, remove shapes hit-tested.
        public abstract void onEraseTouchPointListReceived(final TouchPointList pointList);

    }

    private volatile int px, py, pressure;
    private volatile boolean erasing = false;
    private volatile boolean lastErasing = false;
    private volatile boolean pressed = false;
    private volatile boolean lastPressed = false;
    private volatile boolean stop = false;
    private String systemPath = "/dev/input/event1";
    private volatile Matrix screenMatrix;
    private volatile Matrix viewMatrix;
    private volatile float[] srcPoint = new float[2];
    private volatile float[] dstPoint = new float[2];
    private volatile TouchPointList touchPointList;
    private InputCallback inputCallback;
    private Handler handler = new Handler(Looper.getMainLooper());
    private volatile SurfaceView parentView;
    private ExecutorService singleThreadPool = null;

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

    public void setParentView(final SurfaceView view) {
        parentView = view;
    }

    public void setInputCallback(final InputCallback callback) {
        inputCallback = callback;
    }

    public void start() {
        stop = false;
        clearInternalState();
        EpdController.setScreenHandWritingPenState(parentView, 1);
        submitJob(parentView);
    }

    public void stop() {
        stop = true;
        clearInternalState();
        EpdController.setScreenHandWritingPenState(parentView, 0);
    }

    private void clearInternalState() {
        pressed = false;
        lastErasing = false;
        lastPressed = false;
    }

    private ExecutorService getSingleThreadPool()   {
        if (singleThreadPool == null) {
            singleThreadPool = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setPriority(Thread.MAX_PRIORITY);
                    return t;
                }
            });
        }
        return singleThreadPool;
    }

    private void submitJob(final SurfaceView view) {
        getSingleThreadPool().submit(new Runnable() {
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
            if (code ==  BTN_TOUCH)  {
                erasing = false;
                pressed = value > 0;
                lastPressed = false;
            } else if (code == BTN_TOOL_PENCIL || code == BTN_TOOL_PEN) {
                erasing = false;
            } else if (code == BTN_TOOL_RUBBER) {
                erasing = true;
                pressed = value > 0;
                lastPressed = false;
            }
            onModeChanged();
        }
    }

    private void onModeChanged() {
        if (lastErasing == erasing) {
            return;
        }
        Log.d(TAG, "on erasing state changed last: " + lastErasing + " now: " + erasing);
        if (!lastErasing && erasing) {
            onErasing();
        } else if (lastErasing && !erasing) {
            onDrawing();
        }
        lastErasing = erasing;
    }

    private void resetToNormalState() {
        EpdController.setScreenHandWritingPenState(parentView, 0);
    }

    private void onErasing() {
        //EpdController.setScreenHandWritingPenState(parentView, 2);
    }

    private void onDrawing() {
        //EpdController.setScreenHandWritingPenState(parentView, 1);
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

        if (touchPoint.x <= 0 || touchPoint.x >= 1 || touchPoint.y <= 0 || touchPoint.y >= 1) {
            Log.e(TAG, "Ignore point: " + touchPoint.x + " " + touchPoint.y);
            return false;
        }

        if (touchPoint != null && touchPointList != null) {
            touchPointList.add(touchPoint);
        }
        return true;
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
        if (addToList(touchPoint, false)) {
            if (erasing) {
                invokeCallbackErasing(touchPoint);
            }
        }
        // Log.d(TAG, "move received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void releaseReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        if (touchPointList != null && touchPointList.size() > 0) {
            invokeTouchPointListFinished(touchPointList, erasing);
        }
        resetPointList();
        Log.d(TAG, "release received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void resetPointList() {
        touchPointList = null;
    }


    private void invokeTouchPointListBegin(final boolean erasing) {
        if (inputCallback == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (erasing) {
                    inputCallback.onBeginErasing();
                } else {
                    inputCallback.onBeginHandWriting();
                }
            }
        });
    }

    private void invokeCallbackErasing(final TouchPoint touchPoint) {
        if (inputCallback == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                inputCallback.onErasing(touchPoint);
            }
        });
    }

    private void invokeTouchPointListFinished(final TouchPointList touchPointList, final boolean erasing) {
        if (inputCallback == null || touchPointList == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (erasing) {
                    inputCallback.onEraseTouchPointListReceived(touchPointList);
                } else {
                    inputCallback.onNewTouchPointListReceived(touchPointList);
                }
            }
        });
    }





}
