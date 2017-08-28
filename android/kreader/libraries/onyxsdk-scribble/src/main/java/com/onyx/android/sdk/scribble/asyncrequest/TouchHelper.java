package com.onyx.android.sdk.scribble.asyncrequest;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2017/8/15.
 */

public class TouchHelper {
    private static final String TAG = TouchHelper.class.getSimpleName();

    private EpdPenManager epdPenManager;
    private TouchReader touchReader;
    private RawInputManager rawInputManager;

    private EventBus eventBus;

    public TouchHelper(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EpdPenManager getEpdPenManager() {
        if (epdPenManager == null) {
            epdPenManager = new EpdPenManager();
        }
        return epdPenManager;
    }

    public void setup(View view) {
        setupTouchReader(view);
        setupRawInputManager(view);
        setupEpdPenManager(view);
    }

    private void setupEpdPenManager(final View view) {
        getEpdPenManager().setHostView(view);
        getEpdPenManager().startDrawing();
    }

    private void setupTouchReader(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                getTouchReader().processTouchEvent(motionEvent);
                return true;
            }
        });

        Rect limitRect = new Rect();
        view.getLocalVisibleRect(limitRect);
        getTouchReader().setLimitRect(limitRect);
    }

    private void setupRawInputManager(final View view) {
        getRawInputManager()
                .setHostView(view)
                .setUseRawInput(getDeviceConfig(view.getContext()).useRawInput())
                .setLimitRect(view)
                .startRawInputProcessor();
    }

    public boolean checkTouchPoint(final TouchPoint touchPoint) {
        return getTouchReader().checkTouchPoint(touchPoint);
    }

    public TouchReader getTouchReader() {
        if (touchReader == null) {
            touchReader = new TouchReader(eventBus);
        }
        return touchReader;
    }

    public RawInputManager getRawInputManager() {
        if (rawInputManager == null) {
            rawInputManager = new RawInputManager(eventBus);
        }
        return rawInputManager;
    }

    public void pauseRawDrawing() {
        getRawInputManager().pauseRawDrawing();
        getEpdPenManager().pauseDrawing();
    }

    public void resumeRawDrawing() {
        getRawInputManager().resumeRawDrawing();
        getEpdPenManager().resumeDrawing();
    }

    public void quitRawDrawing() {
        getRawInputManager().quitRawDrawing();
        getEpdPenManager().quitDrawing();
    }

    public void quit() {
        pauseRawDrawing();
        quitRawDrawing();
    }

    private DeviceConfig getDeviceConfig(Context context) {
        return ConfigManager.sharedInstance(context).getDeviceConfig();
    }
}
