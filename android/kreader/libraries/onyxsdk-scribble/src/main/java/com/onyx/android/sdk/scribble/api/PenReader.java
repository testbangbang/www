package com.onyx.android.sdk.scribble.api;

import android.content.Context;
import android.graphics.Matrix;

import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.touch.RawInputProcessor;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;

/**
 * Created by ming on 2017/2/22.
 */

public class PenReader {

    public interface PenReaderCallback {
        void onBeginRawData();

        void onRawTouchPointListReceived(final TouchPointList pointList);

        void onBeginErasing();

        void onEraseTouchPointListReceived(final TouchPointList pointList);
    }

    private RawInputProcessor rawInputProcessor = new RawInputProcessor();
    private DeviceConfig deviceConfig;
    private PenReaderCallback penReaderCallback;

    public PenReader(final Context context) {
        init(context);
    }

    private void init(final Context context) {
        deviceConfig = DeviceConfig.sharedInstance(context, "note");
        rawInputProcessor.setViewMatrix(new Matrix());
        final Matrix screenMatrix = new Matrix();
        screenMatrix.postRotate(deviceConfig.getEpdPostOrientation());
        screenMatrix.postTranslate(deviceConfig.getEpdPostTx(), deviceConfig.getEpdPostTy());
        screenMatrix.preScale(deviceConfig.getEpdWidth() / getTouchWidth(),
                deviceConfig.getEpdHeight() / getTouchHeight());
        rawInputProcessor.setScreenMatrix(screenMatrix);
    }

    public void start() {
        rawInputProcessor.start();
    }

    public void resume() {
        rawInputProcessor.resume();
    }

    public void pause() {
        rawInputProcessor.pause();
    }

    public void stop() {
        rawInputProcessor.quit();
    }

    public void setPenReaderCallback(final PenReaderCallback callback) {
        this.penReaderCallback = callback;
        rawInputProcessor.setRawInputCallback(new RawInputProcessor.RawInputCallback() {
            @Override
            public void onBeginRawData() {
                penReaderCallback.onBeginRawData();
            }

            @Override
            public void onRawTouchPointListReceived(Shape shape, TouchPointList pointList) {
                penReaderCallback.onRawTouchPointListReceived(pointList);
            }

            @Override
            public void onBeginErasing() {
                penReaderCallback.onBeginErasing();
            }

            @Override
            public void onEraseTouchPointListReceived(TouchPointList pointList) {
                penReaderCallback.onEraseTouchPointListReceived(pointList);
            }
        });
    }

    private float getTouchWidth() {
        float value = Device.currentDevice().getTouchWidth();
        if (value <= 0) {
            return deviceConfig.getTouchWidth();
        }
        return value;
    }

    private float getTouchHeight() {
        float value = Device.currentDevice().getTouchHeight();
        if (value <= 0) {
            return deviceConfig.getTouchHeight();
        }
        return value;
    }
}
