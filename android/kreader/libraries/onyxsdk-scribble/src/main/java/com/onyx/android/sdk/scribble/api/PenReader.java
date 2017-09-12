package com.onyx.android.sdk.scribble.api;

import android.content.Context;
import android.graphics.RectF;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.touch.RawInputReader;
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

    private RawInputReader rawInputReader = new RawInputReader();
    private DeviceConfig deviceConfig;
    private PenReaderCallback penReaderCallback;

    public PenReader(final Context context, final View view) {
        init(context, view);
    }

    private void init(final Context context, final View view) {
        deviceConfig = DeviceConfig.sharedInstance(context, "note");
        rawInputReader.setHostView(view);
        rawInputReader.setLimitRect(new RectF(0, 0, getTouchHeight(), getTouchWidth()));
    }

    public void start() {
        rawInputReader.start();
    }

    public void resume() {
        rawInputReader.resume();
    }

    public void pause() {
        rawInputReader.pause();
    }

    public void stop() {
        rawInputReader.quit();
    }

    public void setPenReaderCallback(final PenReaderCallback callback) {
        this.penReaderCallback = callback;
        rawInputReader.setRawInputCallback(new RawInputReader.RawInputCallback() {
            @Override
            public void onBeginRawData(boolean shortcut, TouchPoint point) {
                penReaderCallback.onBeginRawData();
            }

            @Override
            public void onRawTouchPointMoveReceived(TouchPoint point) {

            }

            @Override
            public void onRawTouchPointListReceived(TouchPointList pointList) {
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
