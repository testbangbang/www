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


    private int px, py, pressure;
    private boolean pressed;
    private boolean lastPressed;

    public static abstract class Callback {
        public abstract void onStrokeReceived();

    }

    public void start() {
        read();
    }

    public void stop() {

    }

    private void read() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DataInputStream in = new DataInputStream(new FileInputStream("/dev/input/event1"));
                    byte[] data = new byte[16];
                    short type, code;
                    int value;
                    while (true) {
                        in.readFully(data);
                        ByteBuffer wrapped = ByteBuffer.wrap(data, 8, 8).order(ByteOrder.LITTLE_ENDIAN);
                        type = wrapped.getShort();
                        code = wrapped.getShort();
                        value = wrapped.getInt();
                        processInputEvent(type, code, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void processInputEvent(int type, int code, int value) {
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
                } else {
                }
            }
        } else if (type == EV_KEY) {
            if (code ==  BTN_TOUCH) {
                pressed = value > 0;
                lastPressed = false;
            }
        }
        // check if we need to notify or not.
        Log.d(TAG, "px: " + px + " py: " + py + " pressure: " + pressure + " pressed: " + pressed);
    }
}
