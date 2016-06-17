package com.onyx.kreader.scribble.data;

import android.util.Log;

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

    private int px, py, pressure;
    private boolean erasing = false;
    private boolean pressed;
    private boolean lastPressed;
    private volatile boolean stop = false;
    private String systemPath = "/dev/input/event1";

    private TouchPointList touchPointList;

    public static abstract class Callback {

        public abstract void onNewStrokeReceived(final TouchPointList pointList);

        public abstract void onEraseReceived(final TouchPointList pointList);

    }

    public void start() {
        read();
    }

    public void stop() {
        stop = true;
    }

    private void read() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DataInputStream in = new DataInputStream(new FileInputStream(systemPath));
                    byte[] data = new byte[16];
                    long ts;
                    short type, code;
                    int value;
                    while (!stop) {
                        in.readFully(data);
                        ByteBuffer wrapped = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
                        ts = wrapped.getLong();
                        type = wrapped.getShort();
                        code = wrapped.getShort();
                        value = wrapped.getInt();
                        processInputEvent(ts, type, code, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
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

    private void pressReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        touchPointList = new TouchPointList(200);
        touchPointList.add(new TouchPoint(x, y, pressure, size, ts));
        Log.d(TAG, "pressed received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void moveReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        if (touchPointList != null) {
            touchPointList.add(new TouchPoint(x, y, pressure, size, ts));
        }
        Log.d(TAG, "move received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void releaseReceived(int x, int y, int pressure, int size, long ts, boolean erasing) {
        if (touchPointList != null) {
            touchPointList.add(new TouchPoint(x, y, pressure, size, ts));
        }
        invokeCallback(touchPointList, erasing);
        Log.d(TAG, "release received, x: " + x + " y: " + y + " pressure: " + pressure + " ts: " + ts + " erasing: " + erasing);
    }

    private void invokeCallback(final TouchPointList touchPointList, boolean erasing) {

    }

}
