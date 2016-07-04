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
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.NormalScribbleShape;
import com.onyx.android.sdk.scribble.shape.Shape;
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

    private RequestManager requestManager = new RequestManager(Thread.NORM_PRIORITY);
    private RawInputProcessor rawInputProcessor = new RawInputProcessor();
    private NoteDocument noteDocument = new NoteDocument();
    private ReaderBitmapImpl renderBitmapWrapper = new ReaderBitmapImpl();
    private ReaderBitmapImpl viewBitmapWrapper = new ReaderBitmapImpl();
    private boolean inErasing = false;
    private Rect limitRect = null;
    private volatile SurfaceView surfaceView;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private List<Shape> dirtyStash = new ArrayList<Shape>();
    private RawInputProcessor.InputCallback callback;

    public void setView(final Context context, final SurfaceView view, final RawInputProcessor.InputCallback c) {
        setCallback(c);
        initRawResource(context);
        initWithSurfaceView(view);
        initRawInputProcessor();
        updateScreenMatrix();
        updateViewMatrix();
        updateLimitRect();
        stopDrawing();
    }

    public void stop() {
        stopDrawing();
        quitDrawing();
        removeLayoutListener();
    }

    public void openDocument(final Context context, final String documentUniqueId, final String parentUniqueId) {
        getNoteDocument().open(context, documentUniqueId, parentUniqueId);
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
    }

    private void initWithSurfaceView(final SurfaceView view) {
        surfaceView = view;
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!inErasing) {
                    return true;
                }
                return onErasing(motionEvent);
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

    private float getEpdWidth() {
        final float epdWidth = 1200;
        return epdWidth;
    }

    private float getEpdHeight() {
        final float epdHeight = 825;
        return epdHeight;
    }

    private int getEpdOrientation() {
        return 90;
    }

    private int getTouchOrientation() {
        return 90;
    }

    private int getTouchWidth() {
        return 8192;
    }

    private int getTouchHeight() {
        return 6144;
    }

    // matrix from input touch panel to system view with correct orientation.
    private void updateScreenMatrix() {
        final Matrix screenMatrix = new Matrix();
        screenMatrix.postRotate(getEpdOrientation());
        screenMatrix.postTranslate(getEpdHeight(), 0);
        screenMatrix.preScale((float) getEpdWidth() / (float) getTouchWidth(),
                (float) getEpdHeight() / (float) getTouchHeight());
        rawInputProcessor.setScreenMatrix(screenMatrix);
    }

    // consider view offset to screen.
    private void updateViewMatrix() {
        int viewPosition[] = {0, 0};
        surfaceView.getLocationOnScreen(viewPosition);
        final Matrix viewMatrix = new Matrix();
        viewMatrix.postTranslate(-viewPosition[0], -viewPosition[1]);
        viewMatrix.postScale(1.0f / surfaceView.getWidth(), 1.0f / surfaceView.getHeight());
        rawInputProcessor.setViewMatrix(viewMatrix);
    }

    // matrix from android view to epd.
    private Matrix matrixFromViewToEpd() {
        final Matrix matrix = new Matrix();
        matrix.postRotate(360 - getEpdOrientation());
        matrix.postTranslate(0, getEpdHeight());
        return matrix;
    }

    private void updateLimitRect() {
        limitRect = new Rect();
        surfaceView.getGlobalVisibleRect(limitRect);
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

    public void startDrawing() {
        getRawInputProcessor().start();
    }

    public void stopDrawing() {
        getRawInputProcessor().stop();
    }

    public void quitDrawing() {
        getRawInputProcessor().quit();
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
            public void onBeginHandWriting() {
            }

            @Override
            public void onNewTouchPointListReceived(TouchPointList pointList) {
                NoteViewHelper.this.onNewTouchPointListReceived(pointList);
            }

            @Override
            public void onBeginErasing() {
                NoteViewHelper.this.onBeginErasing();
            }

            @Override
            public void onErasing(final MotionEvent touchPoint) {
            }

            @Override
            public void onEraseTouchPointListReceived(TouchPointList pointList) {
                NoteViewHelper.this.onFinishErasing(pointList);
            }
        });
    }

    private void onNewTouchPointListReceived(final TouchPointList pointList) {
        Shape shape = new NormalScribbleShape();
        shape.addPoints(pointList);
        dirtyStash.add(shape);
        if (callback != null) {
            callback.onNewTouchPointListReceived(pointList);
        }
    }

    private void onBeginErasing() {
        inErasing = true;
        if (callback != null) {
            callback.onBeginErasing();
        }
    }

    private boolean onErasing(final MotionEvent motionEvent) {
        if (callback != null) {
            callback.onErasing(motionEvent);
        }
        return true;
    }

    private void onFinishErasing(TouchPointList pointList) {
        inErasing = false;
        if (callback != null) {
            callback.onEraseTouchPointListReceived(pointList);
        }
    }

    public List<Shape> deatchStash() {
        final List<Shape> temp = dirtyStash;
        dirtyStash = new ArrayList<Shape>();
        return temp;
    }

}
