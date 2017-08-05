package com.onyx.android.sdk.scribble.asyncrequest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginShapeSelectEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchDownEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchMoveEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchUpEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.EraseTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ShapeSelectTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ShapeSelectingEvent;
import com.onyx.android.sdk.scribble.data.LineLayoutArgs;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteDocument;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.math.OnyxMatrix;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.touch.RawInputProcessor;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.android.sdk.scribble.utils.InkUtils;
import com.onyx.android.sdk.scribble.utils.MappingConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 6/16/16.
 * View delegate, connect ShapeManager and view.
 * It receives commands from toolbar or view and convert the command to request and send to ShapeManager
 * to process. Broadcast notification to view through callback.
 * By using rawInputProcessor, it could
 * * ignore touch points from certain input device.
 * * faster than onTouchEvent.
 */
public class AsyncNoteViewHelper {

    private static final String TAG = AsyncNoteViewHelper.class.getSimpleName();

    private static final int PEN_STOP = 0;
    private static final int PEN_START = 1;
    private static final int PEN_DRAWING = 2;
    private static final int PEN_PAUSE = 3;
    private static final int PEN_ERASING = 4;

    private RawInputProcessor rawInputProcessor = null;
    private NoteDocument noteDocument = new NoteDocument();
    private ReaderBitmapImpl renderBitmapWrapper = new ReaderBitmapImpl();
    private Rect softwareLimitRect = null;
    private volatile SurfaceView surfaceView;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private List<Shape> dirtyStash = new ArrayList<>();
    private TouchPointList erasePoints;
    private TouchPointList shapeSelectPoints;
    private DeviceConfig deviceConfig;
    private MappingConfig mappingConfig;
    private LineLayoutArgs lineLayoutArgs;
    private Shape currentShape = null;
    private Shape cursorShape = null;
    private boolean shortcutErasing = false;
    private OnyxMatrix viewToEpdMatrix = null;
    private int viewPosition[] = {0, 0};
    private boolean supportBigPen = false;
    private boolean isLineLayoutMode = false;
    private volatile boolean isDrawing = false;

    private Rect customLimitRect = null;
    private RequestManager requestManager = new RequestManager();
    public DashPathEffect selectedDashPathEffect = new DashPathEffect(new float[]{4, 4, 4, 4}, 2);

