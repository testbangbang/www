package com.onyx.kreader.note.bridge;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.SingleThreadExecutor;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.kreader.note.NoteManager;
import com.onyx.android.sdk.utils.DeviceUtils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by zhuzeng on 9/19/16.
 */
public class RawEventProcessor extends NoteEventProcessorBase {

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

    private String inputDevice = "/dev/input/event1";

    private volatile int px, py, pressure;
    private volatile boolean erasing = false;
    private volatile boolean shortcutDrawing = false;
    private volatile boolean shortcutErasing = false;
    private volatile boolean pressed = false;
    private volatile boolean lastPressed = false;
    private volatile boolean stop = false;
    private volatile FileInputStream fileInputStream;
    private volatile DataInputStream dataInputStream;
    private volatile boolean reportData = false;

    private volatile float[] srcPoint = new float[2];
    private volatile float[] dstPoint = new float[4];
    private volatile TouchPointList touchPointList;
    private View hostView;
    private Handler handler = new Handler(Looper.getMainLooper());
    private SingleThreadExecutor singleThreadExecutor;

    public RawEventProcessor(final NoteManager p) {
        super(p);
    }

    public void update(final View view, final Rect rect, final List<RectF> excludeRect) {
        this.hostView = view;
        setLimitRect(rect);
        addExcludeRect(excludeRect);
    }

    public void start() {
        stop = false;
        reportData = true;
        clearInternalState();
        submitJob();
    }

    public void resume() {
        clearInternalState();
        reportData = true;
    }

    public void pause() {
        clearInternalState();
        reportData = false;
    }

    public void quit() {
        stop = true;
        reportData = false;
        closeInputDevice();
        clearInternalState();
        shutdown();
    }

    private void clearInternalState() {
        px = py = 0;
        pressure = 0;
        pressed = false;
        shortcutDrawing = false;
        shortcutErasing = false;
        lastPressed = false;
    }

    private void closeInputDevice() {
        FileUtils.closeQuietly(fileInputStream);
        fileInputStream = null;

        FileUtils.closeQuietly(dataInputStream);
        dataInputStream = null;
    }

    private void shutdown() {
        if (singleThreadExecutor != null) {
            singleThreadExecutor.shutdown();
            singleThreadExecutor = null;
        }
    }

    private ExecutorService getSingleThreadPool()   {
        if (singleThreadExecutor == null) {
            singleThreadExecutor = new SingleThreadExecutor(Thread.MAX_PRIORITY);
        }
        return singleThreadExecutor.get();
    }

