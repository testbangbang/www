package com.onyx.android.sdk.data.manager;

import android.util.LruCache;

import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;

import java.util.List;

/**
 * Created by suicheng on 2017/4/27.
 */

public class CacheManager {
    private static final int DEFAULT_METADATA_CACHE_SIZE = 2 * 1024 * 1024;
    private static final int DEFAULT_LIBRARY_CACHE_SIZE = 256 * 1024;
    private LruCache<String, List<Metadata>> metadataLruCache;
    private LruCache<String, List<Library>> libraryLruCache;

    public CacheManager() {
    }

    public LruCache<String, List<Metadata>> getMetadataCache() {
        if (metadataLruCache == null) {
            metadataLruCache = new LruCache<>(DEFAULT_METADATA_CACHE_SIZE);
        }
        return metadataLruCache;
    }

    public LruCache<String, List<Library>> getLibraryCache() {
        if (libraryLruCache == null) {
            libraryLruCache = new LruCache<>(DEFAULT_LIBRARY_CACHE_SIZE);
        }
        return libraryLruCache;
    }

    public void setMetadataLruCache(LruCache<String, List<Metadata>> lruCache) {
        this.metadataLruCache = lruCache;
    }

    public void setLibraryLruCache(LruCache<String, List<Library>> lruCache) {
        this.libraryLruCache = lruCache;
    }

    public void addToMetadataCache(String key, List<Metadata> metadataList) {
        getMetadataCache().put(key, metadataList);
    }

    public List<Metadata> getMetadataCache(String key) {
        return getMetadataCache().get(key);
    }

    public void addToLibraryCache(String key, List<Library> libraryList) {
        getLibraryCache().put(key, libraryList);
    }

    public List<Library> getLibraryCache(String key){
        return getLibraryCache().get(key);
    }

    public void clearMetadataCache() {
        getMetadataCache().evictAll();
    }

    public void clearLibraryCache() {
        getLibraryCache().evictAll();
    }

    public void clearAll() {
        clearMetadataCache();
        clearLibraryCache();
    }
}
