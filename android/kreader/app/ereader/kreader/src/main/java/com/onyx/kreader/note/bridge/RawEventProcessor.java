package com.onyx.kreader.note.bridge;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.touch.RawInputReader;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.event.DFBShapeFinishedEvent;
import com.onyx.kreader.note.event.DFBShapeStartEvent;
import com.onyx.kreader.note.event.RawErasingFinishEvent;
import com.onyx.kreader.note.event.RawErasingStartEvent;

import java.util.List;

/**
 * Created by zhuzeng on 9/19/16.
 */
public class RawEventProcessor extends NoteEventProcessorBase {

    private volatile boolean shortcutDrawing = false;
    private volatile boolean shortcutErasing = false;

    private volatile TouchPointList touchPointList;

    RawInputReader rawInputReader = new RawInputReader();

    private volatile float[] srcPoint = new float[2];
    private volatile float[] dstPoint = new float[4];

    public RawEventProcessor(final NoteManager p) {
        super(p);

        rawInputReader.setRawInputCallback(new RawInputReader.RawInputCallback() {

            @Override
            public void onBeginRawData(boolean shortcut, TouchPoint point) {
                Debug.d(getClass(), "onBeginRawData: " + shortcut);
                shortcutErasing = false;
                shortcutDrawing = shortcut;
                drawingPressReceived(point);
            }

            @Override
            public void onEndRawData(final boolean releaseOutLimitRegion, TouchPoint point) {
                Debug.d(getClass(), "onEndRawData: " + releaseOutLimitRegion);
                shortcutDrawing = false;
                if (!isReportData()) {
                    return;
                }
                drawingReleaseReceived(point);
            }

            @Override
            public void onRawTouchPointMoveReceived(TouchPoint point) {
                if (!isReportData()) {
                    return;
                }

                drawingMoveReceived(point);
            }

            @Override
            public void onRawTouchPointListReceived(TouchPointList pointList) {
            }

            @Override
            public void onBeginErasing(boolean shortcut, TouchPoint point) {
                Debug.d(getClass(), "onBeginErasing: " + shortcut);
                shortcutDrawing = false;
                shortcutErasing = shortcut;
                erasingPressReceived(point);
            }

            @Override
            public void onEndErasing(final boolean releaseOutLimitRegion, TouchPoint point) {
                Debug.d(getClass(), "onEndErasing: " + releaseOutLimitRegion);
                shortcutErasing = false;
                if (!isReportData()) {
                    return;
                }
                erasingReleaseReceived(point);
            }

            @Override
            public void onEraseTouchPointMoveReceived(TouchPoint point) {
                if (!isReportData()) {
                    return;
                }

                erasingMoveReceived(point);
            }

            @Override
            public void onEraseTouchPointListReceived(TouchPointList pointList) {
            }
        });
    }

    public void update(final View view, final Rect rect, final List<RectF> excludeRect) {
        rawInputReader.setHostView(view);
        rawInputReader.setLimitRect(rect);
        rawInputReader.setExcludeRect(RectUtils.toRectList(excludeRect));
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

    public void quit() {
        rawInputReader.quit();
    }

    private boolean addToList(final TouchPoint touchPoint, boolean create) {
        if (touchPointList == null) {
            if (!create) {
                return false;
            }
            touchPointList = new TouchPointList(600);
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
        return getCallback().enableRawEventProcessor();
    }

    private void erasingPressReceived(TouchPoint touchPoint) {
        invokeRawErasingStart();
        final PageInfo pageInfo = hitTest(touchPoint.x, touchPoint.y);
        if (pageInfo == null) {
            invokeRawErasingFinish();
            return;
        }

        touchPoint.normalize(pageInfo);
        addToList(touchPoint, true);
    }

    private void erasingMoveReceived(TouchPoint touchPoint) {
        final PageInfo pageInfo = hitTest(touchPoint.x, touchPoint.y);
        if (pageInfo == null) {
            invokeRawErasingFinish();
            return;
        }

        touchPoint.normalize(pageInfo);
        addToList(touchPoint, true);
    }

    private void erasingReleaseReceived(TouchPoint touchPoint) {
        shortcutDrawing = false;
        shortcutErasing = false;

        if (touchPoint == null) {
            invokeRawErasingFinish();
            return;
        }

        final PageInfo pageInfo = hitTest(touchPoint.x, touchPoint.y);
        if (pageInfo == null) {
            invokeRawErasingFinish();
            return;
        }

        touchPoint.normalize(pageInfo);
        addToList(touchPoint, true);
        invokeRawErasingFinish();
    }

    private void drawingPressReceived(TouchPoint touchPoint) {
        invokeDFBShapeStart();
        final PageInfo pageInfo = hitTest(touchPoint.x, touchPoint.y);
        if (pageInfo == null) {
            finishCurrentShape();
            return;
        }

        collectShapePoint(pageInfo, touchPoint, false);
    }

    private void drawingMoveReceived(TouchPoint touchPoint) {
        final PageInfo pageInfo = hitTest(touchPoint.x, touchPoint.y);
        if (pageInfo == null) {
            finishCurrentShape();
            return;
        }

        collectShapePoint(pageInfo, touchPoint, false);
    }

    private void drawingReleaseReceived(TouchPoint touchPoint) {
        if (touchPoint == null) {
            finishCurrentShape();
            return;
        }

        final PageInfo pageInfo = hitTest(touchPoint.x, touchPoint.y);
        if (pageInfo == null) {
            finishCurrentShape();
            return;
        }

        collectShapePoint(pageInfo, touchPoint, true);

        finishCurrentShape();
    }

    private void collectShapePoint(PageInfo page, TouchPoint touchPoint, boolean finished) {
        srcPoint[0] = touchPoint.x;
        srcPoint[1] = touchPoint.y;
        EpdController.mapToEpd(rawInputReader.getHostView(), srcPoint, dstPoint);
        final TouchPoint screenTouch = new TouchPoint(dstPoint[0], dstPoint[1],
                touchPoint.getPressure(), touchPoint.getSize(),
                touchPoint.getTimestamp());

        getNoteManager().collectPoint(page, touchPoint, screenTouch, true, false);
    }

    private void finishCurrentShape() {
        final Shape shape = getNoteManager().getCurrentShape();
        resetLastPageInfo();
        invokeDFBShapeFinished(shape);
        getNoteManager().resetCurrentShape();
    }

    private void invokeRawErasingStart() {
        getNoteManager().getParent().getEventBus().post(new RawErasingStartEvent());
    }

    private void invokeRawErasingFinish() {
        final TouchPointList list = touchPointList;
        getNoteManager().getParent().getEventBus().post(new RawErasingFinishEvent(list));

    }

    private void invokeDFBShapeStart() {
        final boolean shortcut = shortcutDrawing;
        if (shortcut) {
            getCallback().enableTouchInput(false);
        }
        getNoteManager().getParent().getEventBus().post(new DFBShapeStartEvent(shortcut));

    }

    private void invokeDFBShapeFinished(final Shape shape) {
        final boolean shortcut = shortcutDrawing;
        shortcutDrawing = false;
        shortcutErasing = false;
        if (shape == null) {
            return;
        }
        getNoteManager().getParent().getEventBus().post(new DFBShapeFinishedEvent(shape, shortcut));
    }

}

