package com.onyx.android.sdk.reader.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import com.jakewharton.disklrucache.DiskLruCache;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by joy on 8/16/16.
 */
public class BitmapDiskLruCache {
    private static final String TAG = BitmapDiskLruCache.class.getSimpleName();

    DiskLruCache diskCache;

    private BitmapDiskLruCache(DiskLruCache cache) {
        diskCache = cache;
    }

    @NonNull
    public static BitmapDiskLruCache create(final File location, final int maxSize) {
        try {
            return new BitmapDiskLruCache(DiskLruCache.open(location, 0, 1, maxSize));
        } catch (IOException e) {
            Log.w(TAG, e);
            return new BitmapDiskLruCache(null);
        }
    }

    public boolean contains(String key) {
        try {
            if (isClosed()) {
                return false;
            }
            return diskCache.get(key) != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Bitmap get(String key) {
        return get(key, null);
    }

    public Bitmap get(String key, BitmapFactory.Options options) {
        return getFromDiskCache(key, options);
    }

    public Bitmap put(String key, Bitmap bitmap) {
        return put(key, bitmap, Bitmap.CompressFormat.PNG, 100);
    }

    public Bitmap put(final String key, final Bitmap bitmap,
                            Bitmap.CompressFormat compressFormat,
                            int compressQuality) {
        putDiskCache(key, bitmap, compressFormat, compressQuality);
        return bitmap;
    }

    public void clear() {
        if (!isClosed()) {
            try {
                diskCache.delete();
                diskCache = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getFromDiskCache(final String key, final BitmapFactory.Options options) {
        if (isClosed()) {
            return null;
        }

        try {
            DiskLruCache.Snapshot snapshot = diskCache.get(key);
            if (snapshot == null) {
                return null;
            }
            try {
                Benchmark benchmark = new Benchmark();
                InputStream is = snapshot.getInputStream(0);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
                    if (bitmap == null) {
                        diskCache.remove(key);
                        diskCache.flush();
                        return null;
                    }
                    return bitmap;
                } finally {
                    FileUtils.closeQuietly(is);
                    benchmark.report("loadDocument disk cache");
                }
            } finally {
                snapshot.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void putDiskCache(final String key, final Bitmap bitmap,
                              final Bitmap.CompressFormat compressFormat,
                              final int compressQuality) {
        if (isClosed()) {
            return;
        }

        try {
            Benchmark benchmark = new Benchmark();
            DiskLruCache.Editor editor = diskCache.edit(key);
            OutputStream os = editor.newOutputStream(0);
            try {
                bitmap.compress(compressFormat, compressQuality, os);
                os.flush();
                editor.commit();
                diskCache.flush();
                benchmark.report("write disk cache");
            } finally {
                FileUtils.closeQuietly(os);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isClosed() {
        return diskCache == null || diskCache.isClosed();
    }
}
