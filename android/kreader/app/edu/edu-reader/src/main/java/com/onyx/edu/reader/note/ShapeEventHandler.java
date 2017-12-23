package com.onyx.edu.reader.note;

import android.graphics.RectF;
import android.view.MotionEvent;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderFormField;
import com.onyx.android.sdk.reader.api.ReaderFormScribble;
import com.onyx.android.sdk.scribble.api.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.api.event.BeginRawErasingEvent;
import com.onyx.android.sdk.scribble.api.event.DrawingTouchEvent;
import com.onyx.android.sdk.scribble.api.event.EndRawDataEvent;
import com.onyx.android.sdk.scribble.api.event.ErasingTouchEvent;
import com.onyx.android.sdk.scribble.api.event.RawErasePointListReceivedEvent;
import com.onyx.android.sdk.scribble.api.event.RawErasePointMoveReceivedEvent;
import com.onyx.android.sdk.scribble.api.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.api.event.RawTouchPointMoveReceivedEvent;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.edu.reader.note.data.ReaderNotePage;
import com.onyx.edu.reader.note.data.ReaderShapeFactory;
import com.onyx.edu.reader.ui.events.ShapeAddedEvent;
import com.onyx.edu.reader.ui.events.ShapeDrawingEvent;
import com.onyx.edu.reader.ui.events.ShapeErasingEvent;
import com.onyx.edu.reader.ui.events.ShortcutDrawingFinishedEvent;
import com.onyx.edu.reader.ui.events.ShortcutDrawingStartEvent;
import com.onyx.edu.reader.ui.events.ShortcutErasingEvent;
import com.onyx.edu.reader.ui.events.ShortcutErasingFinishEvent;
import com.onyx.edu.reader.ui.events.ShortcutErasingStartEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by joy on 9/19/17.
 */

public class ShapeEventHandler {

    private NoteManager noteManager;
    private volatile Shape currentShape = null;
    private TouchPoint eraserPoint;
    private TouchPointList eraserPointList;
    private PageInfo lastPageInfo = null;
    private boolean shortcutDrawing = false;

    public ShapeEventHandler(NoteManager noteManager) {
        this.noteManager = noteManager;
    }

    public void registerEventBus() {
        getEventBus().register(this);
    }

    public void unregisterEventBus() {
        getEventBus().unregister(this);
    }

    private EventBus getEventBus() {
        return noteManager.getEventBus();
    }

    public Shape getCurrentShape() {
        return currentShape;
    }

    private void resetCurrentShape() {
        currentShape = null;
    }

    public TouchPoint getEraserPoint() {
        return eraserPoint;
    }

    private PageInfo hitTest(final float x, final float y) {
        for(PageInfo pageInfo : noteManager.getVisiblePages()) {
            if (pageInfo.getVisibleRect().contains(x, y)) {
                return pageInfo;
            }
        }
        return null;
    }

    private void onDownMessage(final Shape currentShape) {
        if (ReaderShapeFactory.isDFBShape(currentShape.getType())) {
            noteManager.enableScreenPost(false);
        } else {
            noteManager.enableScreenPost(true);
        }
    }

    private Shape onShapeDown(final PageInfo pageInfo, final TouchPoint normal, final TouchPoint screen, final TouchPoint origin) {
        ReaderNotePage page = noteManager.getNoteDocument().ensurePageExist(null, pageInfo.getName(), 0);
        Shape shape = ShapeFactory.createShape(noteManager.getNoteDrawingArgs().getCurrentShapeType());
        onDownMessage(shape);
        shape.setStrokeWidth(noteManager.getNoteDrawingArgs().strokeWidth / pageInfo.getActualScale());
        shape.setColor(noteManager.getNoteDrawingArgs().strokeColor);
        shape.setPageUniqueId(pageInfo.getName());
        shape.setSubPageUniqueId(page.getSubPageUniqueId());
        shape.ensureShapeUniqueId();
        shape.setDisplayStrokeWidth(noteManager.getNoteDrawingArgs().strokeWidth);
        shape.onDown(normal, screen);
        detectionScribbleFormShape(pageInfo, shape, origin);
        currentShape = shape;
        return shape;
    }

    private void detectionScribbleFormShape(final PageInfo pageInfo, final Shape shape, final TouchPoint origin) {
        ReaderFormField field = getScribbleFormField(pageInfo, origin);
        if (field != null) {
            shape.setFormShape(true);
            shape.setFormId(field.getName());
            shape.setFormRect(field.getRect());
            shape.setFormType(ReaderShapeFactory.SHAPE_FORM_QA);
        }
    }