    private void submitJob() {
        getSingleThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    detectInputDevicePath();
                    readLoop();
                } catch (Exception e) {
                    Debug.d(RawEventProcessor.class, e.toString());
                } finally {
                    finishCurrentShape();
                    closeInputDevice();
                }
            }
        });
    }

    private void readLoop() throws Exception {
        fileInputStream = new FileInputStream(inputDevice);
        dataInputStream = new DataInputStream(fileInputStream);
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
        inputDevice = DeviceUtils.detectInputDevicePath();
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
            if (pressed && pressure > 0) {
                if (!lastPressed) {
                    pressReceived(px, py, pressure, PEN_SIZE, ts, erasing);
                    lastPressed = true;
                } else {
                    moveReceived(px, py, pressure, PEN_SIZE, ts, erasing);
                }
            }
            if (pressure <= 0 && lastPressed) {
                releaseReceived(px, py, pressure, PEN_SIZE, ts, erasing);
                lastPressed = false;
            }
        } else if (type == EV_KEY) {
            if (code == BTN_TOUCH)  {
                erasing = false;
                lastPressed = pressed;
                pressed = value > 0;
            } else if (code == BTN_TOOL_PENCIL || code == BTN_TOOL_PEN) {
                erasing = false;
                shortcutDrawing = true;
                shortcutErasing = false;
            } else if (code == BTN_TOOL_RUBBER) {
                erasing = true;
                shortcutDrawing = false;
                shortcutErasing = true;
            }
        }
    }

    private void mapRawTouchPoint(final float x, final float y, final TouchPoint screenPoint, final TouchPoint viewPoint) {
        srcPoint[0] = x;
        srcPoint[1] = y;

        EpdController.mapFromRawTouchPoint(hostView, srcPoint, dstPoint);
        if (screenPoint != null) {
            screenPoint.x = dstPoint[0];
            screenPoint.y = dstPoint[1];
        }
        if (viewPoint != null) {
            viewPoint.x = dstPoint[2];
            viewPoint.y = dstPoint[3];
        }
    }

    private boolean addToList(final TouchPoint touchPoint, boolean create) {
        if (touchPointList == null) {
            if (!create) {
                return false;
            }
            touchPointList = new TouchPointList(600);
        }

        if (!inLimitRect(touchPoint.x, touchPoint.y)) {
            return false;
        }

        if (inExcludeRect(touchPoint.x, touchPoint.y)) {
            return false;
        }

        if (touchPoint != null && touchPointList != null) {
            touchPointList.add(touchPoint);
        }
        return true;
    }

    private boolean isReportData() {
        if (shortcutDrawing && getCallback().enableShortcutDrawing()) {
            return true;
        }
        if (shortcutErasing && getCallback().enableShortcutErasing()) {
            return true;
        }
        return reportData && getCallback().enableRawEventProcessor();
    }

    private boolean inErasing() {
        return erasing || shortcutErasing;
    }

    private void pressReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        if (!isReportData()) {
            return;
        }
        if (inErasing()) {
            erasingPressReceived(x, y, pressure, size, ts);
        } else {
            drawingPressReceived(x, y, pressure, size, ts);
        }
    }

    private void moveReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        if (!isReportData()) {
            return;
        }
        if (inErasing()) {
            erasingMoveReceived(x, y, pressure, size, ts);
        } else {
            drawingMoveReceived(x, y, pressure, size, ts);
        }
    }

    private void releaseReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        if (!isReportData()) {
            return;
        }
        if (inErasing()) {
            erasingReleaseReceived(x, y, pressure, size, ts);
        } else {
            drawingReleaseReceived(x, y, pressure, size, ts);
        }
    }

    private void erasingPressReceived(int x, int y, int pressure, int size, long ts) {
        invokeRawErasingStart();
        final PageInfo pageInfo = hitTest(x, y);
        if (pageInfo == null) {
            return;
        }
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        mapRawTouchPoint(x, y, null, touchPoint);
        touchPoint.normalize(pageInfo);
        addToList(touchPoint, true);
    }

    private void erasingMoveReceived(int x, int y, int pressure, int size, long ts) {
        final PageInfo pageInfo = hitTest(x, y);
        if (pageInfo == null) {
            return;
        }
        final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
        mapRawTouchPoint(x, y, null, touchPoint);
        touchPoint.normalize(pageInfo);
        addToList(touchPoint, true);
    }

    private void erasingReleaseReceived(int x, int y, int pressure, int size, long ts) {
        shortcutDrawing = false;
        shortcutErasing = false;
        final PageInfo pageInfo = hitTest(x, y);
        if (pageInfo != null) {
            final TouchPoint touchPoint = new TouchPoint(x, y, pressure, size, ts);
            mapRawTouchPoint(x, y, null, touchPoint);
            touchPoint.normalize(pageInfo);
            addToList(touchPoint, true);
        }
        invokeRawErasingFinish();
    }

    private void drawingPressReceived(int x, int y, int pressure, int size, long ts) {
        invokeDFBShapeStart();
        final TouchPoint screenTouch = new TouchPoint(x, y, pressure, size, ts);
        final TouchPoint viewTouch = new TouchPoint(screenTouch);
        mapRawTouchPoint(x, y, screenTouch, viewTouch);
        if (!checkTouchPoint(viewTouch, screenTouch)) {
            return;
        }
        getNoteManager().collectPoint(getLastPageInfo(), viewTouch, screenTouch, true, false);
    }

    private void drawingMoveReceived(int x, int y, int pressure, int size, long ts) {
        final TouchPoint screenTouch = new TouchPoint(x, y, pressure, size, ts);
        final TouchPoint viewTouch = new TouchPoint(screenTouch);
        mapRawTouchPoint(x, y, screenTouch, viewTouch);
        if (!checkTouchPoint(viewTouch, screenTouch)) {
            return;
        }
        getNoteManager().collectPoint(getLastPageInfo(), viewTouch, screenTouch, true, false);
    }

    private void drawingReleaseReceived(int x, int y, int pressure, int size, long ts) {
        final TouchPoint screenTouch = new TouchPoint(x, y, pressure, size, ts);
        final TouchPoint viewTouch = new TouchPoint(screenTouch);
        mapRawTouchPoint(x, y, screenTouch, viewTouch);
        if (!checkTouchPoint(viewTouch, screenTouch)) {
            return;
        }
        finishCurrentShape();
    }

    private boolean checkTouchPoint(final TouchPoint touchPoint, final TouchPoint screen) {
        if (hitTest(touchPoint.x, touchPoint.y) == null ||
            !inLimitRect(touchPoint.x, touchPoint.y) ||
            inExcludeRect(touchPoint.x, touchPoint.y)) {
            finishCurrentShape();
            return false;
        }
        return true;
    }

    private void finishCurrentShape() {
        final Shape shape = getNoteManager().getCurrentShape();
        resetLastPageInfo();
        invokeDFBShapeFinished(shape);
        getNoteManager().resetCurrentShape();
    }

    private void invokeRawErasingStart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                getCallback().onRawErasingStart();
            }
        });
    }

    private void invokeRawErasingFinish() {
        final TouchPointList list = touchPointList;
        handler.post(new Runnable() {
            @Override
            public void run() {
                getCallback().onRawErasingFinished(list);
            }
        });
    }

    private void invokeDFBShapeStart() {
        final boolean shortcut = shortcutDrawing;
        if (shortcut) {
            getCallback().enableTouchInput(false);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                getCallback().onDFBShapeStart(shortcut);
            }
        });
    }

    private void invokeDFBShapeFinished(final Shape shape) {
        final boolean shortcut = shortcutDrawing;
        shortcutDrawing = false;
        shortcutErasing = false;
        if (shape == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                getCallback().onDFBShapeFinished(shape, shortcut);
            }
        });
    }

}
