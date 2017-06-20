package com.onyx.android.sdk.reader.cache;

import android.graphics.Bitmap;

/**
 * Created by joy on 8/9/16.
 */
public class BitmapHolder {

    private int refCount = 0;
    private Bitmap bitmap;

    private Object lock = new Object();
    private boolean debug = false;

    private BitmapHolder(Bitmap bitmap) {
        this.bitmap = bitmap;
        refCount = 1;
    }

    public static BitmapHolder create(Bitmap bitmap) {
        return new BitmapHolder(bitmap);
    }

    public static BitmapHolder create(int width, int height, Bitmap.Config config) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        return create(bitmap);
    }

    public Bitmap getBitmap() {
        synchronized (lock) {
            return bitmap;
        }
    }

    public boolean isRecycled() {
        synchronized (lock) {
            return !isBitmapValid();
        }
    }

    public void detach() {
        synchronized (lock) {
            refCount--;
            if (refCount <= 0) {
                if (isBitmapValid()) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
        }
    }

    public BitmapHolder attach() {
        synchronized (lock) {
            refCount++;
            return this;
        }
    }

    public void eraseColor(int color) {
        synchronized (lock) {
            if (isBitmapValid()) {
                bitmap.eraseColor(color);
            }
        }
    }

    public int getWidth() {
        synchronized (lock) {
            if (!isBitmapValid()) {
                return 0;
            }
            return bitmap.getWidth();
        }
    }

    public int getHeight() {
        synchronized (lock) {
            if (!isBitmapValid()) {
                return 0;
            }
            return bitmap.getHeight();
        }
    }

    private boolean isBitmapValid() {
        return bitmap != null && !bitmap.isRecycled();
    }
}
