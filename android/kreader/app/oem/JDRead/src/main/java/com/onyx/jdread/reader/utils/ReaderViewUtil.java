package com.onyx.jdread.reader.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.utils.StringUtils;

public class ReaderViewUtil {
    private static final String TAG = ReaderViewUtil.class.getSimpleName();
    private static boolean mIsFullUpdate = false;

    public static void clearSurfaceView(SurfaceView surfaceView) {
        Rect rect = getViewportSize(surfaceView);
        Canvas canvas = surfaceView.getHolder().lockCanvas(rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        resetViewPortBackground(canvas, paint, rect);
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
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

    private static void resetFullUpdate() {
        mIsFullUpdate = false;
    }

    private static void unlockDrawingCanvas(SurfaceView surfaceView, Canvas canvas) {
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
        mIsFullUpdate = false;
    }

    public static String trim(String input) {
        if (StringUtils.isNotBlank(input)) {
            input = input.trim();
            input = input.replace("\u0032", "");
            input = input.replace("\\u0032", "");
            input = input.replaceAll("\\u0032", ""); // removes NUL chars
            input = input.replaceAll("\\\\u0032", ""); // removes backslash+u0000
        }
        return input;
    }
}
