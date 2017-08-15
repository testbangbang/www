package com.onyx.android.sdk.scribble.asyncrequest;

import android.view.MotionEvent;

import com.onyx.android.sdk.scribble.asyncrequest.event.BeginErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginShapeSelectEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchDownEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchMoveEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchUpEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.EraseTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ShapeSelectTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ShapeSelectingEvent;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by lxm on 2017/8/15.
 */

public class TouchReader {

    private NoteManager noteManager;
    private Shape currentShape = null;
    private TouchPointList shapeSelectPoints;
    private TouchPointList erasePoints;

    public TouchReader(NoteManager noteManager) {
        this.noteManager = noteManager;
    }

    private boolean isFingerTouch(int toolType) {
        return toolType == MotionEvent.TOOL_TYPE_FINGER;
    }

    private DeviceConfig getDeviceConfig() {
        return noteManager.getDeviceConfig();
    }

    private boolean isSingleTouch() {
        return getDeviceConfig() != null && getDeviceConfig().isSingleTouch();
    }

    private boolean supportBigPen() {
        return getDeviceConfig() != null && getDeviceConfig().supportBigPen();
    }

    private boolean isEnableFingerErasing() {
        return getDeviceConfig() != null && getDeviceConfig().isEnableFingerErasing();
    }

    private boolean useRawInput() {
        return getDeviceConfig() != null && getDeviceConfig().useRawInput();
    }

    private boolean renderByFramework() {
        return ShapeFactory.isDFBShape(noteManager.getDocumentHelper().getCurrentShapeType());
    }


    public boolean processTouchEvent(final MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() > 1) {
            return true;
        }
        int toolType = motionEvent.getToolType(0);
        if (isFingerTouch(toolType) && !isSingleTouch()) {
            return true;
        }

        if ((supportBigPen() && toolType == MotionEvent.TOOL_TYPE_ERASER) || noteManager.inUserErasing()) {
            if (isFingerTouch(toolType)) {
                if (isEnableFingerErasing()) {
                    return forwardErasing(motionEvent);
                }
                return true;
            }
            return forwardErasing(motionEvent);
        }
        if (noteManager.getDocumentHelper().inShapeSelecting()){
            return forwardShapeSelecting(motionEvent);
        }
        if (!(useRawInput() && renderByFramework())) {
            return forwardDrawing(motionEvent);
        }
        return true;
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

