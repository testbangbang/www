package com.onyx.android.sdk.reader.cache;

import android.graphics.Bitmap;
import com.onyx.android.sdk.utils.BitmapUtils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by joy on 8/15/16.
 */
public class BitmapSoftLruCache {
    private static class BitmapSoftReference extends SoftReference<ReaderBitmapImpl> {

        public BitmapSoftReference(ReaderBitmapImpl bitmap, ReferenceQueue<ReaderBitmapImpl> queue) {
            super(bitmap, queue);
        }
    }

    ReferenceQueue<ReaderBitmapImpl> referenceQueue = new ReferenceQueue<>();
    private LinkedHashMap<String, BitmapSoftReference> map = new LinkedHashMap<>();
    private int maxSize;

    public BitmapSoftLruCache(int maxSize) {
        this.maxSize = maxSize;
    }

    public int maxSize() {
        synchronized (map) {
            return maxSize;
        }
    }

    public int size() {
        synchronized (map) {
            return map.size();
        }
    }

    public ReaderBitmapImpl get(String key) {
        synchronized (map) {
            clearStaleEntries();

            BitmapSoftReference result = map.get(key);
            if (result == null) {
                return null;
            }
            map.remove(key);
            map.put(key, result);
            return result.get();
        }
    }

    public ReaderBitmapImpl put(String key, ReaderBitmapImpl bitmap) {
        synchronized (map) {
            clearStaleEntries();

            BitmapSoftReference value = new BitmapSoftReference(bitmap, referenceQueue);
            BitmapSoftReference previous = map.put(key, value);
            if (previous != null) {
                return previous.get();
            }
            if (map.size() > maxSize) {
                String oldest = map.keySet().iterator().next();
                BitmapSoftReference remove = map.get(oldest);
                if (!isStaleReference(remove)) {
                    remove.get().recycleBitmap();
                }
                map.remove(oldest);
            }
            return null;
        }
    }

    public void remove(String key, boolean recycle) {
        synchronized (map) {
            clearStaleEntries();

            BitmapSoftReference reference = map.remove(key);
            if (recycle) {
                if (!isStaleReference(reference)) {
                    reference.get().recycleBitmap();
                }
            }
        }
    }

    public ReaderBitmapImpl getFreeBitmap(int width, int height, Bitmap.Config config) {
        synchronized (map) {
            clearStaleEntries();

            if (map.size() < maxSize) {
                return ReaderBitmapImpl.create(width, height, config);
            }

            Map.Entry<String, BitmapSoftReference> find = null;
            for (Map.Entry<String, BitmapSoftReference> entry : map.entrySet()) {
                ReaderBitmapImpl bitmap = entry.getValue().get();
                if (canReuse(bitmap, width, height, config)) {
                    find = entry;
                }
            }
            if (find == null) {
                return ReaderBitmapImpl.create(width, height, config);
            }

            map.remove(find.getKey());
            ReaderBitmapImpl bitmap = find.getValue().get();
            if (bitmap == null || !BitmapUtils.isValid(bitmap.getBitmap())) {
                return ReaderBitmapImpl.create(width, height, config);
            }

            return bitmap;
        }
    }

    private void clearStaleEntries() {
        ArrayList<Map.Entry<String, BitmapSoftReference>> list = new ArrayList<>();
        for (Map.Entry<String, BitmapSoftReference> entry : map.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            if (isStaleReference(entry.getValue())) {
                list.add(entry);
            }
        }
        for (Map.Entry<String, BitmapSoftReference> entry : list) {
            map.remove(entry.getKey());
        }
    }

    private boolean isStaleReference(BitmapSoftReference reference) {
        return reference.get() == null || !BitmapUtils.isValid(reference.get().getBitmap());
    }

    private boolean canReuse(ReaderBitmapImpl bitmap, int width, int height, Bitmap.Config config) {
        if (bitmap == null || bitmap.getBitmap() == null || bitmap.getBitmap().isRecycled()) {
            return false;
        }
        return bitmap.getBitmap().getWidth() == width && bitmap.getBitmap().getHeight() == height &&
                bitmap.getBitmap().getConfig() == config;
    }

    public void clear() {
        synchronized (map) {
            clearStaleEntries();
            for (BitmapSoftReference reference : map.values()) {
                if (reference != null && reference.get() != null &&
                         BitmapUtils.isValid(reference.get().getBitmap())) {
                    reference.get().getBitmap().recycle();
                }
            }
            map.clear();
        }
    }
}
