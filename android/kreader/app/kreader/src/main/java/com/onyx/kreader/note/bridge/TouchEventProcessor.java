package com.onyx.kreader.note.bridge;

import android.view.MotionEvent;
import android.view.View;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.math.OnyxMatrix;
import com.onyx.android.sdk.scribble.shape.Shape;

/**
 * Created by zhuzeng on 9/19/16.
 */
public class TouchEventProcessor extends NoteEventProcessorBase {
    private NoteEventProcessorManager parent;
    private OnyxMatrix viewToEpdMatrix = null;
    private int viewPosition[] = {0, 0};


    public TouchEventProcessor(final NoteEventProcessorManager p) {
        parent = p;
    }

    public void update(final View view, final OnyxMatrix matrix) {
        view.getLocationOnScreen(viewPosition);
        viewToEpdMatrix = matrix;
    }

    public final NoteEventProcessorManager getParent() {
        return parent;
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
        return true;
    }

    public boolean onRawInputEvent(final TouchPoint touchPoint) {
        return false;
    }

    private void onDrawingTouchDown(final MotionEvent motionEvent) {
        final Shape shape = getParent().createNewShape();
        getParent().beforeDownMessage(shape);
        getParent().addToStash(shape);
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        shape.onDown(normalized, screen);
        if (parent.getCallback() != null) {
            parent.getCallback().onDrawingTouchDown(motionEvent, shape);
        }
    }

    private void onDrawingTouchMove(final MotionEvent motionEvent) {
        final Shape shape = getParent().getCurrentShape();
        if (shape == null) {
            return;
        }
        int n = motionEvent.getHistorySize();
        for(int i = 0; i < n; ++i) {
            final TouchPoint normalized = fromHistorical(motionEvent, i);
            final TouchPoint screen = touchPointFromNormalized(normalized);;
            shape.onMove(normalized, screen);
            if (getParent().getCallback() != null) {
                getParent().getCallback().onDrawingTouchMove(motionEvent, shape, false);
            }
        }
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);;
        shape.onMove(normalized, screen);
        if (getParent().getCallback() != null) {
            getParent().getCallback().onDrawingTouchMove(motionEvent, shape, true);
        }
    }

    private void onDrawingTouchUp(final MotionEvent motionEvent) {
        final Shape shape = getParent().getCurrentShape();
        if (shape == null) {
            return;
        }
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        shape.onUp(normalized, screen);
        if (getParent().getCallback() != null) {
            getParent().getCallback().onDrawingTouchUp(motionEvent, shape);
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


}