    private boolean forwardShapeSelecting(final MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            onBeginShapeSelecting(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            onShapeSelecting(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            onFinishShapeSelecting();
        }
        return true;
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

    private void beforeDownMessage(final Shape currentShape) {
        if (ShapeFactory.isDFBShape(currentShape.getType())) {
            noteManager.getPenManager().enableScreenPost(false);
        } else {
            noteManager.getPenManager().enableScreenPost(true);
        }
    }

    private Shape createNewShape(boolean isSpanTextMode, int type) {
        Shape shape = ShapeFactory.createShape(type);
        shape.setStrokeWidth(noteManager.getDocumentHelper().getStrokeWidth());
        shape.setColor(noteManager.getDocumentHelper().getStrokeColor());
        shape.setLayoutType(isSpanTextMode ? ShapeFactory.POSITION_LINE_LAYOUT : ShapeFactory.POSITION_FREE);
        return shape;
    }

    private void onDrawingTouchDown(final MotionEvent motionEvent) {
        currentShape = createNewShape(noteManager.inSpanScribbleMode(), noteManager.getDocumentHelper().getNoteDrawingArgs().getCurrentShapeType());
        beforeDownMessage(currentShape);
        noteManager.onNewShape(currentShape);
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        if (!checkTouchPoint(normalized)) {
            return;
        }
        currentShape.onDown(normalized, screen);
        noteManager.setDrawing(true);
        EventBus.getDefault().post(new DrawingTouchDownEvent(motionEvent,currentShape));
    }

    private void onDrawingTouchMove(final MotionEvent motionEvent) {
        if (currentShape == null) {
            return;
        }
        int n = motionEvent.getHistorySize();
        for(int i = 0; i < n; ++i) {
            final TouchPoint normalized = fromHistorical(motionEvent, i);
            final TouchPoint screen = touchPointFromNormalized(normalized);
            if (!checkTouchPoint(normalized)) {
                continue;
            }
            currentShape.onMove(normalized, screen);
        }
        EventBus.getDefault().post(new DrawingTouchMoveEvent(motionEvent,currentShape,false));

        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        if (!checkTouchPoint(normalized)) {
            return;
        }
        currentShape.onMove(normalized, screen);
        noteManager.setDrawing(true);
        EventBus.getDefault().post(new DrawingTouchMoveEvent(motionEvent,currentShape,true));
    }

    private void onDrawingTouchUp(final MotionEvent motionEvent) {
        if (currentShape == null) {
            return;
        }
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        if (!checkTouchPoint(normalized)) {
            return;
        }
        currentShape.onUp(normalized, screen);
        noteManager.setDrawing(false);
        EventBus.getDefault().post(new DrawingTouchUpEvent(motionEvent, currentShape));
    }

    private TouchPoint touchPointFromNormalized(final TouchPoint normalized) {
        return viewToEpdMatrix.mapWithOffset(normalized, viewPosition[0], viewPosition[1]);
    }

    private TouchPoint fromHistorical(final MotionEvent motionEvent, int i) {
        return new TouchPoint(motionEvent.getHistoricalX(i),
                motionEvent.getHistoricalY(i),
                motionEvent.getHistoricalPressure(i),
                motionEvent.getHistoricalSize(i),
                motionEvent.getHistoricalEventTime(i));
    }


    private boolean checkTouchPoint(final TouchPoint touchPoint) {
        return softwareLimitRect.contains((int) touchPoint.x, (int) touchPoint.y);
    }

    public boolean checkTouchPointList(final TouchPointList touchPointList) {
        if (touchPointList == null || touchPointList.size() == 0) {
            return false;
        }
        List<TouchPoint> touchPoints = touchPointList.getPoints();
        for (TouchPoint touchPoint : touchPoints) {
            if (!checkTouchPoint(touchPoint)) {
                return false;
            }
        }
        return true;
    }


    private void onBeginErasing() {
        erasePoints = new TouchPointList();
        EventBus.getDefault().post(new BeginErasingEvent());
    }

    private boolean onErasing(final MotionEvent motionEvent) {
        EventBus.getDefault().post(new ErasingEvent(motionEvent));
        if (erasePoints != null) {
            int n = motionEvent.getHistorySize();
            for(int i = 0; i < n; ++i) {
                erasePoints.add(fromHistorical(motionEvent, i));
            }
            erasePoints.add(new TouchPoint(motionEvent.getX(), motionEvent.getY(), motionEvent.getPressure(), motionEvent.getSize(), motionEvent.getEventTime()));
        }
        return true;
    }

    private void onFinishErasing() {
        EventBus.getDefault().post(new EraseTouchPointListReceivedEvent(erasePoints));
    }

    private void onBeginShapeSelecting(MotionEvent motionEvent) {
        shapeSelectPoints = new TouchPointList();
        EventBus.getDefault().post(new BeginShapeSelectEvent(motionEvent));
    }

    private boolean onShapeSelecting(final MotionEvent motionEvent) {
        EventBus.getDefault().post(new ShapeSelectingEvent(motionEvent));
        if (shapeSelectPoints != null) {
            int n = motionEvent.getHistorySize();
            for(int i = 0; i < n; ++i) {
                shapeSelectPoints.add(fromHistorical(motionEvent, i));
            }
            shapeSelectPoints.add(new TouchPoint(motionEvent.getX(), motionEvent.getY(),
                    motionEvent.getPressure(), motionEvent.getSize(), motionEvent.getEventTime()));
        }
        return true;
    }

    private void onFinishShapeSelecting() {
        EventBus.getDefault().post(new ShapeSelectTouchPointListReceivedEvent(shapeSelectPoints));
    }
}
