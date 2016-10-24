package com.onyx.kreader.reflow;

import android.graphics.Bitmap;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.kreader.cache.BitmapDiskLruCache;
import com.onyx.kreader.cache.BitmapSoftLruCache;
import com.onyx.kreader.cache.ReaderBitmapImpl;

import java.io.File;

/**
 * Created by joy on 10/10/16.
 */
public class ReflowedSubPageCache {
    /**
     * we must have space big enough to hold at least one page's reflowed bitmaps
     */
    private static final int MAX_DISK_CACHE_SIZE = 20 * 1024 * 1024;

    private BitmapDiskLruCache diskCache;;

    private ReflowedSubPageCache(final File cacheRoot) {
        diskCache = BitmapDiskLruCache.create(cacheRoot, MAX_DISK_CACHE_SIZE);
    }

    public static ReflowedSubPageCache create(final File cacheRoot) {
        return new ReflowedSubPageCache(cacheRoot);
    }

    public boolean contains(final String key) {
        return diskCache.contains(key);
    }

    public void putCache(final String key, final Bitmap bitmap) {
        diskCache.put(key, bitmap);
    }

    public Bitmap get(final String key) {
        return diskCache.get(key);
    }
}
