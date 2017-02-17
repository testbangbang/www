package com.onyx.android.sdk.reader.utils;

import android.graphics.Bitmap;
import android.graphics.RectF;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;

/**
 * Created by joy on 3/22/16.
 * kreader zhuzeng$ javah -classpath ./build/intermediates/classes/debug/:/opt/adt-bundle-linux/sdk/platforms/android-14/android.jar:./com/onyx/kreader/utils
 * -jni ImageUtils
 */
public class ImageUtils {
    public static final float NO_GAMMA = -1;
    public static final float MAX_GAMMA = 200;

    static {
        System.loadLibrary("neo_cropper");
    }

    static private native double [] crop(Bitmap bitmap, int left, int top, int right, int bottom, double threshold);
    static private native boolean emboldenInPlace(Bitmap bitmap, int level);
    static private native boolean gammaCorrection(Bitmap bitmap, float gamma);

    static public native boolean reflowPage(String pageName, Bitmap input, ImageReflowSettings settings);
    static public native boolean isPageReflowed(String pageName);
    static public native boolean getReflowedPageSize(String pageName, int []size);
    static public native boolean renderReflowedPage(String pageName, int left, int top, int right, int bottom, final Bitmap bitmap);
    static public native void releaseReflowedPages();

    static public native void toRgbwBitmap(final Bitmap dst, final Bitmap src, int orientation);
    static public native void toColorBitmap(final Bitmap dst, final Bitmap src, int orientation);

    static public native boolean isValidPage();

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

    /**
     * modify bitmap in place
     *
     * @param bmp
     * @param emboldenLevel
     * @return
     */
    public static boolean applyBitmapEmbolden(Bitmap bmp, int emboldenLevel) {
        return emboldenInPlace(bmp, emboldenLevel);
    }

    /**
     * modify bitmap in place
     *
     * @param bmp
     * @param selection
     * @return
     */
    public static boolean applyGammaCorrection(Bitmap bmp, float selection) {
        // selection range [minGammaLevel, maxGammaLevel], the bigger the darker.
        // add mapping here. gamma range [0.5, 100]
        // selection 200 ->  gamma 0.5
        // selection 1   ->  gamma 100
        float value = MAX_GAMMA - selection;
        if (value <= 0) {
            value = 1;
        }
        float gamma = (MAX_GAMMA / 2) / value;
        return gammaCorrection(bmp, gamma);
    }
}
