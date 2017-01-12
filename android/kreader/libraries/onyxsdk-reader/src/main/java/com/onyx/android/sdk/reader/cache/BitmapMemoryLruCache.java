package com.onyx.android.sdk.reader.cache;

import android.util.LruCache;
import com.onyx.android.sdk.utils.BitmapUtils;

/**
 * Created by Joy on 2016/5/5.
 */
public class BitmapMemoryLruCache extends LruCache<String, BitmapHolder> {

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public BitmapMemoryLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, BitmapHolder oldValue, BitmapHolder newValue) {
        if (oldValue != null ) {
            oldValue.detach();
        }
    }

    @Override
    protected int sizeOf(String key, BitmapHolder value) {
        if (value == null || !BitmapUtils.isValid(value.getBitmap())) {
            return 0;
        }
        return BitmapUtils.getSizeInBytes(value.getBitmap());
    }
}
