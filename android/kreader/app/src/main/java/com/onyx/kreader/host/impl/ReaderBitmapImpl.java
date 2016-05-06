package com.onyx.kreader.host.impl;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.onyx.kreader.api.ReaderBitmap;

/**
 * Created by zhuzeng on 10/4/15.
 */
public class ReaderBitmapImpl implements ReaderBitmap {

    private Bitmap bitmap;

    public static ReaderBitmapImpl create(int width, int height, Bitmap.Config config) {
        ReaderBitmapImpl readerBitmap = new ReaderBitmapImpl(width, height, config);
        return readerBitmap;
    }

    public ReaderBitmapImpl() {
        super();
    }

    public ReaderBitmapImpl(int width, int height, Bitmap.Config config) {
        super();
        bitmap = Bitmap.createBitmap(width, height, config);
    }

    public void clear() {
        if (bitmap != null) {
            bitmap.eraseColor(Color.WHITE);
        }
    }

    public void recycleBitmap() {
        if (bitmap != null) {
            bitmap.recycle();
        }
        bitmap = null;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void update(int width, int height, Bitmap.Config config) {
        if (bitmap == null || bitmap.getWidth() != width || bitmap.getHeight() != height) {
            recycleBitmap();
            bitmap = Bitmap.createBitmap(width, height, config);
        }
    }

    public void copyFrom(final Bitmap src) {
        recycleBitmap();
        bitmap = src.copy(src.getConfig(), true);
    }
}
