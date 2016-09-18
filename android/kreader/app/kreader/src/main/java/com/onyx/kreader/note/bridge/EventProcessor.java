package com.onyx.kreader.note.bridge;

import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.math.OnyxMatrix;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.kreader.note.data.ReaderShapeFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/18/16.
 * Receive events from touch event or input event reader and send render command to screen
 */
public class EventProcessor {

    public static abstract class InputCallback {

        // when received pen down or stylus button
        public abstract void onBeginRawData();

        // when pen released.
        public abstract void onRawTouchPointListReceived(final Shape shape, final TouchPointList pointList);

        public abstract void onDrawingTouchDown(final MotionEvent motionEvent, final Shape shape);

        public abstract void onDrawingTouchMove(final MotionEvent motionEvent, final Shape shape, boolean last);

        public abstract void onDrawingTouchUp(final MotionEvent motionEvent, final Shape shape);

        // caller should render the page here.
        public abstract void onBeginErasing();

        // caller should draw erase indicator
        public abstract void onErasing(final MotionEvent motionEvent);

        // caller should do hit test in current page, remove shapes hit-tested.
        public abstract void onEraseTouchPointListReceived(final TouchPointList pointList);

    }

    private Shape currentShape = null;
    private OnyxMatrix viewToEpdMatrix = null;
    private OnyxMatrix digitizerToEpdMatrix = null;
    private int viewPosition[] = {0, 0};
    private volatile View view;
    private List<Shape> dirtyStash = new ArrayList<>();
    private volatile NoteDrawingArgs noteDrawingArgs;
    private InputCallback callback;


    public NoteDrawingArgs getNoteDrawingArgs() {
        return noteDrawingArgs;
    }

    public void setNoteDrawingArgs(NoteDrawingArgs noteDrawingArgs) {
        this.noteDrawingArgs = noteDrawingArgs;
    }

    public InputCallback getCallback() {
        return callback;
    }

    public void setCallback(InputCallback callback) {
        this.callback = callback;
    }

    public void update(final View targetView, final OnyxMatrix matrix, final OnyxMatrix digitizer) {
        view = targetView;
        viewToEpdMatrix = matrix;
        digitizerToEpdMatrix = digitizer;
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
        currentShape = createNewShape();
        beforeDownMessage(currentShape);
        dirtyStash.add(currentShape);
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        currentShape.onDown(normalized, screen);
        if (callback != null) {
            callback.onDrawingTouchDown(motionEvent, currentShape);
        }
    }

    private void onDrawingTouchMove(final MotionEvent motionEvent) {
        if (currentShape == null) {
            return;
        }
        int n = motionEvent.getHistorySize();
        for(int i = 0; i < n; ++i) {
            final TouchPoint normalized = fromHistorical(motionEvent, i);
            final TouchPoint screen = touchPointFromNormalized(normalized);;
            currentShape.onMove(normalized, screen);
            if (callback != null) {
                callback.onDrawingTouchMove(motionEvent, currentShape, false);
            }
        }
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);;
        currentShape.onMove(normalized, screen);
        if (callback != null) {
            callback.onDrawingTouchMove(motionEvent, currentShape, true);
        }
    }

    private void onDrawingTouchUp(final MotionEvent motionEvent) {
        if (currentShape == null) {
            return;
        }
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        currentShape.onUp(normalized, screen);
        if (callback != null) {
            callback.onDrawingTouchUp(motionEvent, currentShape);
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

    private void beforeDownMessage(final Shape currentShape) {
        if (ReaderShapeFactory.isDFBShape(currentShape.getType())) {
            enableScreenPost(false);
        } else {
            enableScreenPost(true);
        }
    }

    public void enableScreenPost(boolean enable) {
        if (view != null) {
            EpdController.enablePost(view, enable ? 1 : 0);
        }
    }

    private Shape createNewShape() {
        Shape shape = ShapeFactory.createShape(getNoteDrawingArgs().currentShapeType);
        shape.setStrokeWidth(getNoteDrawingArgs().strokeWidth);
        shape.setColor(getNoteDrawingArgs().strokeColor);
        return shape;
    }

}