    private ReaderFormField getScribbleFormField(final PageInfo pageInfo, final TouchPoint origin) {
        if (!noteManager.getParent().getReaderUserDataInfo().hasFormFields(pageInfo)) {
            return null;
        }
        List<ReaderFormField> fields = noteManager.getParent().getReaderUserDataInfo().getFormFields(pageInfo);
        for (ReaderFormField field : fields) {
            if (field instanceof ReaderFormScribble) {
                RectF rect = field.getRect();
                if (rect.contains(origin.x, origin.y)) {
                    return field;
                }
            }
        }
        return null;
    }


    private Shape onShapeMove(final PageInfo pageInfo, final TouchPoint normal, final TouchPoint screen) {
        if (getCurrentShape().inVisibleDrawRectF(noteManager.getVisibleDrawRectF())) {
            getCurrentShape().onMove(normal, screen);
        }
        return getCurrentShape();
    }

    private Shape onShapeUp(final PageInfo pageInfo, final TouchPoint normal, final TouchPoint screen) {
        final Shape shape = getCurrentShape();
        if (shape == null) {
            return null;
        }
        shape.onUp(normal, screen);
        return shape;
    }

    public Shape collectPoint(final PageInfo pageInfo, final TouchPoint point, boolean createShape, boolean up) {
        float[] srcPoint = new float[2];
        float[] dstPoint = new float[2];

        TouchPoint origin = new TouchPoint(point);
        srcPoint[0] = point.x;
        srcPoint[1] = point.y;
        EpdController.mapToEpd(noteManager.getHostView(), srcPoint, dstPoint);
        final TouchPoint screen = new TouchPoint(dstPoint[0], dstPoint[1],
                point.getPressure(), point.getSize(),
                point.getTimestamp());

        if (pageInfo == null) {
            return onShapeUp(pageInfo, point, screen);
        }
        point.normalize(pageInfo);
        if (getCurrentShape() == null) {
            if (createShape) {
                return onShapeDown(pageInfo, point, screen, origin);
            }
            return null;
        }
        if (!up) {
            return onShapeMove(pageInfo, point, screen);
        }
        return onShapeUp(pageInfo, point, screen);
    }

    private TouchPoint fromHistorical(final MotionEvent motionEvent, int i) {
        final TouchPoint normalized = new TouchPoint(motionEvent.getHistoricalX(i),
                motionEvent.getHistoricalY(i),
                motionEvent.getHistoricalPressure(i),
                motionEvent.getHistoricalSize(i),
                motionEvent.getHistoricalEventTime(i));
        return normalized;
    }

    public void onDrawingTouchDown(MotionEvent motionEvent) {
        final TouchPoint touchPoint = new TouchPoint(motionEvent);
        lastPageInfo = hitTest(touchPoint.getX(), touchPoint.getY());
        if (lastPageInfo == null) {
            return;
        }
        final Shape shape = collectPoint(lastPageInfo, touchPoint, true, false);
        getEventBus().post(new ShapeDrawingEvent(shape));
    }

    public void onDrawingTouchMove(MotionEvent motionEvent) {
        if (lastPageInfo == null) {
            return;
        }

        int n = motionEvent.getHistorySize();
        for (int i = 0; i < n; i++) {
            final TouchPoint touchPoint = fromHistorical(motionEvent, i);
            PageInfo pageInfo = hitTest(touchPoint.getX(), touchPoint.getY());
            if (pageInfo != lastPageInfo) {
                continue;
            }
            collectPoint(lastPageInfo, touchPoint, true, false);
        }

        TouchPoint touchPoint = new TouchPoint(motionEvent);
        PageInfo pageInfo = hitTest(touchPoint.getX(), touchPoint.getY());
        if (pageInfo != lastPageInfo) {
            return;
        }
        final Shape shape = collectPoint(lastPageInfo, touchPoint, true, false);
        getEventBus().post(new ShapeDrawingEvent(shape));
    }

    public void onDrawingTouchUp(MotionEvent motionEvent) {
        if (lastPageInfo == null || currentShape == null) {
            return;
        }
        TouchPoint touchPoint = new TouchPoint(motionEvent);
        collectPoint(lastPageInfo, touchPoint, false, true);
        finishCurrentShape();
    }

    public void onErasingTouchDown(final MotionEvent motionEvent) {
        eraserPointList = new TouchPointList();
    }

