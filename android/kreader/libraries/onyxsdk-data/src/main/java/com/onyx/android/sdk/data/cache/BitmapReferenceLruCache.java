package com.onyx.android.sdk.data.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.facebook.common.references.CloseableReference;

/**
 * Created by suicheng on 2017/4/27.
 */
public class BitmapReferenceLruCache extends LruCache<String, CloseableReference<Bitmap>> {

    public BitmapReferenceLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, CloseableReference<Bitmap> oldValue, CloseableReference<Bitmap> newValue) {
        oldValue.close();
    }

    public void clear() {
        evictAll();
    }
}
