package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.EndRawErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.EndRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingTouchEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawErasePointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawErasePointMoveReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawTouchPointMoveReceivedEvent;
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
            eventBus.post(new BeginRawDataEvent(shortcutDrawing, point));
        }

        @Override
        public void onEndRawData(boolean outLimitRegion, TouchPoint point) {
            eventBus.post(new EndRawDataEvent(outLimitRegion, point));
        }

        @Override
        public void onRawTouchPointMoveReceived(TouchPoint point) {
            eventBus.post(new RawTouchPointMoveReceivedEvent(point));
        }

        @Override
        public void onRawTouchPointListReceived(TouchPointList pointList) {
            eventBus.post(new RawTouchPointListReceivedEvent(pointList));
        }

        @Override
        public void onBeginErasing(boolean shortcutErasing, TouchPoint point) {
            eventBus.post(new BeginRawErasingEvent(shortcutErasing, point));
        }

        @Override
        public void onEndErasing(boolean outLimitRegion, TouchPoint point) {
            eventBus.post(new EndRawErasingEvent(outLimitRegion, point));
        }

        @Override
        public void onEraseTouchPointMoveReceived(TouchPoint point) {
            eventBus.post(new RawErasePointMoveReceivedEvent(point));
        }

        @Override
        public void onEraseTouchPointListReceived(TouchPointList pointList) {
            eventBus.post(new RawErasePointListReceivedEvent(pointList));
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

    public TouchHelper setup(View view) {
        setupTouchReader(view);
        setupRawInputManager(view);
        setupEpdPenManager(view);
        return this;
    }

    public TouchHelper setUseRawInput(boolean use) {
        getRawInputManager().setUseRawInput(use);
        getTouchReader().setUseRawInput(use);
        return this;
    }

    private void setupEpdPenManager(final View view) {
        getEpdPenManager().setHostView(view);
    }

    private void setupTouchReader(final View view) {
        if (customLimitRect == null) {
            Rect limitRect = new Rect();
            view.getLocalVisibleRect(limitRect);
            getTouchReader().setLimitRect(limitRect);
        }else {
            getTouchReader().setLimitRect(customLimitRect);
        }
    }

    public boolean onTouchEvent(final MotionEvent motionEvent) {
        getTouchReader().processTouchEvent(motionEvent);
        return true;
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
            rawInputManager = new RawInputManager();
        }
        return rawInputManager;
    }

    public TouchHelper setInUserErasing(boolean inUserErasing) {
        getTouchReader().setInUserErasing(inUserErasing);
        return this;
    }

    public TouchHelper setRenderByFramework(boolean renderByFramework) {
        getTouchReader().setRenderByFramework(renderByFramework);
        return this;
    }

    public TouchHelper startRawDrawing() {
        getRawInputManager().startRawInputReader();
        getEpdPenManager().startDrawing();
        return this;
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

    public void setCustomLimitRect(Rect rect) {
        setCustomLimitRect(rect, null);
    }

    public TouchHelper setCustomLimitRect(Rect limitRect, List<Rect> excludeRectList) {
        customLimitRect = limitRect;
        getTouchReader().setLimitRect(limitRect);
        getRawInputManager().setLimitRect(limitRect, excludeRectList);
        return this;
    }

    public TouchHelper setStrokeWidth(float w) {
        getRawInputManager().setStrokeWidth(w);
        return this;
    }

    public Rect getRelativeRect(final View parentView, final View childView) {
        int [] parent = new int[2];
        int [] child = new int[2];
        parentView.getLocationOnScreen(parent);
        childView.getLocationOnScreen(child);
        Rect rect = new Rect();
        childView.getLocalVisibleRect(rect);
        rect.offset(child[0] - parent[0], child[1] - parent[1]);
        return rect;
    }
}