    public void onErasingTouchMove(final MotionEvent motionEvent) {
        TouchPointList list = new TouchPointList();
        int n = motionEvent.getHistorySize();
        for (int i = 0; i < n; i++) {
            final TouchPoint touchPoint = fromHistorical(motionEvent, i);
            PageInfo pageInfo = hitTest(touchPoint.getX(), touchPoint.getY());
            if (pageInfo == null) {
                continue;
            }
            list.add(fromHistorical(motionEvent, i));
            eraserPointList.add(fromHistorical(motionEvent, i));
        }
        list.add(new TouchPoint(motionEvent));
        eraserPointList.add(new TouchPoint(motionEvent));
        getEventBus().post(new ShapeErasingEvent(false, false, list));
    }

    public void onErasingTouchUp(final MotionEvent motionEvent) {
        getEventBus().post(new ShapeErasingEvent(false, true, eraserPointList));
        eraserPoint = null;
    }

    @Subscribe
    public void onErasingTouchEvent(ErasingTouchEvent e) {
        switch (e.getMotionEvent().getAction()) {
            case MotionEvent.ACTION_DOWN:
                onErasingTouchDown(e.getMotionEvent());
                break;
            case MotionEvent.ACTION_MOVE:
                onErasingTouchMove(e.getMotionEvent());
                break;
            case MotionEvent.ACTION_UP:
                onErasingTouchUp(e.getMotionEvent());
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onDrawingTouchEvent(DrawingTouchEvent e) {
        if (noteManager.isDFBForCurrentShape()) {
            return;
        }
        switch (e.getMotionEvent().getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (noteManager.isEraser()) {
                    onErasingTouchDown(e.getMotionEvent());
                } else {
                    onDrawingTouchDown(e.getMotionEvent());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (noteManager.isEraser()) {
                    onErasingTouchMove(e.getMotionEvent());
                } else {
                    onDrawingTouchMove(e.getMotionEvent());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (noteManager.isEraser()) {
                    onErasingTouchUp(e.getMotionEvent());
                } else {
                    onDrawingTouchUp(e.getMotionEvent());
                }
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onBeginRawDataEvent(BeginRawDataEvent e) {
        Debug.e(getClass(), "onBeginRawDataEvent");
        noteManager.restoreStrokeWidth();
        if (e.isShortcutDrawing()) {
            shortcutDrawing = true;
            getEventBus().post(new ShortcutDrawingStartEvent());
        }
    }

    @Subscribe
    public void onEndRawDataEvent(EndRawDataEvent e) {
        Debug.e(getClass(), "onEndRawDataEvent");
//        onNewStash(e.shape);
    }

    private void finishCurrentShape() {
        noteManager.onNewStash(currentShape);
        resetCurrentShape();
        if (shortcutDrawing) {
            getEventBus().post(new ShortcutDrawingFinishedEvent());
        } else if (noteManager.isDFBForCurrentShape()) {
            getEventBus().post(new ShapeAddedEvent());
        }
    }

    @Subscribe
    public void onRawTouchPointMoveReceivedEvent(RawTouchPointMoveReceivedEvent e) {
    }

    @Subscribe
    public void onRawTouchPointListReceivedEvent(RawTouchPointListReceivedEvent e) {
        Debug.e(getClass(), "onRawTouchPointListReceivedEvent");
        PageInfo lastPageInfo = null;
        for (TouchPoint p : e.getTouchPointList().getPoints()) {
            PageInfo pageInfo = hitTest(p.getX(), p.getY());
            if (pageInfo == null || (lastPageInfo != null && pageInfo != lastPageInfo)) {
                if (currentShape != null) {
                    finishCurrentShape();
                }
                continue;
            }

            collectPoint(pageInfo, p, true, false);
        }
        if (currentShape != null) {
            finishCurrentShape();
        }
    }

    @Subscribe
    public void onRawErasingStartEvent(BeginRawErasingEvent e) {
        Debug.e(getClass(), "onRawErasingStartEvent");
        getEventBus().post(new ShortcutErasingStartEvent());
    }

    @Subscribe
    public void onRawErasingFinishEvent(RawErasePointListReceivedEvent e) {
        Debug.e(getClass(), "onRawErasingFinishEvent");
        getEventBus().post(new ShortcutErasingFinishEvent(e.getTouchPointList()));
    }

    @Subscribe
    public void onRawErasePointMoveReceivedEvent(RawErasePointMoveReceivedEvent e) {

    }

    @Subscribe
    public void onRawErasePointListReceivedEvent(RawErasePointListReceivedEvent e) {
        Debug.e(getClass(), "onRawErasePointListReceivedEvent");
        getEventBus().post(new ShortcutErasingEvent(e.getTouchPointList()));
    }

}
