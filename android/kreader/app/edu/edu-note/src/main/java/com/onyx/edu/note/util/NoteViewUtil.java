package com.onyx.edu.note.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;

/**
 * Created by solskjaer49 on 2017/6/23 16:26.
 */

public class NoteViewUtil {
    private static boolean mIsFullUpdate = false;

    public static void clearSurfaceView(SurfaceView surfaceView) {
        Rect rect = getViewportSize(surfaceView);
        Canvas canvas = getCanvasForDraw(surfaceView, rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        resetViewPortBackground(canvas, paint, rect);
        unlockDrawingCanvas(surfaceView, canvas);
    }

    private static Canvas getCanvasForDraw(SurfaceView surfaceView, Rect rect) {
        if (mIsFullUpdate) {
            EpdController.setViewDefaultUpdateMode(surfaceView, UpdateMode.GC);
        } else {
            EpdController.resetUpdateMode(surfaceView);
        }
        return surfaceView.getHolder().lockCanvas(rect);
    }

    private static void resetViewPortBackground(final Canvas canvas, final Paint paint, final Rect rect) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);
    }

    private static Rect getViewportSize(View view) {
        return new Rect(0, 0, view.getWidth(), view.getHeight());
    }

    public static void setFullUpdate(boolean isFullUpdate) {
        mIsFullUpdate = isFullUpdate;
    }

    private static void unlockDrawingCanvas(SurfaceView surfaceView, Canvas canvas) {
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
        mIsFullUpdate = false;
    }
}
