package com.onyx.kreader.note.bridge;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.math.OnyxMatrix;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.note.NoteManager;

/**
 * Created by zhuzeng on 9/19/16.
 */
public class TouchEventProcessor extends NoteEventProcessorBase {

    private TouchPoint eraserPoint;
    private OnyxMatrix viewToEpdMatrix = null;
    private int viewPosition[] = {0, 0};

    public TouchEventProcessor(final NoteManager p) {
        super(p);
    }

    public void update(final View view, final OnyxMatrix matrix, final Rect rect) {
        view.getLocationOnScreen(viewPosition);
        viewToEpdMatrix = matrix;
        setLimitRect(rect);
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

    private void onDrawingTouchDown(final MotionEvent motionEvent) {
        final TouchPoint touchPoint = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(touchPoint);
        if (hitTest(touchPoint.x, touchPoint.y) == null) {
            return;
        }
        touchPoint.normalize(getLastPageInfo());
        final Shape shape = getNoteManager().createNewShape(getLastPageInfo());
        getNoteManager().onDownMessage(shape);
        shape.onDown(touchPoint, screen);
        if (getCallback() != null) {
            getCallback().onDrawingTouchDown(motionEvent, shape);
        }
    }

    private void onDrawingTouchMove(final MotionEvent motionEvent) {
        final Shape shape = getNoteManager().getCurrentShape();
        if (shape == null) {
            return;
        }
        int n = motionEvent.getHistorySize();
        for(int i = 0; i < n; ++i) {
            final TouchPoint touchPoint = fromHistorical(motionEvent, i);
            final TouchPoint screen = touchPointFromNormalized(touchPoint);
            if (!inLastPage(touchPoint.x, touchPoint.y)) {
                continue;
            }
            touchPoint.normalize(getLastPageInfo());
            shape.onMove(touchPoint, screen);
            if (getCallback() != null) {
                getCallback().onDrawingTouchMove(motionEvent, shape, false);
            }
        }
        final TouchPoint touchPoint = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(touchPoint);
        if (!inLastPage(touchPoint.x, touchPoint.y)) {
            return;
        }
        touchPoint.normalize(getLastPageInfo());
        shape.onMove(touchPoint, screen);
        if (getCallback() != null) {
            getCallback().onDrawingTouchMove(motionEvent, shape, true);
        }
    }

    private void onDrawingTouchUp(final MotionEvent motionEvent) {
        final Shape shape = getNoteManager().getCurrentShape();
        if (shape == null) {
            return;
        }
        final TouchPoint touchPoint = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(touchPoint);
        if (!inLastPage(touchPoint.x, touchPoint.y)) {
            return;
        }
        touchPoint.normalize(getLastPageInfo());
        shape.onUp(touchPoint, screen);
        if (getCallback() != null) {
            getCallback().onDrawingTouchUp(motionEvent, shape);
        }
    }

    private void onErasingTouchDown(final MotionEvent motionEvent) {
        eraserPoint = new TouchPoint(motionEvent);
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
        eraserPoint = new TouchPoint(motionEvent);
        if (getCallback() != null) {
            getCallback().onErasingTouchMove(motionEvent, null, true);
        }
    }

    private void onErasingTouchUp(final MotionEvent motionEvent) {
        eraserPoint = new TouchPoint(motionEvent);
        if (getCallback() != null) {
            getCallback().onErasingTouchUp(motionEvent, null);
        }
        eraserPoint = null;
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

}
