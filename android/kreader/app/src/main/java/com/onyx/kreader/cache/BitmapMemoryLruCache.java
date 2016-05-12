package com.onyx.kreader.cache;

import android.graphics.Bitmap;
import android.util.LruCache;
import com.onyx.kreader.common.Benchmark;
import com.onyx.kreader.utils.BitmapUtils;

/**
 * Created by Joy on 2016/5/5.
 */
public class BitmapMemoryLruCache extends LruCache<String, Bitmap> {

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public BitmapMemoryLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        if (oldValue != null && !oldValue.isRecycled()) {
            oldValue.recycle();
        }
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return BitmapUtils.getSizeInBytes(value);
    }
}
