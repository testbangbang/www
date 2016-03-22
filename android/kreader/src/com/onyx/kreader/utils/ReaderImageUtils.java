package com.onyx.kreader.utils;

import android.graphics.*;
import com.onyx.kreader.reflow.ReaderScannedPageReflowManager;
import com.onyx.kreader.reflow.ReaderScannedPageReflowSettings;

/**
 * Created by joy on 3/22/16.
 */
public class ReaderImageUtils {
    public static final float NO_GAMMA = -1;
    public static final float STANDARD_GAMMA = 150;
    public static final float MAX_GAMMA = 200;

    static {
        System.loadLibrary("onyx_cropper");
    }

    static private native double [] crop(Bitmap bitmap, int left, int top, int right, int bottom, double threshold);
    static private native boolean reflowPage(Bitmap input, final String pageName, ReaderScannedPageReflowManager parent, ReaderScannedPageReflowSettings settings);
    static private native boolean emboldenInPlace(Bitmap bitmap, int level);
    static private native boolean gammaCorrection(Bitmap bitmap, float gamma);

    /**
     * Return content region with specified bitmap. usually takes about 60ms to finish 1440x1080 bitmap.
     * @param bitmap The image bitmap.
     * @param left The sub left.
     * @param top  The sub top.
     * @param right The sub right.
     * @param bottom The sub bottom.
     * @return
     */
    static public RectF cropPage(Bitmap bitmap, int left, int top, int right, int bottom, double threshold) {
        double [] result = crop(bitmap, left, top, right, bottom, threshold);
        return RectUtils.rectangle(result);
    }

    static public boolean reflowScannedPage(Bitmap bitmap, final String pageName, ReaderScannedPageReflowManager parent) {
        return reflowPage(bitmap, pageName, parent, parent.getSettings());
    }

    static public void drawRectOnBitmap(Bitmap bmp, RectF rect) {
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawRect(rect, paint);
    }

    public static Bitmap emboldenBitmapInPlace(Bitmap bmp, int emboldenLevel) {
        emboldenInPlace(bmp, emboldenLevel);
        return bmp;
    }

    public static Bitmap applyGammaCorrection(Bitmap bmp, float selection) {
        // selection range [minGammaLevel, maxGammaLevel], the bigger the darker.
        // add mapping here. gamma range [0.5, 100]
        // selection 200 ->  gamma 0.5
        // selection 1   ->  gamma 100
        float value = MAX_GAMMA - selection;
        if (value <= 0) {
            value = 1;
        }
        float gamma = (MAX_GAMMA / 2) / value;
        gammaCorrection(bmp, gamma);
        return bmp;
    }
}
