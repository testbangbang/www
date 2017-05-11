package com.onyx.android.sdk.data.cache;

import android.util.LruCache;

import com.onyx.android.sdk.data.model.Library;

import java.util.List;

/**
 * Created by suicheng on 2017/5/10.
 */

public class LibraryListLruCache extends LruCache<String, List<Library>> {
    private int itemSize = 450;

    public LibraryListLruCache(int maxSize) {
        super(maxSize);
    }

    public LibraryListLruCache(int maxSize, int itemSize) {
        super(maxSize);
        this.itemSize = itemSize;
    }

    @Override
    protected int sizeOf(String key, List<Library> value) {
        return itemSize;
    }
}
