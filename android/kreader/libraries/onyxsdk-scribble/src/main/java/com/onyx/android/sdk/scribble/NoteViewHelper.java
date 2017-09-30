package com.onyx.android.sdk.scribble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.scribble.asyncrequest.ConfigManager;
import com.onyx.android.sdk.scribble.data.LineLayoutArgs;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteDocument;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.touch.RawInputReader;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.android.sdk.scribble.utils.InkUtils;
import com.onyx.android.sdk.scribble.utils.MappingConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 6/16/16.
 * View delegate, connect ShapeManager and view.
 * It receives commands from toolbar or view and convert the command to request and send to ShapeManager
 * to process. Broadcast notification to view through callback.
 * By using rawInputReader, it could
 * * ignore touch points from certain input device.
 * * faster than onTouchEvent.
 */
public class NoteViewHelper {

    private static final String TAG = NoteViewHelper.class.getSimpleName();

    private static final int PEN_STOP = 0;
    private static final int PEN_START = 1;
    private static final int PEN_DRAWING = 2;
    private static final int PEN_PAUSE = 3;
    private static final int PEN_ERASING = 4;

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

        // caller should render the page here.
        public abstract void onBeginShapeSelect();

        // caller should draw shape select indicator
        public abstract void onShapeSelecting(final MotionEvent motionEvent);