    public void reset(final View view) {
        EpdController.setScreenHandWritingPenState(view, PEN_PAUSE);
        EpdController.enablePost(view, 1);
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public void setView(final Context context, final SurfaceView view) {
        initRawResource(context);
        initBigPenState(context);
        initViewToEpdMatrix();
        initWithSurfaceView(view);
        initRawInputProcessor();
        updateScreenMatrix();
        updateViewMatrix();
        updateLimitRect();
        pauseDrawing();
    }

    public SurfaceView getView() {
        return surfaceView;
    }

    public void quit() {
        pauseDrawing();
        removeLayoutListener();
        quitDrawing();
        setLineLayoutMode(false);
    }

    public void flushTouchPointList() {
        TouchPointList touchPointList = getRawInputProcessor().detachTouchPointList();
        boolean erasing = getRawInputProcessor().isErasing();
        if (touchPointList == null || erasing) {
            return;
        }
        onNewTouchPointListReceived(touchPointList);
    }

    public void openDocument(final Context context, final String documentUniqueId, final String parentUniqueId) {
        getNoteDocument().open(context, documentUniqueId, parentUniqueId);
        onDocumentOpened();
    }

    public void createDocument(final Context context, final String documentUniqueId, final String parentUniqueId) {
        getNoteDocument().create(context, documentUniqueId, parentUniqueId);
        onDocumentOpened();
    }

    private void onDocumentOpened() {
        renderBitmapWrapper.clear();
        NoteModel.setDefaultEraserRadius(deviceConfig.getEraserRadius());
        getNoteDocument().getNoteDrawingArgs().setEraserRadius(deviceConfig.getEraserRadius());
        InkUtils.setPressureEntries(mappingConfig.getPressureList());
        EpdController.setStrokeWidth(getNoteDocument().getNoteDrawingArgs().strokeWidth);
        EpdController.setStrokeColor(getNoteDocument().getNoteDrawingArgs().strokeColor);
    }

    public void undo(final Context context) {
        getNoteDocument().getCurrentPage(context).undo(isLineLayoutMode());
    }

    public void redo(final Context context) {
        getNoteDocument().getCurrentPage(context).redo(isLineLayoutMode());
    }

    public void clearPageUndoRedo(final Context context) {
        NotePage currentPage = getNoteDocument().getCurrentPage(context);
        if (currentPage != null) {
            currentPage.clearUndoRedoRecord();
        }
    }

    public void save(final Context context, final String title , boolean closeAfterSave) {
        getNoteDocument().save(context, title);
        if (closeAfterSave) {
            getNoteDocument().close(context);
            renderBitmapWrapper.clear();
        }
    }

    private void initRawResource(final Context context) {
        deviceConfig = DeviceConfig.sharedInstance(context, "note");
        mappingConfig = MappingConfig.sharedInstance(context, "note");
    }

    private void initBigPenState(final Context context) {
        supportBigPen = deviceConfig.supportBigPen();
    }

    private void initWithSurfaceView(final SurfaceView view) {
        surfaceView = view;
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return processTouchEvent(motionEvent);
            }
        });
        surfaceView.getViewTreeObserver().addOnGlobalLayoutListener(getGlobalLayoutListener());
    }

    private ViewTreeObserver.OnGlobalLayoutListener getGlobalLayoutListener() {
        if (globalLayoutListener == null) {
            globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    updateViewMatrix();
                    updateLimitRect();
                }
            };
        }
        return globalLayoutListener;
    }

    private float getTouchWidth() {
        float value = Device.currentDevice().getTouchWidth();
        if (value <= 0) {
            return deviceConfig.getTouchWidth();
        }
        return value;
    }

    private float getTouchHeight() {
        float value = Device.currentDevice().getTouchHeight();
        if (value <= 0) {
            return deviceConfig.getTouchHeight();
        }
        return value;
    }


    // matrix from input touch panel to system view with correct orientation.
    private void updateScreenMatrix() {
        if (!useRawInput()) {
            return;
        }

        final Matrix screenMatrix = new Matrix();
        screenMatrix.postRotate(deviceConfig.getEpdPostOrientation());
        screenMatrix.postTranslate(deviceConfig.getEpdPostTx(), deviceConfig.getEpdPostTy());
        screenMatrix.preScale(deviceConfig.getEpdWidth() / getTouchWidth(),
                deviceConfig.getEpdHeight() / getTouchHeight());
        getRawInputProcessor().setScreenMatrix(screenMatrix);
    }

    // consider view offset to screen.
    private void updateViewMatrix() {
        surfaceView.getLocationOnScreen(viewPosition);
        if (!useRawInput()) {
            return;
        }

        final Matrix viewMatrix = new Matrix();
        viewMatrix.postTranslate(-viewPosition[0], -viewPosition[1]);
        getRawInputProcessor().setViewMatrix(viewMatrix);
    }

    private OnyxMatrix initViewToEpdMatrix() {
        viewToEpdMatrix = new OnyxMatrix();
        viewToEpdMatrix.postRotate(deviceConfig.getViewPostOrientation());
        viewToEpdMatrix.postTranslate(deviceConfig.getViewPostTx(), deviceConfig.getViewPostTy());
        return viewToEpdMatrix;
    }

    // matrix from android view to epd.
    private OnyxMatrix matrixFromViewToEpd() {
        return viewToEpdMatrix;
    }

    public void setCustomLimitRect(Rect targetRect){
        customLimitRect = targetRect;
        updateLimitRect();
    }

    private void resetRawInputProcessor() {
        rawInputProcessor = null;
    }

    private void updateLimitRect() {
        Rect dfbLimitRect = new Rect();
        softwareLimitRect = new Rect();
        int xAxisOffset = 0, yAxisOffset = 0;


        if (customLimitRect == null) {
            //for software render limit rect
            surfaceView.getLocalVisibleRect(softwareLimitRect);
            //for dfb render limit rect
            surfaceView.getGlobalVisibleRect(dfbLimitRect);
        } else {
            Rect surfaceLocalVisibleRect = new Rect();

            surfaceView.getLocalVisibleRect(surfaceLocalVisibleRect);
            softwareLimitRect = customLimitRect;

            //a little tricky here,we assume target rect is always smaller than visible rect.
            xAxisOffset = customLimitRect.left - surfaceLocalVisibleRect.left;
            yAxisOffset = surfaceLocalVisibleRect.bottom - customLimitRect.bottom;

            surfaceView.getGlobalVisibleRect(dfbLimitRect);

            //do the transform here.
            dfbLimitRect.set(dfbLimitRect.left + xAxisOffset,
                    dfbLimitRect.top + yAxisOffset,
                    dfbLimitRect.right - xAxisOffset,
                    dfbLimitRect.bottom - yAxisOffset);
        }

        dfbLimitRect.offsetTo(0, 0);
        getRawInputProcessor().setLimitRect(customLimitRect == null ? dfbLimitRect : customLimitRect);

        int viewPosition[] = {0, 0};
        surfaceView.getLocationOnScreen(viewPosition);
        if (DeviceConfig.sharedInstance(surfaceView.getContext()).getEpdPostOrientation() == 270) {
            int reverseTop = ((Activity) surfaceView.getContext()).getWindow().getDecorView().getBottom() - surfaceView.getHeight() - viewPosition[1];
            dfbLimitRect.offsetTo(viewPosition[0] + xAxisOffset, reverseTop + yAxisOffset);
        }else {
            dfbLimitRect.offsetTo(viewPosition[0] + xAxisOffset, viewPosition[1] + yAxisOffset);
        }

        final OnyxMatrix matrix = matrixFromViewToEpd();
        matrix.mapInPlace(dfbLimitRect);
        EpdController.setScreenHandWritingRegionLimit(surfaceView,
                Math.min(dfbLimitRect.left, dfbLimitRect.right),
                Math.min(dfbLimitRect.top, dfbLimitRect.bottom),
                Math.max(dfbLimitRect.left, dfbLimitRect.right),
                Math.max(dfbLimitRect.top, dfbLimitRect.bottom));
    }

    private void startDrawing() {
        if (!useRawInput()) {
            return;
        }
        getRawInputProcessor().start();
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_START);
    }

    public void resumeDrawing() {
        setPenState(NoteDrawingArgs.PenState.PEN_SCREEN_DRAWING);
        if (!useRawInput()) {
            return;
        }

        getRawInputProcessor().resume();
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_DRAWING);
    }

    public void pauseDrawing() {
        if (!useRawInput()) {
            return;
        }

        getRawInputProcessor().pause();
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_PAUSE);
    }

    public void enableScreenPost(boolean enable) {
        if (surfaceView != null) {
            EpdController.enablePost(surfaceView, enable ? 1 : 0);
        }
    }

    public void quitDrawing() {
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_STOP);
        if (!useRawInput()) {
            return;
        }
        getRawInputProcessor().quit();
        resetRawInputProcessor();
    }

    public void setBackground(int bgType) {
        getNoteDocument().setBackground(bgType);
    }

    public void setBackgroundFilePath(String filePath){
        getNoteDocument().setBackgroundFilePath(filePath);
    }

    public void setLineLayoutBackground(int bgType) {
        getNoteDocument().setLineLayoutBackground(bgType);
    }

    public void setStrokeWidth(float width) {
        getNoteDocument().setStrokeWidth(width);
        EpdController.setStrokeWidth(width);
    }

    public void setStrokeColor(int color) {
        getNoteDocument().setStrokeColor(color);
        EpdController.setStrokeColor(color);
    }

    public void updateDrawingArgs(final NoteDrawingArgs drawingArgs) {
        setStrokeColor(drawingArgs.strokeColor);
        setStrokeWidth(drawingArgs.strokeWidth);
        setCurrentShapeType(drawingArgs.getCurrentShapeType());
        setBackground(drawingArgs.background);
    }

    public void updateShapeDataInfo(final Context context, final ShapeDataInfo shapeDataInfo) {
        shapeDataInfo.updateShapePageMap(
                getNoteDocument().getPageNameList(),
                getNoteDocument().getCurrentPageIndex());
        shapeDataInfo.setInUserErasing(inUserErasing());
        shapeDataInfo.updateDrawingArgs(getNoteDocument().getNoteDrawingArgs());
        shapeDataInfo.setCanRedoShape(getNoteDocument().getCurrentPage(context).canRedo());
        shapeDataInfo.setCanUndoShape(getNoteDocument().getCurrentPage(context).canUndo());
        shapeDataInfo.setDocumentUniqueId(getNoteDocument().getDocumentUniqueId());
    }

    private void removeLayoutListener() {
        if (surfaceView == null || globalLayoutListener == null) {
            return;
        }
        surfaceView.getViewTreeObserver().removeGlobalOnLayoutListener(getGlobalLayoutListener());
        surfaceView = null;
        globalLayoutListener = null;
    }

    public Rect getViewportSize() {
        if (surfaceView != null) {
            return new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        }
        return null;
    }

    public final RawInputProcessor getRawInputProcessor() {
        if (rawInputProcessor == null) {
            rawInputProcessor = new RawInputProcessor();
        }
        return rawInputProcessor;
    }

    public final NoteDocument getNoteDocument() {
        return noteDocument;
    }

    public Bitmap updateRenderBitmap(final Rect viewportSize) {
        renderBitmapWrapper.update(viewportSize.width(), viewportSize.height(), Bitmap.Config.ARGB_8888);
        return renderBitmapWrapper.getBitmap();
    }

    public Bitmap getRenderBitmap() {
        return renderBitmapWrapper.getBitmap();
    }

    private void initRawInputProcessor() {
        if (!useRawInput()) {
            return;
        }
        getRawInputProcessor().setRawInputCallback(new RawInputProcessor.RawInputCallback() {
            @Override
            public void onBeginRawData() {
                EventBus.getDefault().post(new BeginRawDataEvent());
            }

            @Override
            public void onRawTouchPointListReceived(final Shape shape, TouchPointList pointList) {
                AsyncNoteViewHelper.this.onNewTouchPointListReceived(pointList);
            }

            @Override
            public void onBeginErasing() {
                ensureErasing();
            }

            @Override
            public void onEraseTouchPointListReceived(final TouchPointList pointList) {
            }

            @Override
            public void onEndRawData() {
            }

            @Override
            public void onEndErasing() {
            }
        });
        startDrawing();
    }

    private void onNewTouchPointListReceived(final TouchPointList pointList) {
        if (!useRawInput()) {
            return;
        }
        Shape shape = createNewShape(isLineLayoutMode(), getNoteDocument().getNoteDrawingArgs().getCurrentShapeType());
        shape.addPoints(pointList);
        dirtyStash.add(shape);
        EventBus.getDefault().post(new RawTouchPointListReceivedEvent(shape,pointList));
    }

    private Shape createNewShape(boolean isSpanTextMode, int type) {
        Shape shape = ShapeFactory.createShape(type);
        shape.setStrokeWidth(getNoteDocument().getStrokeWidth());
        shape.setColor(getNoteDocument().getStrokeColor());
        shape.setLayoutType(isSpanTextMode ? ShapeFactory.POSITION_LINE_LAYOUT : ShapeFactory.POSITION_FREE);
        return shape;
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
        shortcutErasing = false;
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
            shapeSelectPoints.add(new TouchPoint(motionEvent.getX(), motionEvent.getY(), motionEvent.getPressure(), motionEvent.getSize(), motionEvent.getEventTime()));
        }
        return true;
    }

    private void onFinishShapeSelecting() {
        EventBus.getDefault().post(new ShapeSelectTouchPointListReceivedEvent(shapeSelectPoints));
    }

    public List<Shape> getDirtyStash() {
        return dirtyStash;
    }

    public List<Shape> detachStash() {
        final List<Shape> temp = new ArrayList<>();
        temp.addAll(dirtyStash);
        dirtyStash = new ArrayList<>();
        return temp;
    }

    public NoteDrawingArgs.PenState getPenState() {
        return getNoteDocument().getPenState();
    }

    public void setPenState(NoteDrawingArgs.PenState penState) {
        getNoteDocument().setPenState(penState);
    }

    public void ensureErasing() {
        shortcutErasing = true;
    }

    public void updatePenStateByCurrentShapeType() {
        int type = getCurrentShapeType();
        if (ShapeFactory.isDFBShape(type)) {
            setPenState(NoteDrawingArgs.PenState.PEN_SCREEN_DRAWING);
            return;
        }
        switch (type) {
            case ShapeFactory.SHAPE_ERASER:
                setPenState(NoteDrawingArgs.PenState.PEN_USER_ERASING);
                break;
            case ShapeFactory.SHAPE_SELECTOR:
                setPenState(NoteDrawingArgs.PenState.PEN_SHAPE_SELECTING);
                break;
            default:
                setPenState(NoteDrawingArgs.PenState.PEN_CANVAS_DRAWING);
                break;
        }
    }

    public boolean inShapeSelecting(){
        return getPenState() == NoteDrawingArgs.PenState.PEN_SHAPE_SELECTING;
    }

    public boolean inErasing() {
        return (shortcutErasing || getPenState() == NoteDrawingArgs.PenState.PEN_USER_ERASING);
    }

    public boolean inUserErasing() {
        return getPenState() == NoteDrawingArgs.PenState.PEN_USER_ERASING;
    }

    public int getCurrentShapeType() {
        return getNoteDocument().getNoteDrawingArgs().getCurrentShapeType();
    }

    public void setCurrentShapeType(int currentShapeType) {
        getNoteDocument().getNoteDrawingArgs().setCurrentShapeType(currentShapeType);
        updatePenStateByCurrentShapeType();
    }

    private boolean useRawInput() {
        return deviceConfig != null && deviceConfig.useRawInput();
    }

    private boolean renderByFramework() {
        return ShapeFactory.isDFBShape(getCurrentShapeType());
    }

    private boolean isSingleTouch() {
        return deviceConfig != null && deviceConfig.isSingleTouch();
    }

    private boolean isEnableFingerErasing() {
        return deviceConfig != null && deviceConfig.isEnableFingerErasing();
    }

    public boolean useDFBForCurrentState() {
        return ShapeFactory.isDFBShape(getCurrentShapeType()) && !inUserErasing();
    }

    private boolean isFingerTouch(int toolType) {
        return toolType == MotionEvent.TOOL_TYPE_FINGER;
    }

    private boolean processTouchEvent(final MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() > 1) {
            return true;
        }
        int toolType = motionEvent.getToolType(0);
        if (isFingerTouch(toolType) && !isSingleTouch()) {
            return true;
        }

        if ((supportBigPen && toolType == MotionEvent.TOOL_TYPE_ERASER) || inErasing()) {
            if (isFingerTouch(toolType)) {
                if (isEnableFingerErasing()) {
                    return forwardErasing(motionEvent);
                }
                return true;
            }
            return forwardErasing(motionEvent);
        }
        if (inShapeSelecting()){
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
            enableScreenPost(false);
        } else {
            enableScreenPost(true);
        }
    }

    private void onDrawingTouchDown(final MotionEvent motionEvent) {
        currentShape = createNewShape(isLineLayoutMode, getNoteDocument().getNoteDrawingArgs().getCurrentShapeType());
        beforeDownMessage(currentShape);
        dirtyStash.add(currentShape);
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        if (!checkTouchPoint(normalized)) {
            return;
        }
        currentShape.onDown(normalized, screen);
        setDrawing(true);
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
        setDrawing(true);
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
        setDrawing(false);
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

    public boolean supportColor(Context context){
        return DeviceConfig.sharedInstance(context, "note").supportColor();
    }

    public void setLineLayoutMode(boolean lineLayoutMode) {
        isLineLayoutMode = lineLayoutMode;
    }

    public boolean isDrawing() {
        return isDrawing;
    }

    public void setDrawing(boolean drawing) {
        isDrawing = drawing;
    }

    public boolean isLineLayoutMode() {
        return isLineLayoutMode;
    }

    public void updateCursorShape(final int left, final int top, final int right, final int bottom) {
        TouchPointList touchPointList = new TouchPointList();
        TouchPoint downPoint = new TouchPoint();
        downPoint.offset(left, top);
        TouchPoint currentPoint = new TouchPoint();
        currentPoint.offset(right, bottom);
        touchPointList.add(downPoint);
        touchPointList.add(currentPoint);
        getCursorShape().addPoints(touchPointList);
    }

    public Shape getCursorShape() {
        if (cursorShape == null) {
            cursorShape = createNewShape(true, ShapeFactory.SHAPE_LINE);
        }
        return cursorShape;
    }

    public void renderCursorShape(final RenderContext renderContext) {
        if (cursorShape == null || !isLineLayoutMode()) {
            return;
        }
        cursorShape.render(renderContext);
    }

    public void renderSelectedRect(RectF selectedRectF, RenderContext renderContext) {
        if (selectedRectF.width() < 0 || selectedRectF.height() < 0) {
            return;
        }
        Paint boundingPaint = new Paint(Color.BLACK);
        boundingPaint.setStyle(Paint.Style.STROKE);
        boundingPaint.setPathEffect(selectedDashPathEffect);
        renderContext.canvas.drawRect(selectedRectF, boundingPaint);
    }

    public void drawLineLayoutBackground(final RenderContext renderContext) {
        if (!isLineLayoutMode()) {
            return;
        }
        if (getNoteDocument().getLineLayoutBackground() == NoteBackgroundType.EMPTY) {
            return;
        }
        LineLayoutArgs args = getLineLayoutArgs();
        if (args == null) {
            return;
        }

        Rect viewRect = new Rect();
        surfaceView.getLocalVisibleRect(viewRect);
        int count = args.getLineCount();
        int lineHeight = args.getLineHeight();
        int baseline = args.getBaseLine();
        Paint paint = new Paint();
        for (int i = 0; i < count; i++) {
            renderContext.canvas.drawLine(viewRect.left, baseline + 1, viewRect.right, baseline + 1, paint);
            baseline += lineHeight;
        }
    }

    public void setLineLayoutArgs(LineLayoutArgs lineLayoutArgs) {
        this.lineLayoutArgs = lineLayoutArgs;
    }

    public LineLayoutArgs getLineLayoutArgs() {
        return lineLayoutArgs;
    }

    public void renderToSurfaceView() {
        Rect rect = checkSurfaceView();
        if (rect == null) {
            return;
        }

        applyUpdateMode();
        Canvas canvas = surfaceView.getHolder().lockCanvas(rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        clearBackground(canvas, paint, rect);
        canvas.drawBitmap(getRenderBitmap(), 0, 0, paint);
        RenderContext renderContext = RenderContext.create(canvas, paint, null);
        for (Shape shape : getDirtyStash()) {
            shape.render(renderContext);
        }
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

    private Rect checkSurfaceView() {
        if (surfaceView == null || !surfaceView.getHolder().getSurface().isValid()) {
            Log.e(TAG, "surfaceView is not valid");
            return null;
        }
        return getViewportSize();
    }

    private void applyUpdateMode() {
        if (false) {
            EpdController.setViewDefaultUpdateMode(surfaceView, UpdateMode.GC);
        } else {
            EpdController.resetUpdateMode(surfaceView);
        }
    }

    private Canvas getCanvasForDraw(SurfaceView surfaceView, Rect rect) {
        return surfaceView.getHolder().lockCanvas(rect);
    }

    private void clearBackground(final Canvas canvas, final Paint paint, final Rect rect) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);
    }

}
