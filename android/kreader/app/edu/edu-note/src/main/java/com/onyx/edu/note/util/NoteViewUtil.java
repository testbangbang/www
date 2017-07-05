package com.onyx.edu.note.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.List;

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

    public static void resetFullUpdate() {
        mIsFullUpdate = false;
    }

    private static void unlockDrawingCanvas(SurfaceView surfaceView, Canvas canvas) {
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
        mIsFullUpdate = false;
    }

    public static void drawPage(SurfaceView surfaceView, Bitmap viewBitmap, List<Shape> stashShapeList) {
        Rect rect = getViewportSize(surfaceView);
        Canvas canvas = getCanvasForDraw(surfaceView, rect);
        if (canvas == null) {
            resetFullUpdate();
            return;
        }

        Paint paint = new Paint();
        resetViewPortBackground(canvas, paint, rect);
        if (viewBitmap != null) {
            canvas.drawBitmap(viewBitmap, 0, 0, paint);
        }
        RenderContext renderContext = RenderContext.create(canvas, paint, null);
        for (Shape shape : stashShapeList) {
            shape.render(renderContext);
        }
        unlockDrawingCanvas(surfaceView, canvas);
    }
}
