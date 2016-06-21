package com.onyx.android.sdk.scribble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.scribble.data.RawInputReader;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.BaseScribbleRequest;
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
public class ShapeViewHelper {

    private static final String TAG = ShapeViewHelper.class.getSimpleName();

    private RequestManager requestManager = new RequestManager();
    private RawInputReader rawInputReader = new RawInputReader();
    private ReaderBitmapImpl bitmapWrapper = null;
    private boolean enableBitmap = true;

    private Rect limitRect = null;

    public void setView(final SurfaceView view) {
    }

    public void submit(final Context context, final BaseScribbleRequest request, final BaseCallback callback) {
        getRequestManager().submitRequest(context, request, generateRunnable(request), callback);
    }

    public final RequestManager getRequestManager() {
        return requestManager;
    }

    public final RawInputReader getRawInputReader() {
        return rawInputReader;
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


    private final Runnable generateRunnable(final BaseScribbleRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(ShapeViewHelper.this);
                    request.execute(ShapeViewHelper.this);
                } catch (java.lang.Exception exception) {
                    Log.d(TAG, Log.getStackTraceString(exception));
                    request.setException(exception);
                } finally {
                    request.afterExecute(ShapeViewHelper.this);
                    getRequestManager().dumpWakelocks();
                    getRequestManager().removeRequest(request);
                }
            }
        };
        return runnable;
    }

    private void init() {
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
    }


}
