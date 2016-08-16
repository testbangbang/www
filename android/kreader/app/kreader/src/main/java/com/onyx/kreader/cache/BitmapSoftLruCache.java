package com.onyx.kreader.cache;

import android.graphics.Bitmap;
import android.util.LruCache;
import com.onyx.android.sdk.utils.BitmapUtils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by joy on 8/15/16.
 */
public class BitmapSoftLruCache {
    private LinkedHashMap<String, SoftReference<Bitmap>> map = new LinkedHashMap<>();
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

    public SoftReference<Bitmap> get(String key) {
        synchronized (map) {
            SoftReference<Bitmap> result = map.get(key);
            if (result != null) {
                map.remove(key);
                map.put(key, result);
                return result;
            }
            return result;
        }
    }

    public SoftReference<Bitmap> put(String key, SoftReference<Bitmap> value) {
        synchronized (map) {
            SoftReference<Bitmap> previous = map.put(key, value);
            if (previous != null) {
                return previous;
            }
            if (map.size() > maxSize) {
                String oldest = map.keySet().iterator().next();
                SoftReference<Bitmap> bitmap = map.get(oldest);
                if (bitmap != null && BitmapUtils.isValid(bitmap.get())) {
                    bitmap.get().recycle();
                }
                map.remove(oldest);
            }
            return null;
        }
    }

    private void remove(String key) {
        synchronized (map) {
            SoftReference<Bitmap> bitmap = map.remove(key);
            if (bitmap != null && BitmapUtils.isValid(bitmap.get())) {
                bitmap.get().recycle();
            }
        }
    }

    public Bitmap getFreeBitmap(int width, int height, Bitmap.Config config) {
        synchronized (map) {
            clearObsoleteEntries();

            if (size() < maxSize()) {
                return Bitmap.createBitmap(width, height, config);
            }

            Map.Entry<String, SoftReference<Bitmap>> find = null;
            for (Map.Entry<String, SoftReference<Bitmap>> entry : map.entrySet()) {
                Bitmap bitmap = entry.getValue().get();
                if (canReuse(bitmap, width, height, config)) {
                    find = entry;
                }
            }
            if (find == null) {
                return Bitmap.createBitmap(width, height, config);
            }

            remove(find.getKey());
            Bitmap bitmap = find.getValue().get();
            if (!BitmapUtils.isValid(bitmap)) {
                return Bitmap.createBitmap(width, height, config);
            }

            return bitmap;
        }
    }

    private void clearObsoleteEntries() {
        ArrayList<Map.Entry<String, SoftReference<Bitmap>>> list = new ArrayList<>();
        for (Map.Entry<String, SoftReference<Bitmap>> entry : map.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            if (!BitmapUtils.isValid(entry.getValue().get())) {
                list.add(entry);
            }
        }
        for (Map.Entry<String, SoftReference<Bitmap>> entry : list) {
            remove(entry.getKey());
        }
    }

    private boolean canReuse(Bitmap bitmap, int width, int height, Bitmap.Config config) {
        if (bitmap == null || bitmap.isRecycled()) {
            return false;
        }
        return bitmap.getWidth() == width && bitmap.getHeight() == height &&
                bitmap.getConfig() == config;
    }

    public void clear() {
        synchronized (map) {
            for (SoftReference<Bitmap> bitmap : map.values()) {
                if (bitmap.get() != null && BitmapUtils.isValid(bitmap.get())) {
                    bitmap.get().recycle();
                }
            }
            map.clear();
        }
    }
}
