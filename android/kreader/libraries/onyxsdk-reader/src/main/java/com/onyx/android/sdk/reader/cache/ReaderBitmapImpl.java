package com.onyx.android.sdk.reader.cache;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.reader.host.options.BaseOptions;

/**
 * Created by joy on 8/9/16.
 */
public class ReaderBitmapImpl implements ReaderBitmap {

    private String key;
    private Bitmap bitmap;
    private float gammaCorrection = BaseOptions.getLowerGammaLimit();
    private int emboldenLevel;

    public static ReaderBitmapImpl create(int width, int height, Bitmap.Config config) {
        ReaderBitmapImpl readerBitmap = new ReaderBitmapImpl(width, height, config);
        return readerBitmap;
    }

    public ReaderBitmapImpl() {
        super();
    }

    public ReaderBitmapImpl(final String key, final Bitmap bitmap) {
        this.key = key;
        this.bitmap = bitmap;
    }

    public ReaderBitmapImpl(int width, int height, Bitmap.Config config) {
        super();
        bitmap = Bitmap.createBitmap(width, height, config);
    }

    public boolean isValid() {
        return BitmapUtils.isValid(bitmap);
    }

    public void clear() {
        if (bitmap != null) {
            bitmap.eraseColor(Color.WHITE);
        }
    }

    public void recycleBitmap() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
    }

    public String getKey() {
        return key;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setGammaCorrection(float correction) {
        this.gammaCorrection = correction;
    }

    public float gammaCorrection() {
        return gammaCorrection;
    }

    public boolean isGammaApplied(final float targetGammaCorrection) {
        return (Float.compare(gammaCorrection, targetGammaCorrection) == 0);
    }

    public boolean isEmboldenApplied(final float targetEmboldenLevel) {
        return emboldenLevel == targetEmboldenLevel;
    }

    public int getEmboldenLevel() {
        return emboldenLevel;
    }

    public void setEmboldenLevel(int emboldenLevel) {
        this.emboldenLevel = emboldenLevel;
    }

    public boolean attachWith(String key, final Bitmap src) {
        this.key = key;
        bitmap = src;
        return BitmapUtils.isValid(bitmap);
    }
}
