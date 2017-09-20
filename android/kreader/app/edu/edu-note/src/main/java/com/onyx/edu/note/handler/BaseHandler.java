package com.onyx.edu.note.handler;

import android.support.annotation.CallSuper;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.api.event.BeginRawErasingEvent;
import com.onyx.android.sdk.scribble.api.event.DrawingTouchEvent;
import com.onyx.android.sdk.scribble.api.event.ErasingEvent;
import com.onyx.android.sdk.scribble.api.event.ErasingTouchEvent;
import com.onyx.android.sdk.scribble.api.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.api.event.TouchErasePointsReceivedEvent;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.edu.note.scribble.event.HandlerActivateEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by solskjaer49 on 2017/5/27 12:30.
 */

public abstract class BaseHandler {
    private static final String TAG = BaseHandler.class.getSimpleName();
    protected NoteManager noteManager;

    private Shape currentShape = null;
    private TouchPointList erasePoints;

    public BaseHandler(NoteManager noteManager) {
        this.noteManager = noteManager;
    }

    @CallSuper
    public void onActivate(HandlerArgs args) {
        buildMainMenuIds();
        buildToolBarMenuIds();
        buildSubMenuIds();
        noteManager.post(new HandlerActivateEvent(args.getNoteTitle(), noteManager.getShapeDataInfo().getHumanReadableCurPageIndex(),
                noteManager.getShapeDataInfo().getPageCount()));
    }

    public void onDeactivate() {
    }

    public void close() {
    }

    public abstract List<Integer> buildMainMenuIds();

    public abstract List<Integer> buildToolBarMenuIds();

    public abstract SparseArray<List<Integer>> buildSubMenuIds();

    public abstract void handleSubMenuEvent(int subMenuID);

    public abstract void saveDocument(String uniqueID, String title, boolean closeAfterSave, BaseCallback callback);

    public abstract void prevPage();

    public abstract void nextPage();

    public abstract void addPage();

    public abstract void deletePage();

    public void onRawTouchPointListReceived() {}

    public void onDrawingTouchDown() {}

    public void onDrawingTouchMove() {}

    public void onDrawingTouchUp() {}

    @Subscribe
    public void onRawTouchPointListReceivedEvent(RawTouchPointListReceivedEvent event) {
        Shape shape = createNewShape(noteManager.inSpanLayoutMode(),
                noteManager.getShapeDataInfo().getCurrentShapeType());
        shape.addPoints(event.getTouchPointList());
        noteManager.onNewShape(shape);
        onRawTouchPointListReceived();
    }

    private Shape createNewShape(boolean isSpanTextMode, int type) {
        Shape shape = ShapeFactory.createShape(type);
        shape.setStrokeWidth(noteManager.getShapeDataInfo().getStrokeWidth());
        shape.setColor(noteManager.getShapeDataInfo().getStrokeColor());
        shape.setLayoutType(isSpanTextMode ? ShapeFactory.POSITION_LINE_LAYOUT : ShapeFactory.POSITION_FREE);
        return shape;
    }

    @Subscribe
    public void onDrawingTouchEvent(DrawingTouchEvent event) {
        forwardDrawing(event.getMotionEvent());
    }

    @Subscribe
    public void onErasingTouchEvent(ErasingTouchEvent event) {
        forwardErasing(event.getMotionEvent());
    }

    private boolean forwardDrawing(final MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            onDrawingTouchDown(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            onDrawingTouchMove(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            onDrawingTouchUp(motionEvent);
        }
        return true;
    }

    private void onDrawingTouchDown(final MotionEvent motionEvent) {
        currentShape = createNewShape(noteManager.inSpanLayoutMode(), noteManager.getShapeDataInfo().getCurrentShapeType());
        beforeDownMessage(currentShape);
        noteManager.onNewShape(currentShape);
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        if (!noteManager.getTouchHelper().checkTouchPoint(normalized)) {
            return;
        }
        currentShape.onDown(normalized, screen);
        noteManager.setDrawing(true);
        if (!currentShape.supportDFB()) {
            drawCurrentPage();
        }
        onDrawingTouchDown();
    }

    private void onDrawingTouchMove(final MotionEvent motionEvent) {
        if (currentShape == null) {
            return;
        }
        int n = motionEvent.getHistorySize();
        for(int i = 0; i < n; ++i) {
            final TouchPoint normalized = TouchPoint.fromHistorical(motionEvent, i);
            final TouchPoint screen = touchPointFromNormalized(normalized);
            if (!noteManager.getTouchHelper().checkTouchPoint(normalized)) {
                continue;
            }
            currentShape.onMove(normalized, screen);
        }

        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        if (!noteManager.getTouchHelper().checkTouchPoint(normalized)) {
            return;
        }
        currentShape.onMove(normalized, screen);
        noteManager.setDrawing(true);
        if (!currentShape.supportDFB()) {
            drawCurrentPage();
        }
        onDrawingTouchMove();
    }

    protected void onDrawingTouchUp(final MotionEvent motionEvent) {
        if (currentShape == null) {
            return;
        }
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        if (!noteManager.getTouchHelper().checkTouchPoint(normalized)) {
            return;
        }
        currentShape.onUp(normalized, screen);
        noteManager.setDrawing(false);
        if (!currentShape.supportDFB()) {
            drawCurrentPage();
        }
        onDrawingTouchUp();
    }

    private void beforeDownMessage(final Shape currentShape) {
        if (ShapeFactory.isDFBShape(currentShape.getType())) {
            noteManager.enableScreenPost(false);
        } else {
            noteManager.enableScreenPost(true);
        }
    }

    private TouchPoint touchPointFromNormalized(final TouchPoint normalized) {
        // TODO
        //return viewToEpdMatrix.mapWithOffset(normalized, viewPosition[0], viewPosition[1]);
        return normalized;
    }

    private boolean forwardErasing(final MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            onBeginErasing();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            onErasing(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            onFinishErasing();
        }
        return true;
    }

    private void onBeginErasing() {
        erasePoints = new TouchPointList();
        noteManager.post(new BeginRawErasingEvent(false, null));
    }

    private boolean onErasing(final MotionEvent motionEvent) {
        noteManager.post(new ErasingEvent(new TouchPoint(motionEvent), false));
        if (erasePoints != null) {
            int n = motionEvent.getHistorySize();
            for(int i = 0; i < n; ++i) {
                erasePoints.add(TouchPoint.fromHistorical(motionEvent, i));
            }
            erasePoints.add(new TouchPoint(motionEvent.getX(), motionEvent.getY(), motionEvent.getPressure(), motionEvent.getSize(), motionEvent.getEventTime()));
        }
        return true;
    }

    private void onFinishErasing() {
        noteManager.post(new TouchErasePointsReceivedEvent(erasePoints));
    }

    private void drawCurrentPage() {
        AsyncBaseNoteRequest request = new AsyncBaseNoteRequest();
        noteManager.submitRequest(request, null);
    }
}
