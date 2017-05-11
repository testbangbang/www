package com.onyx.android.sdk.data.cache;

import android.util.LruCache;

import com.onyx.android.sdk.data.model.Metadata;

import java.util.List;

/**
 * Created by suicheng on 2017/5/9.
 */

public class MetadataListLruCache extends LruCache<String, List<Metadata>> {
    private int itemSize = 1300;

    public MetadataListLruCache(int maxSize) {
        super(maxSize);
    }

    public MetadataListLruCache(int maxSize, int itemSize) {
        super(maxSize);
        this.itemSize = itemSize;
    }

    @Override
    protected int sizeOf(String key, List<Metadata> value) {
        return itemSize;
    }
}
