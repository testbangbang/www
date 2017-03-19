package com.onyx.android.sdk.reader.utils;

import android.graphics.Bitmap;
import android.graphics.RectF;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;

import java.util.List;

/**
 * Created by joy on 3/22/16.
 * kreader zhuzeng$ javah -classpath ./build/intermediates/classes/debug/:/opt/adt-bundle-linux/sdk/platforms/android-14/android.jar: -jni com.onyx.android.sdk.reader.utils.ImageUtils
 */
public class ImageUtils {
    public static final float NO_GAMMA = -1;
    public static final float MAX_GAMMA = 200;

    static {
        System.loadLibrary("neo_cropper");
    }

    static private native double [] crop(Bitmap bitmap, int left, int top, int right, int bottom, double threshold);
    static private native boolean emboldenInPlace(Bitmap bitmap, int level);
    static private native boolean gammaCorrection(Bitmap bitmap, float gamma, float [] regions);

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

    public static float getGammaCorrectionBySelection(float selection) {
        // selection range [minGammaLevel, maxGammaLevel], the bigger the darker.
        // add mapping here. gamma range [0.5, 100]
        // selection 200 ->  gamma 0.5
        // selection 1   ->  gamma 100
        float value = MAX_GAMMA - selection;
        if (value <= 0) {
            value = 1;
        }
        return (MAX_GAMMA / 2) / value;
    }

    /**
     * modify bitmap in place
     *
     * @param bmp
     * @param selection
     * @return
     */
    public static boolean applyGammaCorrection(Bitmap bmp, float selection, final List<RectF> regions) {
        float gamma = getGammaCorrectionBySelection(selection);
        float [] array = null;
        if (regions != null) {
            array = new float[regions.size() * 4];
            for (int i = 0; i < regions.size(); ++i) {
                array[i * 4] = regions.get(i).left;
                array[i * 4 + 1] = regions.get(i).top;
                array[i * 4 + 2] = regions.get(i).right;
                array[i * 4 + 3] = regions.get(i).bottom;
            }
        }
        return gammaCorrection(bmp, gamma, array);
    }
}
