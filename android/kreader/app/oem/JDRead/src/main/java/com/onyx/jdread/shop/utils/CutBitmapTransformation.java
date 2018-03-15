package com.onyx.jdread.shop.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.onyx.jdread.R;
import com.onyx.jdread.util.Utils;

/**
 * Created by tanmojie on 2016/8/6.
 */
public class CutBitmapTransformation extends BitmapTransformation {

    public static final float RADIO_CROP_WIDTH = Utils.getValuesFloat(R.integer.radio_crop_width);
    public static final float NO_CROP_PERCENT = Utils.getValuesFloat(R.integer.no_crop_percent);
    private static CutBitmapTransformation transformation;

    private CutBitmapTransformation(Context context) {
        super(context);
    }

    public static CutBitmapTransformation getInstance(Context context) {
        if (transformation == null) {
            synchronized (CutBitmapTransformation.class) {
                if (transformation == null) {
                    transformation = new CutBitmapTransformation(context);
                }
            }
        }
        return transformation;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        try {
            if (toTransform == null) {
                return toTransform;
            }
            float ratio = (float) toTransform.getWidth() / (float) toTransform.getHeight();
            if (ratio <= NO_CROP_PERCENT) {
                return toTransform;
            }
            float leftCropRatio = (ratio - RADIO_CROP_WIDTH) / Utils.getValuesFloat(R.integer.two_point_zero);
            int targetWidth = toTransform.getWidth();
            int x = 0;
            targetWidth = (int) (RADIO_CROP_WIDTH * toTransform.getHeight());
            x = (int) (leftCropRatio * toTransform.getHeight());
            if (!toTransform.isRecycled()) {

                toTransform = Bitmap.createBitmap(toTransform, x, 0, targetWidth,
                        toTransform.getHeight());
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return toTransform;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
