package com.onyx.edu.reader.note.bridge;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.math.OnyxMatrix;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.edu.reader.note.NoteManager;

import java.util.List;

/**
 * Created by zhuzeng on 9/19/16.
 */
public class TouchEventProcessor extends NoteEventProcessorBase {

    private TouchPoint eraserPoint;
    private TouchPointList historyErasingPoints = new TouchPointList();
    private OnyxMatrix viewToEpdMatrix = null;
    private int viewPosition[] = {0, 0};

    public TouchEventProcessor(final NoteManager p) {
        super(p);
    }

    public void update(final View view, final OnyxMatrix matrix, final Rect rect, final List<RectF> excludeRect) {
        view.getLocationOnScreen(viewPosition);
        viewToEpdMatrix = matrix;
        setLimitRect(rect);
        addExcludeRect(excludeRect);
    }

    public boolean onTouchEventDrawing(final MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            onDrawingTouchDown(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            onDrawingTouchMove(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            onDrawingTouchUp(motionEvent);
        }
        return true;
    }

    public boolean onTouchEventErasing(final MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            onErasingTouchDown(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            onErasingTouchMove(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            onErasingTouchUp(motionEvent);
        }
        return true;
    }

    public boolean onTouchEventSelecting(final MotionEvent motionEvent) {
        return true;
    }

    private void onDrawingTouchDown(final MotionEvent motionEvent) {
        final TouchPoint touchPoint = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(touchPoint);
        if (!checkTouchPoint(touchPoint, screen)) {
            return;
        }
        final Shape shape = getNoteManager().collectPoint(getLastPageInfo(), touchPoint, screen, true, false);
        if (getCallback() != null) {
            getCallback().onDrawingTouchDown(motionEvent, shape);
        }
    }

    private void onDrawingTouchMove(final MotionEvent motionEvent) {
        int n = motionEvent.getHistorySize();
        for(int i = 0; i < n; ++i) {
            final TouchPoint touchPoint = fromHistorical(motionEvent, i);
            final TouchPoint screen = touchPointFromNormalized(touchPoint);
            if (!checkTouchPoint(touchPoint, screen)) {
                continue;
            }
            final Shape shape = getNoteManager().collectPoint(getLastPageInfo(), touchPoint, screen, true, false);
            if (getCallback() != null) {
                getCallback().onDrawingTouchMove(motionEvent, shape, false);
            }
        }
        final TouchPoint touchPoint = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(touchPoint);
        if (!checkTouchPoint(touchPoint, screen)) {
            return;
        }
        final Shape shape = getNoteManager().collectPoint(getLastPageInfo(), touchPoint, screen, true, false);
        if (getCallback() != null) {
            getCallback().onDrawingTouchMove(motionEvent, shape, true);
        }
    }

    private void onDrawingTouchUp(final MotionEvent motionEvent) {
        final TouchPoint touchPoint = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(touchPoint);
        if (!checkTouchPoint(touchPoint, screen)) {
            return;
        }
        final Shape shape = getNoteManager().collectPoint(getLastPageInfo(), touchPoint, screen, true, true);
        if (getCallback() != null) {
            getCallback().onDrawingTouchUp(motionEvent, shape);
        }
    }

    private boolean checkTouchPoint(final TouchPoint touchPoint, final TouchPoint screen) {
        if (hitTest(touchPoint.x, touchPoint.y) == null || !inLimitRect(touchPoint.x, touchPoint.y) || inExcludeRect(touchPoint.x, touchPoint.y)) {
            finishCurrentShape(getLastPageInfo(), touchPoint, screen, false);
            return false;
        }
        return true;
    }

    public boolean inScribbleRect(final TouchPoint touchPoint) {
        return inLimitRect(touchPoint.x, touchPoint.y) && !inExcludeRect(touchPoint.x, touchPoint.y);
    }

    private void onErasingTouchDown(final MotionEvent motionEvent) {
        eraserPoint = new TouchPoint(motionEvent);
        historyErasingPoints = new TouchPointList();
        if (getCallback() != null) {
            getCallback().onErasingTouchDown(motionEvent, null);
        }
    }

    private void onErasingTouchMove(final MotionEvent motionEvent) {
        int n = motionEvent.getHistorySize();
        for(int i = 0; i < n; ++i) {
            eraserPoint = fromHistorical(motionEvent, i);
            if (getCallback() != null) {
                getCallback().onErasingTouchMove(motionEvent, null, false);
            }
        }
        final PageInfo pageInfo = getNoteManager().hitTest(motionEvent.getX(), motionEvent.getY());
        if (pageInfo == null) {
            return;
        }
        eraserPoint = new TouchPoint(motionEvent);
        if (getCallback() != null) {
            getCallback().onErasingTouchMove(motionEvent, historyErasingPoints, true);
        }
        eraserPoint.normalize(pageInfo);
        historyErasingPoints.add(eraserPoint);
    }

    private void onErasingTouchUp(final MotionEvent motionEvent) {
        eraserPoint = null;
        if (getCallback() != null) {
            getCallback().onErasingTouchUp(motionEvent, historyErasingPoints);
        }
    }

    private TouchPoint touchPointFromNormalized(final TouchPoint normalized) {
        final TouchPoint screen = viewToEpdMatrix.mapWithOffset(normalized, viewPosition[0], viewPosition[1]);
        return screen;
    }

    private TouchPoint fromHistorical(final MotionEvent motionEvent, int i) {
        final TouchPoint normalized = new TouchPoint(motionEvent.getHistoricalX(i),
                motionEvent.getHistoricalY(i),
                motionEvent.getHistoricalPressure(i),
                motionEvent.getHistoricalSize(i),
                motionEvent.getHistoricalEventTime(i));
        return normalized;
    }

    public final TouchPoint getEraserPoint() {
        return eraserPoint;
    }

    private void finishCurrentShape(final PageInfo pageInfo, final TouchPoint normal, final TouchPoint screen, boolean create) {
        final Shape shape = getNoteManager().getCurrentShape();
        if (getCallback() != null && shape != null) {
            getCallback().onDrawingTouchUp(null, shape);
        }
        getNoteManager().resetCurrentShape();
        resetLastPageInfo();
    }

}
