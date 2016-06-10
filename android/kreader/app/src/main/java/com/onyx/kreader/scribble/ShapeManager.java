package com.onyx.kreader.scribble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.common.RequestManager;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.scribble.data.UndoRedoManager;
import com.onyx.kreader.scribble.request.BaseScribbleRequest;

/**
 * manage all shape pages associated with specified document.
 */
public class ShapeManager {

    private static final String TAG = ShapeManager.class.getSimpleName();
    private ReaderBitmapImpl bitmapWrapper = new ReaderBitmapImpl();
    private boolean enableBitmap = true;

    private Rect limitRect = null;
    public static final int KEYCDOE_SCRIBBLER = 213;
    public static final int KEYCDOE_ERASE = 214;
    public static int digitizerId = 1;

    private RequestManager requestManager = new RequestManager();

    public ShapeManager() {
    }

    private final Runnable generateRunnable(final BaseScribbleRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(requestManager);
                    request.execute(ShapeManager.this);
                } catch (java.lang.Exception exception) {
                    Log.d(TAG, Log.getStackTraceString(exception));
                    request.setException(exception);
                } finally {
                    request.afterExecute(requestManager);
                    requestManager.dumpWakelocks();
                    requestManager.removeRequest(request);
                }
            }
        };
        return runnable;
    }

    public void submit(final Context context, final BaseScribbleRequest request, final BaseCallback callback) {
        requestManager.submitRequest(context, request, generateRunnable(request), callback);
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
    }
}
