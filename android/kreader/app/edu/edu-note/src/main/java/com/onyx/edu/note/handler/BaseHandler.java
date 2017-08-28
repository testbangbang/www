package com.onyx.edu.note.handler;

import android.support.annotation.CallSuper;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.EraseTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ViewTouchEvent;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.data.ScribbleToolBarMenuID;
import com.onyx.edu.note.scribble.event.HandlerActivateEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/5/27 12:30.
 */

public abstract class BaseHandler {
    private static final String TAG = BaseHandler.class.getSimpleName();
    protected NoteManager mNoteManager;

    List<Integer> mFunctionBarMenuFunctionIDList = new ArrayList<>();
    List<Integer> mToolBarMenuFunctionIDList = new ArrayList<>();
    SparseArray<List<Integer>> mFunctionBarMenuSubMenuIDListSparseArray = new SparseArray<>();

    private Shape currentShape = null;
    private TouchPointList erasePoints;

    public BaseHandler(NoteManager mNoteManager) {
        this.mNoteManager = mNoteManager;
    }

    @CallSuper
    public void onActivate() {
        buildFunctionBarMenuFunctionList();
        buildToolBarMenuFunctionList();
        buildFunctionBarMenuSubMenuIDListSparseArray();
        mNoteManager.post(new HandlerActivateEvent(mFunctionBarMenuFunctionIDList, mToolBarMenuFunctionIDList, mFunctionBarMenuSubMenuIDListSparseArray));
    }

    public void onDeactivate() {
    }

    public void close() {
    }

    protected abstract void buildFunctionBarMenuFunctionList();

    protected abstract void buildToolBarMenuFunctionList();

    protected abstract void buildFunctionBarMenuSubMenuIDListSparseArray();

    public abstract void handleFunctionBarMenuFunction(@ScribbleFunctionBarMenuID.ScribbleFunctionBarMenuDef int functionBarMenuID);

    public abstract void handleSubMenuFunction(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID);

    public abstract void handleToolBarMenuFunction(String uniqueID, String title,
                                                   @ScribbleToolBarMenuID.ScribbleToolBarMenuDef int toolBarMenuID);

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
        Shape shape = createNewShape(mNoteManager.inSpanScribbleMode(),
                mNoteManager.getDocumentHelper().getNoteDrawingArgs().getCurrentShapeType());
        shape.addPoints(event.getTouchPointList());
        mNoteManager.onNewShape(shape);
        onRawTouchPointListReceived();
    }

    private Shape createNewShape(boolean isSpanTextMode, int type) {
        Shape shape = ShapeFactory.createShape(type);
        shape.setStrokeWidth(mNoteManager.getDocumentHelper().getStrokeWidth());
        shape.setColor(mNoteManager.getDocumentHelper().getStrokeColor());
        shape.setLayoutType(isSpanTextMode ? ShapeFactory.POSITION_LINE_LAYOUT : ShapeFactory.POSITION_FREE);
        return shape;
    }

    @Subscribe
    public void onViewTouchEvent(ViewTouchEvent event) {
        MotionEvent motionEvent = event.getMotionEvent();
        if (motionEvent.getPointerCount() > 1) {
            return;
        }
        int toolType = motionEvent.getToolType(0);
        if (isFingerTouch(toolType) && !isSingleTouch()) {
            return;
        }

        if ((supportBigPen() && toolType == MotionEvent.TOOL_TYPE_ERASER) || mNoteManager.inUserErasing()) {
            if (isFingerTouch(toolType)) {
                if (isEnableFingerErasing()) {
                    forwardErasing(motionEvent);
                }
                return;
            }
            forwardErasing(motionEvent);
        }
        if (!(isUseRawInput() && renderByFramework())) {
            forwardDrawing(motionEvent);
        }
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
        currentShape = createNewShape(mNoteManager.inSpanScribbleMode(), mNoteManager.getDocumentHelper().getNoteDrawingArgs().getCurrentShapeType());
        beforeDownMessage(currentShape);
        mNoteManager.onNewShape(currentShape);
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        if (!mNoteManager.getTouchHelper().checkTouchPoint(normalized)) {
            return;
        }
        currentShape.onDown(normalized, screen);
        mNoteManager.setDrawing(true);
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
            if (!mNoteManager.getTouchHelper().checkTouchPoint(normalized)) {
                continue;
            }
            currentShape.onMove(normalized, screen);
        }

        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        if (!mNoteManager.getTouchHelper().checkTouchPoint(normalized)) {
            return;
        }
        currentShape.onMove(normalized, screen);
        mNoteManager.setDrawing(true);
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
        if (!mNoteManager.getTouchHelper().checkTouchPoint(normalized)) {
            return;
        }
        currentShape.onUp(normalized, screen);
        mNoteManager.setDrawing(false);
        if (!currentShape.supportDFB()) {
            drawCurrentPage();
        }
        onDrawingTouchUp();
    }

    private void beforeDownMessage(final Shape currentShape) {
        if (ShapeFactory.isDFBShape(currentShape.getType())) {
            mNoteManager.enableScreenPost(false);
        } else {
            mNoteManager.enableScreenPost(true);
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
        mNoteManager.post(new BeginErasingEvent());
    }

    private boolean onErasing(final MotionEvent motionEvent) {
        mNoteManager.post(new ErasingEvent(new TouchPoint(motionEvent), false));
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
        mNoteManager.post(new EraseTouchPointListReceivedEvent(erasePoints));
    }

    private void drawCurrentPage() {
        AsyncBaseNoteRequest request = new AsyncBaseNoteRequest();
        mNoteManager.submitRequest(request, null);
    }

    private boolean isFingerTouch(int toolType) {
        return toolType == MotionEvent.TOOL_TYPE_FINGER;
    }

    private boolean isSingleTouch() {
        return mNoteManager.getDeviceConfig().isSingleTouch();
    }

    private boolean supportBigPen() {
        return mNoteManager.getDeviceConfig().supportBigPen();
    }

    private boolean isEnableFingerErasing() {
        return mNoteManager.getDeviceConfig().isEnableFingerErasing();
    }

    private boolean isUseRawInput() {
        return mNoteManager.getDeviceConfig().useRawInput();
    }

    private boolean renderByFramework() {
        return ShapeFactory.isDFBShape(mNoteManager.getDocumentHelper().getCurrentShapeType());
    }
}
