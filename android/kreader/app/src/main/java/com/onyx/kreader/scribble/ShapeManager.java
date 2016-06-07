package com.onyx.kreader.scribble;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.RequestManager;
import com.onyx.kreader.scribble.data.UndoRedoManager;
import com.onyx.kreader.scribble.request.BaseScribbleRequest;

/**
 * manage all shape pages associated with specified document.
 */
public class ShapeManager {

    private static final String TAG = ShapeManager.class.getSimpleName();

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

}
