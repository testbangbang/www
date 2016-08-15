package com.onyx.kreader.cache;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.utils.BitmapUtils;

/**
 * Created by joy on 8/9/16.
 */
public class ReaderBitmapImpl implements ReaderBitmap {

    private BitmapHolder bitmap;

    public static ReaderBitmapImpl create(int width, int height, Bitmap.Config config) {
        ReaderBitmapImpl readerBitmap = new ReaderBitmapImpl(width, height, config);
        return readerBitmap;
    }

    public ReaderBitmapImpl() {
        super();
    }

    public ReaderBitmapImpl(int width, int height, Bitmap.Config config) {
        super();
        bitmap = BitmapHolder.create(width, height, config);
    }

    public void clear() {
        if (bitmap != null) {
            bitmap.eraseColor(Color.WHITE);
        }
    }

    public void recycleBitmap() {
        if (bitmap != null) {
            bitmap.detach();
        }
        bitmap = null;
    }

    public Bitmap getBitmap() {
        return bitmap.getBitmap();
    }

    public BitmapHolder getBitmapReference()  {
        return bitmap;
    }

    public void update(int width, int height, Bitmap.Config config) {
        if (bitmap == null || bitmap.getWidth() != width || bitmap.getHeight() != height) {
            recycleBitmap();
            bitmap = BitmapHolder.create(width, height, config);
        }
    }

    public boolean attachWith(final BitmapHolder src) {
        recycleBitmap();
        bitmap = src.attach();
        return BitmapUtils.isValid(bitmap.getBitmap());
    }
}
