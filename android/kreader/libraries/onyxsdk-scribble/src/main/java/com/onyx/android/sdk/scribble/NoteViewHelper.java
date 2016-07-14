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
import com.onyx.android.sdk.scribble.data.NoteDocument;
import com.onyx.android.sdk.scribble.data.RawInputProcessor;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.LineShape;
import com.onyx.android.sdk.scribble.shape.NormalScribbleShape;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

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
        PEN_DRAWING,                // in drawing state
        PEN_ERASER_DRAWING,         // in drawing state, but use eraser
        PEN_USER_ERASING,           // in user erasing state
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
    private RawInputProcessor.InputCallback callback;
    private TouchPointList erasePoints;
    private DeviceConfig deviceConfig;
    private volatile int currentShapeType = ShapeFactory.SHAPE_NORMAL_SCRIBBLE;
    private Shape currentShape = null;
    private PenState penState;

    public void reset(final View view) {
        EpdController.setScreenHandWritingPenState(view, 3);
        EpdController.enablePost(view, 1);
    }

    public void setView(final Context context, final SurfaceView view, final RawInputProcessor.InputCallback c) {
        setCallback(c);
        initRawResource(context);
        initWithSurfaceView(view);
        initRawInputProcessor();
        updateScreenMatrix();
        updateViewMatrix();
        updateLimitRect();
        pauseDrawing();
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

    private void setCallback(final RawInputProcessor.InputCallback c) {
        callback = c;
    }

    // matrix from input touch panel to system view with correct orientation.
    private void updateScreenMatrix() {
        final Matrix screenMatrix = new Matrix();
        screenMatrix.postRotate(deviceConfig.getEpdPostOrientation());
        screenMatrix.postTranslate(deviceConfig.getEpdPostTx(), deviceConfig.getEpdPostTy());
        screenMatrix.preScale(deviceConfig.getEpdWidth() / deviceConfig.getTouchWidth(),
                deviceConfig.getEpdHeight() / deviceConfig.getTouchHeight());
        rawInputProcessor.setScreenMatrix(screenMatrix);
    }

    // consider view offset to screen.
    private void updateViewMatrix() {
        int viewPosition[] = {0, 0};
        surfaceView.getLocationOnScreen(viewPosition);
        final Matrix viewMatrix = new Matrix();
        viewMatrix.postTranslate(-viewPosition[0], -viewPosition[1]);
        rawInputProcessor.setViewMatrix(viewMatrix);
    }

    // matrix from android view to epd.
    private Matrix matrixFromViewToEpd() {
        final Matrix matrix = new Matrix();
        matrix.postRotate(deviceConfig.getViewPostOrientation());
        matrix.postTranslate(deviceConfig.getViewPostTx(), deviceConfig.getViewPostTy());
        return matrix;
    }

    private void updateLimitRect() {
        limitRect = new Rect();
        surfaceView.getGlobalVisibleRect(limitRect);
        limitRect.offsetTo(0, 0);
        rawInputProcessor.setLimitRect(limitRect);

        int viewPosition[] = {0, 0};
        surfaceView.getLocationOnScreen(viewPosition);
        limitRect.offsetTo(viewPosition[0], viewPosition[1]);

        final Matrix matrix = matrixFromViewToEpd();
        ShapeUtils.mapInPlace(limitRect, matrix);
        EpdController.setScreenHandWritingRegionLimit(surfaceView,
                Math.min(limitRect.left, limitRect.right),
                Math.min(limitRect.top, limitRect.bottom),
                Math.max(limitRect.left, limitRect.right),
                Math.max(limitRect.top, limitRect.bottom));
    }

    private void startDrawing() {
        getRawInputProcessor().start();
    }

    public void resumeDrawing() {
        setPenState(PenState.PEN_DRAWING);
        getRawInputProcessor().resume();
    }

    public void pauseDrawing() {
        getRawInputProcessor().pause();
    }

    public void enableScreenPost() {
        if (surfaceView != null) {
            EpdController.enablePost(surfaceView, 1);
        }
    }

    public void quitDrawing() {
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
        rawInputProcessor.setParentView(surfaceView);
        rawInputProcessor.setInputCallback(new RawInputProcessor.InputCallback() {
            @Override
            public void onBeginRawData() {
            }

            @Override
            public void onRawTouchPointListReceived(final Shape shape, TouchPointList pointList) {
                NoteViewHelper.this.onNewTouchPointListReceived(pointList);
            }

            public void onDrawingTouchDown(final MotionEvent motionEvent, final Shape shape) {

            }

            public void onDrawingTouchMove(final MotionEvent motionEvent, final Shape shape) {
            }

            public void onDrawingTouchUp(final MotionEvent motionEvent, final Shape shape) {
            }

            @Override
            public void onBeginErasing() {
                ensureErasing();
            }

            @Override
            public void onErasing(final MotionEvent touchPoint) {
            }

            @Override
            public void onEraseTouchPointListReceived(TouchPointList pointList) {

            }
        });
        startDrawing();
    }

    private void onNewTouchPointListReceived(final TouchPointList pointList) {
        Shape shape = createNewShape();
        shape.addPoints(pointList);
        dirtyStash.add(shape);
        if (callback != null) {
            callback.onRawTouchPointListReceived(shape, pointList);
        }
    }

    private Shape createNewShape() {
        Shape shape = null;
        switch (currentShapeType) {
            case ShapeFactory.SHAPE_NORMAL_SCRIBBLE:
                shape = new NormalScribbleShape();
                break;
            case ShapeFactory.SHAPE_LINE:
                shape = new LineShape();
                break;
            default:
                shape = new NormalScribbleShape();
        }
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
    }

    public List<Shape> deatchStash() {
        final List<Shape> temp = dirtyStash;
        dirtyStash = new ArrayList<Shape>();
        return temp;
    }

    public PenState getPenState() {
        return penState;
    }

    public void setPenState(PenState penState) {
        this.penState = penState;
    }

    public void ensureErasing() {
        if (!inUserErasing()) {
            setPenState(PenState.PEN_ERASER_DRAWING);
        }
    }

    public boolean inErasing() {
        return (penState == PenState.PEN_ERASER_DRAWING || penState == PenState.PEN_USER_ERASING);
    }

    public boolean inUserErasing() {
        return penState == PenState.PEN_USER_ERASING;
    }

    public int getCurrentShapeType() {
        return currentShapeType;
    }

    public void setCurrentShapeType(int currentShapeType) {
        this.currentShapeType = currentShapeType;
    }

    private boolean useRawData() {
        return deviceConfig.useRawInput() && ShapeFactory.isDFBShape(currentShapeType);
    }

    private boolean processTouchEvent(final MotionEvent motionEvent) {
        if (useRawData()) {
            return processRawDataTouchEvent(motionEvent);
        }
        return processNormalTouchEvent(motionEvent);
    }

    private boolean processRawDataTouchEvent(final MotionEvent motionEvent) {
        if (!inErasing()) {
            return true;
        }
        return forwardErasing(motionEvent);
    }

    private boolean processNormalTouchEvent(final MotionEvent motionEvent) {
        if (inErasing()) {
            return forwardErasing(motionEvent);
        }
        return forwardDrawing(motionEvent);
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

    private void onDrawingTouchDown(final MotionEvent motionEvent) {
        currentShape = createNewShape();
        dirtyStash.add(currentShape);
        currentShape.onDown(new TouchPoint(motionEvent), new TouchPoint(motionEvent));
        if (callback != null) {
            callback.onDrawingTouchDown(motionEvent, currentShape);
        }
    }

    private void onDrawingTouchMove(final MotionEvent motionEvent) {
        currentShape.onMove(new TouchPoint(motionEvent), new TouchPoint(motionEvent));
        if (callback != null) {
            callback.onDrawingTouchMove(motionEvent, currentShape);
        }
    }

    private void onDrawingTouchUp(final MotionEvent motionEvent) {
        currentShape.onUp(new TouchPoint(motionEvent), new TouchPoint(motionEvent));
        if (callback != null) {
            callback.onDrawingTouchUp(motionEvent, currentShape);
        }
    }


}
