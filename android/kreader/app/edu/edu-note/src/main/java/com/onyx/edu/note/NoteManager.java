package com.onyx.edu.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.data.NoteDocument;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.NoteViewUtil;
import com.onyx.edu.note.actions.scribble.DocumentFlushAction;
import com.onyx.edu.note.actions.scribble.DrawPageAction;
import com.onyx.edu.note.actions.scribble.RemoveByPointListAction;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/2/11 18:44.
 */

public class NoteManager {
    private static final String TAG = NoteManager.class.getSimpleName();

    public RequestManager getRequestManager() {
        return requestManager;
    }

    private RequestManager requestManager;
    private NoteViewHelper mNoteViewHelper;
    private static NoteManager instance;
    //TODO:use WeakReference here avoid context leak in static class as AndroidStudio lint check.
    private WeakReference<Context> contextWeakReference;
    private ShapeDataInfo shapeDataInfo = new ShapeDataInfo();
    private NoteViewHelper.InputCallback mInputCallback;

    private TouchPoint mErasePoint = null;


    private NoteManager(Context context) {
        requestManager = new RequestManager(Thread.NORM_PRIORITY);
        if (mNoteViewHelper == null) {
            mNoteViewHelper = new NoteViewHelper();
        }
        contextWeakReference = new WeakReference<>(context.getApplicationContext());
    }

    static public NoteManager sharedInstance(Context context) {
        if (instance == null) {
            instance = new NoteManager(context);
        }
        return instance;
    }

    private Runnable generateRunnable(final AsyncBaseNoteRequest request) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(mNoteViewHelper);
                    request.execute(mNoteViewHelper);
                } catch (Throwable tr) {
                    request.setException(tr);
                } finally {
                    request.postExecute(mNoteViewHelper);
                    requestManager.dumpWakelocks();
                    requestManager.removeRequest(request);
                }
            }
        };
    }

    private void beforeSubmit(AsyncBaseNoteRequest request) {
        final Rect rect = mNoteViewHelper.getViewportSize();
        if (rect != null) {
            request.setViewportSize(rect);
        }
    }

    public boolean submitRequest(final AsyncBaseNoteRequest request, final BaseCallback callback) {
        beforeSubmit(request);
        if (contextWeakReference.get() != null) {
            return requestManager.submitRequestToMultiThreadPool(contextWeakReference.get(),
                    request, generateRunnable(request), callback);
        } else {
            Log.e(TAG, "Context has been GC");
            return false;
        }
    }

    public boolean submitRequestWithIdentifier(final AsyncBaseNoteRequest request, final String identifier,
                                               final BaseCallback callback) {
        beforeSubmit(request);
        request.setIdentifier(identifier);
        if (contextWeakReference.get() != null) {
            return requestManager.submitRequestToMultiThreadPool(contextWeakReference.get(), identifier,
                    request, generateRunnable(request), callback);
        } else {
            Log.e(TAG, "Context has been GC");
            return false;
        }
    }

    public void sync(boolean render,
                     boolean resume) {
        syncWithCallback(render, resume, null);
    }

    public void syncWithCallback(boolean render,
                                 boolean resume,
                                 final BaseCallback callback) {
        final List<Shape> stash = detachStash();
        if (isLineLayoutMode()) {
            stash.clear();
        }
        final DocumentFlushAction action = new DocumentFlushAction(stash,
                render,
                resume,
                shapeDataInfo.getDrawingArgs());
        action.execute(this, callback);
    }

    public ShapeDataInfo getShapeDataInfo() {
        return shapeDataInfo;
    }

    public void setShapeDataInfo(ShapeDataInfo shapeDataInfo) {
        this.shapeDataInfo = shapeDataInfo;
    }

    public void setStrokeWidth(float strokeWidth,BaseCallback callback){
        if (shapeDataInfo.isInUserErasing()) {
            shapeDataInfo.setCurrentShapeType(ShapeFactory.SHAPE_PENCIL_SCRIBBLE);
        }
        shapeDataInfo.setStrokeWidth(strokeWidth);
        syncWithCallback(true, true, callback);
    }

    //TODO:avoid direct obtain note view helper,because we plan to remove this class.

    public void reset(View view) {
        mNoteViewHelper.reset(view);
    }

    public View getView() {
        return mNoteViewHelper.getView();
    }

    public void setView(Context context, SurfaceView surfaceView) {
        mNoteViewHelper.setView(context, surfaceView, getInputCallback());
    }

    private NoteViewHelper.InputCallback getInputCallback() {
        if (mInputCallback == null) {
            mInputCallback = new NoteViewHelper.InputCallback() {
                @Override
                public void onBeginRawData() {
                }

                @Override
                public void onRawTouchPointListReceived(final Shape shape, TouchPointList pointList) {
                }

                @Override
                public void onBeginErasing() {
                    NoteManager.this.onBeginErasing();
                }

                @Override
                public void onErasing(final MotionEvent touchPoint) {
                    NoteManager.this.onErasing(touchPoint);
                }

                @Override
                public void onEraseTouchPointListReceived(TouchPointList pointList) {
                    onFinishErasing(pointList);
                }

                @Override
                public void onDrawingTouchDown(final MotionEvent motionEvent, final Shape shape) {
                    if (!shape.supportDFB()) {
                        drawCurrentPage();
                    }
                }

                @Override
                public void onDrawingTouchMove(final MotionEvent motionEvent, final Shape shape, boolean last) {
                    if (last && !shape.supportDFB()) {
                        drawCurrentPage();
                    }
                }

                @Override
                public void onDrawingTouchUp(final MotionEvent motionEvent, final Shape shape) {
                    if (!shape.supportDFB()) {
                        drawCurrentPage();
                    }
                }

            };
        }
        return mInputCallback;
    }

    protected void onBeginErasing() {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                mErasePoint = new TouchPoint();
            }
        });
    }

    protected void onErasing(final MotionEvent touchPoint) {
        if (mErasePoint == null) {
            return;
        }
        mErasePoint.x = touchPoint.getX();
        mErasePoint.y = touchPoint.getY();
    }

    protected void onFinishErasing(TouchPointList pointList) {
        mErasePoint = null;
        new RemoveByPointListAction(pointList).execute(this, null);
    }

    private void drawCurrentPage() {
        new DrawPageAction().execute(this, null);
    }

    public void quit() {
        mNoteViewHelper.quit();
    }

    public boolean isLineLayoutMode() {
        return mNoteViewHelper.isLineLayoutMode();
    }

    public List<Shape> detachStash() {
        return mNoteViewHelper.detachStash();
    }

    public void clearSurfaceView(SurfaceView surfaceView) {
        NoteViewUtil.clearSurfaceView(surfaceView);
    }

    public void setDrawing(boolean drawing) {
        mNoteViewHelper.setDrawing(drawing);
    }

    public boolean isDrawing() {
        return mNoteViewHelper.isDrawing();
    }

    public boolean inUserErasing() {
        return mNoteViewHelper.inUserErasing();
    }

    public NoteDocument getNoteDocument() {
        return mNoteViewHelper.getNoteDocument();
    }

    public void pauseDrawing() {
        mNoteViewHelper.pauseDrawing();
    }

    public void resumeDrawing() {
        mNoteViewHelper.resumeDrawing();
    }

    public Bitmap getViewBitmap() {
        return mNoteViewHelper.getViewBitmap();
    }

    public Bitmap getRenderBitmap() {
        return mNoteViewHelper.getRenderBitmap();
    }

    public List<Shape> getDirtyShape() {
        return mNoteViewHelper.getDirtyStash();
    }
}