        // caller should do hit test in current page, shapes select hit-tested.
        public abstract void onShapeSelectTouchPointListReceived(final TouchPointList pointList);
    }

    private RequestManager requestManager = new RequestManager(Thread.NORM_PRIORITY);
    private RawInputReader rawInputReader = null;
    private NoteDocument noteDocument = new NoteDocument();
    private ReaderBitmapImpl renderBitmapWrapper = new ReaderBitmapImpl();
    private ReaderBitmapImpl viewBitmapWrapper = new ReaderBitmapImpl();
    private Rect softwareLimitRect = null;
    private volatile SurfaceView surfaceView;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private List<Shape> dirtyStash = new ArrayList<>();
    private InputCallback callback;
    private TouchPointList erasePoints;
    private TouchPointList shapeSelectPoints;
    private DeviceConfig deviceConfig;
    private MappingConfig mappingConfig;
    private LineLayoutArgs lineLayoutArgs;
    private Shape currentShape = null;
    private Shape cursorShape = null;
    private boolean shortcutErasing = false;
    private int viewPosition[] = {0, 0};
    private float src[] = {0, 0};
    private float dst[] = {0, 0};
    private boolean supportBigPen = false;
    private boolean isLineLayoutMode = false;
    private volatile boolean isDrawing = false;

    private Rect customLimitRect = null;

    public void reset(final View view) {
        EpdController.setScreenHandWritingPenState(view, PEN_PAUSE);
        EpdController.enablePost(view, 1);
    }

    public void setView(final Context context, final SurfaceView view, final InputCallback c) {
        setCallback(c);
        initRawResource(context);
        initBigPenState(context);
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
        TouchPointList touchPointList = getRawInputReader().detachTouchPointList();
        boolean erasing = getRawInputReader().isErasing();
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
        ConfigManager.init(context.getApplicationContext());
        deviceConfig = ConfigManager.getInstance().getDeviceConfig();
        mappingConfig = ConfigManager.getInstance().getMappingConfig();
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

    private void setCallback(final InputCallback c) {
        callback = c;
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
        getRawInputReader().setHostView(surfaceView);
    }

    // consider view offset to screen.
    private void updateViewMatrix() {
        surfaceView.getLocationOnScreen(viewPosition);
        if (!useRawInput()) {
            return;
        }
        getRawInputReader().setHostView(surfaceView);
    }

    public void setCustomLimitRect(Rect targetRect){
        customLimitRect = targetRect;
        updateLimitRect();
    }

    private void resetRawInputReader() {
        rawInputReader = null;
    }

    private void updateLimitRect() {
        softwareLimitRect = new Rect();
        surfaceView.getLocalVisibleRect(softwareLimitRect);
        getRawInputReader().setHostView(surfaceView);
        getRawInputReader().setLimitRect(softwareLimitRect);
        EpdController.setScreenHandWritingRegionLimit(surfaceView);
    }

    private void startDrawing() {
        if (!useRawInput()) {
            return;
        }
        getRawInputReader().start();
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_START);
    }

    public void resumeDrawing() {
        setPenState(NoteDrawingArgs.PenState.PEN_SCREEN_DRAWING);
        if (!useRawInput()) {
            return;
        }

        getRawInputReader().resume();
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_DRAWING);
    }

    public void pauseDrawing() {
        if (!useRawInput()) {
            return;
        }

        getRawInputReader().pause();
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
        callback = null;
        getRawInputReader().quit();
        resetRawInputReader();
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

    public void submit(final Context context, final BaseNoteRequest request, final BaseCallback callback) {
        beforeSubmit(context, request, callback);
        getRequestManager().submitRequest(context, request, generateRunnable(request), callback);
    }

    public void submitRequestWithIdentifier(final Context context,
                                            final String identifier,
                                            final BaseNoteRequest request,
                                            final BaseCallback callback) {
        request.setIdentifier(identifier);
        beforeSubmit(context, request, callback);
        getRequestManager().submitRequest(context, identifier, request, generateRunnable(request), callback);
    }

    private void beforeSubmit(final Context context, final BaseNoteRequest request, final BaseCallback callback) {
        final Rect rect = getViewportSize();
        if (rect != null) {
            request.setViewportSize(rect);
        }
    }

    public final RequestManager getRequestManager() {
        return requestManager;
    }

    public final RawInputReader getRawInputReader() {
        if (rawInputReader == null) {
            rawInputReader = new RawInputReader();
        }
        return rawInputReader;
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

    // copy from render bitmap to view bitmap.
    public void copyBitmap() {
        if (renderBitmapWrapper == null) {
            return;
        }
        final Bitmap bitmap = renderBitmapWrapper.getBitmap();
        if (bitmap == null) {
            return;
        }
        viewBitmapWrapper.copyFrom(bitmap);
    }

    public Bitmap getViewBitmap() {
        if (viewBitmapWrapper == null) {
            return null;
        }
        return viewBitmapWrapper.getBitmap();
    }

    private final Runnable generateRunnable(final BaseNoteRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(NoteViewHelper.this);
                    request.execute(NoteViewHelper.this);
                } catch (java.lang.Exception exception) {
                    Log.d(TAG, Log.getStackTraceString(exception));
                    request.setException(exception);
                } finally {
                    request.afterExecute(NoteViewHelper.this);
                    getRequestManager().dumpWakelocks();
                    getRequestManager().removeRequest(request);
                }
            }
        };
        return runnable;
    }

    private void initRawInputProcessor() {
        if (!useRawInput()) {
            return;
        }
        getRawInputReader().setRawInputCallback(new RawInputReader.RawInputCallback() {
            @Override
            public void onBeginRawData(boolean shortcut, TouchPoint point) {
                if (callback != null) {
                    callback.onBeginRawData();
                }
            }

            @Override
            public void onRawTouchPointMoveReceived(TouchPoint point) {

            }

            @Override
            public void onRawTouchPointListReceived(TouchPointList pointList) {
                NoteViewHelper.this.onNewTouchPointListReceived(pointList);
            }

            @Override
            public void onBeginErasing(boolean shortcut, TouchPoint point) {
                ensureErasing();
            }

            @Override
            public void onEraseTouchPointMoveReceived(TouchPoint point) {

            }

            @Override
            public void onEraseTouchPointListReceived(final TouchPointList pointList) {
            }

            @Override
            public void onEndRawData(final boolean outLimitRegion, TouchPoint point) {
            }

            @Override
            public void onEndErasing(final boolean outLimitRegion, TouchPoint point) {
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
        if (callback != null) {
            callback.onRawTouchPointListReceived(shape, pointList);
        }
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
        if (callback != null) {
            callback.onBeginErasing();
        }
    }

    private boolean onErasing(final MotionEvent motionEvent) {
        if (callback != null) {
            callback.onErasing(motionEvent);
        }
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
        if (callback != null) {
            callback.onEraseTouchPointListReceived(erasePoints);
        }
        shortcutErasing = false;
    }

    private void onBeginShapeSelecting() {
        shapeSelectPoints = new TouchPointList();
        if (callback != null) {
            callback.onBeginShapeSelect();
        }
    }

    private boolean onShapeSelecting(final MotionEvent motionEvent) {
        if (callback != null) {
            callback.onShapeSelecting(motionEvent);
        }
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
        if (callback != null) {
            callback.onShapeSelectTouchPointListReceived(shapeSelectPoints);
        }
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
            onBeginShapeSelecting();
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
            final TouchPoint screen = touchPointFromNormalized(normalized);
            if (!checkTouchPoint(normalized)) {
                continue;
            }
            currentShape.onMove(normalized, screen);
            if (callback != null) {
                callback.onDrawingTouchMove(motionEvent, currentShape, false);
            }
        }

        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        if (!checkTouchPoint(normalized)) {
            return;
        }
        currentShape.onMove(normalized, screen);
        setDrawing(true);
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
        if (!checkTouchPoint(normalized)) {
            return;
        }
        currentShape.onUp(normalized, screen);
        setDrawing(false);
        if (callback != null) {
            callback.onDrawingTouchUp(motionEvent, currentShape);
        }
    }

    private TouchPoint touchPointFromNormalized(final TouchPoint normalized) {
        src[0] = normalized.getX();
        src[1] = normalized.getY();
        EpdController.mapToEpd(surfaceView, src, dst);
        TouchPoint result = new TouchPoint(dst[0], dst[1], normalized.getPressure(), normalized.getSize(), normalized.getTimestamp());
        return result;
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
}
