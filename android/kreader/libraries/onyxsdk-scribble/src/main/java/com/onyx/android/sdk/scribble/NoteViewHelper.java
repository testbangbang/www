package com.onyx.android.sdk.scribble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.scribble.data.NoteDocument;
import com.onyx.android.sdk.scribble.data.RawInputReader;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.NormalScribbleShape;
import com.onyx.android.sdk.scribble.shape.Shape;

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

    private RequestManager requestManager = new RequestManager();
    private RawInputReader rawInputReader = new RawInputReader();
    private NoteDocument noteDocument = new NoteDocument();
    private ReaderBitmapImpl bitmapWrapper = null;
    private boolean enableBitmap = true;
    private Rect limitRect = null;
    private SurfaceView surfaceView;

    public void setView(final SurfaceView view) {
        surfaceView = view;
        initRawInputReader();
        updateScreenMatrix();
        updateViewMatrix();
        updateLimitRect();
        startDrawing();
    }

    private void updateScreenMatrix() {
        // read from conf or
    }

    private void updateViewMatrix() {

    }

    private void updateLimitRect() {

    }

    public void startDrawing() {
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        getRawInputReader().start();
        EpdController.enablePost(surfaceView, 0);
        //EpdController.startHandwriting();
    }

    public void starErasing() {
        getRawInputReader().stop();
        //EpdController.stopHandwriting();
    }

    public void stop() {
        getRawInputReader().stop();
        //EpdController.stopHandwriting();
    }

    public void submit(final Context context, final BaseNoteRequest request, final BaseCallback callback) {
        getRequestManager().submitRequest(context, request, generateRunnable(request), callback);
    }

    public final RequestManager getRequestManager() {
        return requestManager;
    }

    public final RawInputReader getRawInputReader() {
        return rawInputReader;
    }

    public final NoteDocument getNoteDocument() {
        return noteDocument;
    }

    public Bitmap updateBitmap(final Rect viewportSize) {
        bitmapWrapper.update(viewportSize.width(), viewportSize.height(), Bitmap.Config.ARGB_8888);
        return bitmapWrapper.getBitmap();
    }

    public Bitmap getShapeBitmap() {
        if (bitmapWrapper == null || !enableBitmap) {
            return null;
        }
        return bitmapWrapper.getBitmap();
    }

    public void enableBitmap(boolean enable) {
        enableBitmap = enable;
        if (enableBitmap && bitmapWrapper == null) {
            bitmapWrapper = new ReaderBitmapImpl();
        }
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

    private void initRawInputReader() {

        rawInputReader.setInputCallback(new RawInputReader.InputCallback() {
            @Override
            public void onBeginHandWriting() {

            }

            @Override
            public void onNewStrokeReceived(TouchPointList pointList) {
                // create shape and add to memory.
                Shape shape = new NormalScribbleShape();
                shape.addPoints(pointList);
                // send request and send to request manager.

            }

            @Override
            public void onBeginErase() {

            }

            @Override
            public void onEraseReceived(TouchPointList pointList) {

            }
        });
        final Matrix screenMatrix = new Matrix();
        screenMatrix.preScale(1600.0f / 10206.0f, 1200.0f / 7422.0f);
        rawInputReader.setScreenMatrix(screenMatrix);
    }

}
