package com.onyx.android.sdk.scribble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
import com.onyx.android.sdk.scribble.data.*;
import com.onyx.android.sdk.scribble.math.OnyxMatrix;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.touch.RawInputProcessor;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;

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
public class NoteViewHelper {

    private static final String TAG = NoteViewHelper.class.getSimpleName();

    public enum PenState {
        PEN_NULL,                   // not initialized yet.
        PEN_SCREEN_DRAWING,         // in direct screen drawing state, the input could be raw input or touch panel.
        PEN_CANVAS_DRAWING,         // in canvas drawing state
        PEN_USER_ERASING,           // in user erasing state
    }

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

        public abstract void onDrawingTouchMove(final MotionEvent motionEvent, final Shape shape);

        public abstract void onDrawingTouchUp(final MotionEvent motionEvent, final Shape shape);

        // caller should render the page here.
        public abstract void onBeginErasing();

        // caller should draw erase indicator
        public abstract void onErasing(final MotionEvent motionEvent);

        // caller should do hit test in current page, remove shapes hit-tested.
        public abstract void onEraseTouchPointListReceived(final TouchPointList pointList);

    }

    private RequestManager requestManager = new RequestManager(Thread.NORM_PRIORITY);
    private RawInputProcessor rawInputProcessor = new RawInputProcessor();
    private NoteDocument noteDocument = new NoteDocument();
    private ReaderBitmapImpl renderBitmapWrapper = new ReaderBitmapImpl();
    private ReaderBitmapImpl viewBitmapWrapper = new ReaderBitmapImpl();
    private Rect limitRect = null;
    private volatile SurfaceView surfaceView;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private List<Shape> dirtyStash = new ArrayList<Shape>();
    private InputCallback callback;
    private TouchPointList erasePoints;
    private DeviceConfig deviceConfig;
    private Shape currentShape = null;
    private boolean shortcutErasing = false;
    private OnyxMatrix viewToEpdMatrix = null;
    private int viewPosition[] = {0, 0};

    public void reset(final View view) {
        EpdController.setScreenHandWritingPenState(view, PEN_PAUSE);
        EpdController.enablePost(view, 1);
    }

    public void setView(final Context context, final SurfaceView view, final InputCallback c) {
        setCallback(c);
        initRawResource(context);
        initViewToEpdMatrix();
        initWithSurfaceView(view);
        initRawInputProcessor();
        updateScreenMatrix();
        updateViewMatrix();
        updateLimitRect();
        pauseDrawing();
    }

    public View getView() {
        return surfaceView;
    }

    public void quit() {
        pauseDrawing();
        quitDrawing();
        removeLayoutListener();
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
        EpdController.setStrokeWidth(getNoteDocument().getNoteDrawingArgs().strokeWidth);
        EpdController.setStrokeColor(getNoteDocument().getNoteDrawingArgs().strokeColor);
    }

    public void undo(final Context context) {
        getNoteDocument().getCurrentPage(context).undo();
    }

    public void redo(final Context context) {
        getNoteDocument().getCurrentPage(context).redo();
    }

    public void close(final Context context, final String title) {
        getNoteDocument().save(context, title);
        getNoteDocument().close(context);
        renderBitmapWrapper.clear();
    }

    private void initRawResource(final Context context) {
        deviceConfig = DeviceConfig.sharedInstance(context, "note");
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

        final Matrix screenMatrix = new Matrix();
        screenMatrix.postRotate(deviceConfig.getEpdPostOrientation());
        screenMatrix.postTranslate(deviceConfig.getEpdPostTx(), deviceConfig.getEpdPostTy());
        screenMatrix.preScale(deviceConfig.getEpdWidth() / getTouchWidth(),
                deviceConfig.getEpdHeight() / getTouchHeight());
        rawInputProcessor.setScreenMatrix(screenMatrix);
    }

    // consider view offset to screen.
    private void updateViewMatrix() {
        surfaceView.getLocationOnScreen(viewPosition);
        if (!useRawInput()) {
            return;
        }

        final Matrix viewMatrix = new Matrix();
        viewMatrix.postTranslate(-viewPosition[0], -viewPosition[1]);
        rawInputProcessor.setViewMatrix(viewMatrix);
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

    private void updateLimitRect() {
        if (!useRawInput()) {
            return;
        }

        limitRect = new Rect();
        surfaceView.getGlobalVisibleRect(limitRect);
        limitRect.offsetTo(0, 0);
        rawInputProcessor.setLimitRect(limitRect);

        int viewPosition[] = {0, 0};
        surfaceView.getLocationOnScreen(viewPosition);
        limitRect.offsetTo(viewPosition[0], viewPosition[1]);

        final OnyxMatrix matrix = matrixFromViewToEpd();
        matrix.mapInPlace(limitRect);
        EpdController.setScreenHandWritingRegionLimit(surfaceView,
                Math.min(limitRect.left, limitRect.right),
                Math.min(limitRect.top, limitRect.bottom),
                Math.max(limitRect.left, limitRect.right),
                Math.max(limitRect.top, limitRect.bottom));
    }

    private void startDrawing() {
        if (!useRawInput()) {
            return;
        }
        getRawInputProcessor().start();
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_START);
    }

    public void resumeDrawing() {
        setPenState(PenState.PEN_SCREEN_DRAWING);
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
    }

    public void setBackground(int bgType) {
        getNoteDocument().setBackground(bgType);
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
        setCurrentShapeType(drawingArgs.currentShapeType);
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
    }

    private Rect getViewportSize() {
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

    public final RawInputProcessor getRawInputProcessor() {
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
        rawInputProcessor.setRawInputCallback(new RawInputProcessor.RawInputCallback() {
            @Override
            public void onBeginRawData() {
            }

            @Override
            public void onRawTouchPointListReceived(final Shape shape, TouchPointList pointList) {
                NoteViewHelper.this.onNewTouchPointListReceived(pointList);
            }

            @Override
            public void onBeginErasing() {
                ensureErasing();
            }

            @Override
            public void onEraseTouchPointListReceived(final TouchPointList pointList) {
            }


        });
        startDrawing();
    }

    private void onNewTouchPointListReceived(final TouchPointList pointList) {
        if (!useRawInput()) {
            return;
        }
        Shape shape = createNewShape();
        shape.addPoints(pointList);
        dirtyStash.add(shape);
        if (callback != null) {
            callback.onRawTouchPointListReceived(shape, pointList);
        }
    }

    private Shape createNewShape() {
        Shape shape = ShapeFactory.createShape(getNoteDocument().getNoteDrawingArgs().currentShapeType);
        shape.setStrokeWidth(getNoteDocument().getStrokeWidth());
        shape.setColor(getNoteDocument().getStrokeColor());
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
        erasePoints.add(new TouchPoint(motionEvent.getX(), motionEvent.getY(), motionEvent.getPressure(), motionEvent.getSize(), motionEvent.getEventTime()));
        return true;
    }

    private void onFinishErasing() {
        if (callback != null) {
            callback.onEraseTouchPointListReceived(erasePoints);
        }
        shortcutErasing = false;
    }

    public List<Shape> getDirtyStash() {
        return dirtyStash;
    }

    public List<Shape> deatchStash() {
        final List<Shape> temp = dirtyStash;
        dirtyStash = new ArrayList<Shape>();
        return temp;
    }

    public PenState getPenState() {
        return getNoteDocument().getPenState();
    }

    public void setPenState(PenState penState) {
        getNoteDocument().setPenState(penState);
    }

    public void ensureErasing() {
        shortcutErasing = true;
    }

    public void updatePenStateByCurrentShapeType() {
        int type = getCurrentShapeType();
        if (ShapeFactory.isDFBShape(type)) {
            setPenState(PenState.PEN_SCREEN_DRAWING);
        } else if (type == ShapeFactory.SHAPE_ERASER) {
            setPenState(PenState.PEN_USER_ERASING);
        } else {
            setPenState(PenState.PEN_CANVAS_DRAWING);
        }
    }

    public boolean inErasing() {
        return (shortcutErasing || getPenState() == PenState.PEN_USER_ERASING);
    }

    public boolean inUserErasing() {
        return getPenState() == PenState.PEN_USER_ERASING;
    }

    public int getCurrentShapeType() {
        return getNoteDocument().getNoteDrawingArgs().currentShapeType;
    }

    public void setCurrentShapeType(int currentShapeType) {
        getNoteDocument().getNoteDrawingArgs().currentShapeType = currentShapeType;
        updatePenStateByCurrentShapeType();
    }

    private boolean useRawInput() {
        if (deviceConfig == null) {
            return false;
        }
        return deviceConfig.useRawInput();
    }

    private boolean renderByFramework() {
        return ShapeFactory.isDFBShape(getCurrentShapeType());
    }

    private boolean isSingleTouch() {
        if (deviceConfig == null) {
            return false;
        }
        return deviceConfig.isSingleTouch();
    }

    public boolean useDFBForCurrentState() {
        return ShapeFactory.isDFBShape(getCurrentShapeType()) && !inUserErasing();
    }

    private boolean processTouchEvent(final MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() > 1) {
            return true;
        }
        int toolType = motionEvent.getToolType(0);
        if (toolType == MotionEvent.TOOL_TYPE_FINGER && !isSingleTouch()) {
            return true;
        }

        if (toolType == MotionEvent.TOOL_TYPE_ERASER || inErasing()) {
            return forwardErasing(motionEvent);
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
        currentShape = createNewShape();
        beforeDownMessage(currentShape);
        dirtyStash.add(currentShape);
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        currentShape.onDown(normalized, screen);
        if (callback != null && !currentShape.supportDFB()) {
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
            if (callback != null && !currentShape.supportDFB()) {
                callback.onDrawingTouchMove(motionEvent, currentShape);
            }
        }
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);;
        currentShape.onMove(normalized, screen);
        if (callback != null && !currentShape.supportDFB()) {
            callback.onDrawingTouchMove(motionEvent, currentShape);
        }
    }

    private void onDrawingTouchUp(final MotionEvent motionEvent) {
        if (currentShape == null) {
            return;
        }
        final TouchPoint normalized = new TouchPoint(motionEvent);
        final TouchPoint screen = touchPointFromNormalized(normalized);
        currentShape.onUp(normalized, screen);
        if (callback != null && !currentShape.supportDFB()) {
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

}
