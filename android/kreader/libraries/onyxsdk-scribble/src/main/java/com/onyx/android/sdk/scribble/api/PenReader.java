package com.onyx.android.sdk.scribble.api;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.SurfaceView;

import com.onyx.android.sdk.api.device.epd.EpdController;
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

    public PenReader(final Context context, final SurfaceView surfaceView) {
        init(context, surfaceView);
    }

    private void init(final Context context, final SurfaceView surfaceView) {
        rawInputProcessor.setMoveFeedback(true);
        deviceConfig = DeviceConfig.sharedInstance(context, "note");
        rawInputProcessor.setHostView(surfaceView);
        rawInputProcessor.setLimitRect(new Rect(0, 0, (int) getTouchHeight(), (int) getTouchWidth()));
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

            @Override
            public void onEndErasing() {
                penReaderCallback.onEndErasing();
            }

            @Override
            public void onEndRawData() {
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
