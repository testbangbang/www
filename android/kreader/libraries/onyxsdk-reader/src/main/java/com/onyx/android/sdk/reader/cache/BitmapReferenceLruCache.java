package com.onyx.android.sdk.reader.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.util.Map;

/**
 * Created by Joy on 2016/5/5.
 */
public class BitmapReferenceLruCache extends LruCache<String, ReaderBitmapImpl> {

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public BitmapReferenceLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, ReaderBitmapImpl oldValue, ReaderBitmapImpl newValue) {
        oldValue.close();
    }

    public void clear() {
        evictAll();
    }

    public ReaderBitmapImpl getFreeBitmap(int width, int height, Bitmap.Config config) {
        if (size() >= maxSize()) {
            String freeKey = null;
            for (Map.Entry<String, ReaderBitmapImpl> entry : snapshot().entrySet()) {
                if (entry.getValue().isValid() &&
                        entry.getValue().getBitmap().getWidth() == width &&
                        entry.getValue().getBitmap().getHeight() == height &&
                        entry.getValue().getBitmap().getConfig() == config) {
                    freeKey = entry.getKey();
                    break;
                }
            }
            if (freeKey != null) {
                ReaderBitmapImpl bitmap = get(freeKey).clone();
                remove(freeKey);
                return bitmap;
            }
        }

        return ReaderBitmapImpl.create(width, height, config);
    }
}
