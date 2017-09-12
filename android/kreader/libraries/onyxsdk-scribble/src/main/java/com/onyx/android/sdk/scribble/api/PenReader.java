package com.onyx.android.sdk.scribble.api;

import android.content.Context;
import android.graphics.RectF;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.data.TouchPoint;
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

        void onEndRawData();

        void onRawTouchPointListReceived(final TouchPointList pointList);

        void onBeginErasing();

        void onEndErasing();

        void onEraseTouchPointListReceived(final TouchPointList pointList);
    }

    private RawInputProcessor rawInputProcessor = new RawInputProcessor();
    private DeviceConfig deviceConfig;
    private PenReaderCallback penReaderCallback;

    public PenReader(final Context context, final View view) {
        init(context, view);
    }

    private void init(final Context context, final View view) {
        deviceConfig = DeviceConfig.sharedInstance(context, "note");
        rawInputProcessor.setHostView(view);
        rawInputProcessor.setLimitRect(new RectF(0, 0, getTouchHeight(), getTouchWidth()));
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
            public void onBeginRawData(boolean shortcut, TouchPoint point) {
                penReaderCallback.onBeginRawData();
            }

            @Override
            public void onRawTouchPointMoveReceived(Shape shape, TouchPoint point) {

            }

            @Override
            public void onRawTouchPointListReceived(Shape shape, TouchPointList pointList) {
                penReaderCallback.onRawTouchPointListReceived(pointList);
            }

            @Override
            public void onBeginErasing(boolean shortcut, TouchPoint point) {
                penReaderCallback.onBeginErasing();
            }

            @Override
            public void onEraseTouchPointMoveReceived(TouchPoint point) {

            }

            @Override
            public void onEraseTouchPointListReceived(TouchPointList pointList) {
                penReaderCallback.onEraseTouchPointListReceived(pointList);
            }

            @Override
            public void onEndErasing(final boolean releaseOutLimitRegion, TouchPoint point) {
                penReaderCallback.onEndErasing();
            }

            @Override
            public void onEndRawData(final boolean releaseOutLimitRegion, TouchPoint point) {
                penReaderCallback.onEndRawData();
            }
        });
    }

    private float getTouchWidth() {
        float value = EpdController.getTouchWidth();
        if (value <= 0) {
            return deviceConfig.getTouchWidth();
        }
        return value;
    }

    private float getTouchHeight() {
        float value = EpdController.getTouchHeight();
        if (value <= 0) {
            return deviceConfig.getTouchHeight();
        }
        return value;
    }
}
