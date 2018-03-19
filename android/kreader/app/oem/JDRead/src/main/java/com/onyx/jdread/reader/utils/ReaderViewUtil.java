package com.onyx.jdread.reader.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.reader.ui.ReaderActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ReaderViewUtil {
    private static final String TAG = ReaderViewUtil.class.getSimpleName();
    private static boolean mIsFullUpdate = false;
    public static final int BUFFER_SIZE = 1024;

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
        }
        return input;
    }

    public static boolean readAssetsFile(AssetManager am, String srcFileName, String destPath) {
        InputStream is = null;
        FileOutputStream fos = null;
        boolean bRet = true;
        try {
            is = am.open(srcFileName);
            fos = new FileOutputStream(new File(destPath));
            int len = 0;
            byte[] buf = new byte[BUFFER_SIZE];
            while (true) {
                len = is.read(buf);
                if (len < 0) {
                    break;
                }
                fos.write(buf, 0, len);
            }
        } catch (Exception e) {
            bRet = false;
        } finally {
            FileUtils.closeQuietly(fos);
            FileUtils.closeQuietly(is);
        }

        return bRet;
    }

    static public String getKey(String message) {
        return FileUtils.computeMD5(message);
    }

    public static void updateReadingTime(final Context context, final String md5, final long readingTime) {
        Intent intent = new Intent();
        intent.setAction(ReaderConfig.BOOK_SINGLE_READ_TIME);
        intent.putExtra(ReaderConfig.BOOK_MD5, md5);
        intent.putExtra(ReaderConfig.BOOK_READING_TIME, readingTime);
        context.sendBroadcast(intent);
    }

    public static void applyFastModeByConfig() {
        if (JDPreferenceManager.getBooleanValue(R.string.speed_refresh_key, false)) {
            EpdController.applyApplicationFastMode(ReaderActivity.class.getSimpleName(), true, true);
        }
    }

    public static void clearFastModeByConfig() {
        if (JDPreferenceManager.getBooleanValue(R.string.speed_refresh_key, false)) {
            EpdController.applyApplicationFastMode(ReaderActivity.class.getSimpleName(), false, true);
        }
    }
}
