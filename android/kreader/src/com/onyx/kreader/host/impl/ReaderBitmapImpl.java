package com.onyx.kreader.host.impl;

import android.graphics.Bitmap;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.utils.TimeUtils;

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
        recycleBitmap();
    }

    public ReaderBitmapImpl(int width, int height, Bitmap.Config config) {
        super();
        recycleBitmap();
        bitmap = Bitmap.createBitmap(width, height, config);
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
            Bitmap old = bitmap;
            bitmap = Bitmap.createBitmap(width, height, config);
            if (old != null) {
                old.recycle();
            }
        }
    }

    public long copyFrom(final ReaderBitmap src) {
        long start = TimeUtils.nanoTime();
        if (bitmap != null) {
            bitmap.recycle();
        }
        bitmap = src.getBitmap().copy(src.getBitmap().getConfig(), true);
        long end = TimeUtils.nanoTime();
        return (end - start);
    }

    public boolean replaceWithNewBitmap(final Bitmap src) {
        if (src == null || src == bitmap) {
            return false;
        }
        bitmap.recycle();
        bitmap = src;
        return true;
    }


}
