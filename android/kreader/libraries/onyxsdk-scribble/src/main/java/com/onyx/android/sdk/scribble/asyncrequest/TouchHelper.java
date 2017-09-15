package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.scribble.asyncrequest.event.BeginErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingTouchEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawErasePointsReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.touch.RawInputReader;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by lxm on 2017/8/15.
 */

public class TouchHelper {
    private static final String TAG = TouchHelper.class.getSimpleName();

    private class ReaderCallback implements TouchReader.TouchInputCallback, RawInputReader.RawInputCallback {

        private TouchPointList erasePoints;

        @Override
        public void onErasingTouchEvent(MotionEvent event) {
            eventBus.post(new ErasingTouchEvent(event));
        }

        @Override
        public void onDrawingTouchEvent(MotionEvent event) {
            eventBus.post(new DrawingTouchEvent(event));
        }

        @Override
        public void onBeginRawData(boolean shortcutDrawing, TouchPoint point) {
            eventBus.post(new BeginRawDataEvent());
        }

        @Override
        public void onEndRawData(boolean outLimitRegion, TouchPoint point) {

        }

        @Override
        public void onRawTouchPointMoveReceived(TouchPoint point) {

        }

        @Override
        public void onRawTouchPointListReceived(TouchPointList pointList) {
            eventBus.post(new RawTouchPointListReceivedEvent(pointList));
        }

        @Override
        public void onBeginErasing(boolean shortcutErasing, TouchPoint point) {
            erasePoints = new TouchPointList();
            eventBus.post(new BeginErasingEvent());
        }

        @Override
        public void onEndErasing(boolean outLimitRegion, TouchPoint point) {
            eventBus.post(new RawErasePointsReceivedEvent(erasePoints));
            erasePoints = null;
        }

        @Override
        public void onEraseTouchPointMoveReceived(TouchPoint point) {
            erasePoints.add(point);
        }

        @Override
        public void onEraseTouchPointListReceived(TouchPointList pointList) {
            eventBus.post(new ErasingEvent(null, false));
        }
    }

    private EpdPenManager epdPenManager;
    private TouchReader touchReader;
    private RawInputManager rawInputManager;

    private ReaderCallback callback = new ReaderCallback();

    private EventBus eventBus;
    private Rect customLimitRect;

    public TouchHelper(EventBus eventBus) {
        this.eventBus = eventBus;

        getTouchReader().setTouchInputCallback(callback);
        getRawInputManager().setRawInputCallback(callback);
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

    public void setUseRawInput(boolean use) {
        getRawInputManager().setUseRawInput(use);
        getTouchReader().setUseRawInput(use);
    }

    private void setupEpdPenManager(final View view) {
        getEpdPenManager().setHostView(view);
    }

    private void setupTouchReader(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                getTouchReader().processTouchEvent(motionEvent);
                return true;
            }
        });

        if (customLimitRect == null) {
            Rect limitRect = new Rect();
            view.getLocalVisibleRect(limitRect);
            getTouchReader().setLimitRect(limitRect);
        }else {
            getTouchReader().setLimitRect(customLimitRect);
        }
    }

    private void setupRawInputManager(final View view) {
        getRawInputManager()
                .setHostView(view)
                .setLimitRect(view);
    }

    public boolean checkTouchPoint(final TouchPoint touchPoint) {
        return getTouchReader().checkTouchPoint(touchPoint);
    }

    public TouchReader getTouchReader() {
        if (touchReader == null) {
            touchReader = new TouchReader();
        }
        return touchReader;
    }

    public RawInputManager getRawInputManager() {
        if (rawInputManager == null) {
            rawInputManager = new RawInputManager(eventBus);
        }
        return rawInputManager;
    }

    public void setInUserErasing(boolean inUserErasing) {
        getTouchReader().setInUserErasing(inUserErasing);
    }

    public void setRenderByFramework(boolean renderByFramework) {
        getTouchReader().setRenderByFramework(renderByFramework);
    }

    public void startRawDrawing() {
        getRawInputManager().startRawInputReader();
        getEpdPenManager().startDrawing();
    }

    public void pauseRawDrawing() {
        getRawInputManager().pauseRawInputReader();
        getEpdPenManager().pauseDrawing();
    }

    public void resumeRawDrawing() {
        getRawInputManager().resumeRawInputReader();
        getEpdPenManager().resumeDrawing();
    }

    public void quitRawDrawing() {
        getRawInputManager().quitRawInputReader();
        getEpdPenManager().quitDrawing();
    }

    public boolean checkShapesOutOfRange(List<Shape> shapes) {
        return getTouchReader().checkShapesOutOfRange(shapes);
    }

    public void quit() {
        pauseRawDrawing();
        quitRawDrawing();
    }

    // TODO find a better place to place this method
    public TouchPoint getEraserPoint() {
        return null;
    }

    public void setCustomLimitRect(View view, Rect rect) {
        setCustomLimitRect(view, rect, null);
    }

    public void setCustomLimitRect(View view, Rect limitRect, List<Rect> excludeRectList) {
        customLimitRect = limitRect;
        getTouchReader().setLimitRect(limitRect);
        getRawInputManager().setCustomLimitRect(view, limitRect, excludeRectList);
    }


    public void setStrokeWidth(float w) {
        getRawInputManager().setStrokeWidth(w);
    }
}
