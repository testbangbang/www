package com.onyx.kreader.scribble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.RequestManager;
import com.onyx.kreader.dataprovider.request.BaseDataProviderRequest;
import com.onyx.kreader.scribble.request.BaseScribbleRequest;

import java.util.List;

public class ScribbleManager {

    private static final String TAG = ScribbleManager.class.getSimpleName();

    private Rect limitRect = null;
    private float eraserRadius = 15.0f;

    public static final int KEYCDOE_SCRIBBLER = 213;
    public static final int KEYCDOE_ERASE = 214;
    public static int digitizerId = 1;

    private RequestManager requestManager = new RequestManager();

    public ScribbleManager() {
    }

    public float getEraserRadius() {
        return eraserRadius;
    }

    private final Runnable generateRunnable(final BaseScribbleRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.execute(ScribbleManager.this);
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
