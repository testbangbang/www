package com.onyx.android.sdk.data.manager;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.cache.BitmapReferenceLruCache;
import com.onyx.android.sdk.data.cache.LibraryListLruCache;
import com.onyx.android.sdk.data.cache.MetadataListLruCache;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

/**
 * Created by suicheng on 2017/4/27.
 */

public class CacheManager {
    private static final int DEFAULT_METADATA_CACHE_SIZE = 512 * 1024;
    private static final int DEFAULT_LIBRARY_CACHE_SIZE = 256 * 1024;
    private static final int DEFAULT_BITMAP_CACHE_SIZE = 27 * 1024 * 1024;

    private LruCache<String, List<Metadata>> metadataLruCache;
    private LruCache<String, List<Library>> libraryLruCache;
    private BitmapReferenceLruCache bitmapLruCache;

    public CacheManager() {
    }

    public LruCache<String, List<Metadata>> getMetadataLruCache() {
        if (metadataLruCache == null) {
            metadataLruCache = new MetadataListLruCache(DEFAULT_METADATA_CACHE_SIZE);
        }
        return metadataLruCache;
    }

    public LruCache<String, List<Library>> getLibraryLruCache() {
        if (libraryLruCache == null) {
            libraryLruCache = new LibraryListLruCache(DEFAULT_LIBRARY_CACHE_SIZE);
        }
        return libraryLruCache;
    }

    public BitmapReferenceLruCache getBitmapLruCache() {
        if (bitmapLruCache == null) {
            bitmapLruCache = new BitmapReferenceLruCache(DEFAULT_BITMAP_CACHE_SIZE);
        }
        return bitmapLruCache;
    }

    public void setMetadataLruCache(LruCache<String, List<Metadata>> lruCache) {
        this.metadataLruCache = lruCache;
    }

    public void setLibraryLruCache(LruCache<String, List<Library>> lruCache) {
        this.libraryLruCache = lruCache;
    }

    public void setBitmapLruCache(BitmapReferenceLruCache bitmapLruCache) {
        this.bitmapLruCache = bitmapLruCache;
    }

    public void addToMetadataCache(String key, List<Metadata> metadataList) {
        getMetadataLruCache().put(key, metadataList);
    }

    public void clearMetadataCache(String key) {
        getMetadataLruCache().remove(key);
    }

    public List<Metadata> getMetadataLruCache(String key) {
        return getMetadataLruCache().get(key);
    }

    public void addToLibraryCache(String key, List<Library> libraryList) {
        getLibraryLruCache().put(key, libraryList);
    }

    public List<Library> getLibraryLruCache(String key){
        return getLibraryLruCache().get(key);
    }

    public void addToBitmapRefCache(String key, CloseableReference<Bitmap> bitmap) {
        getBitmapLruCache().put(key, bitmap);
    }

    public CloseableReference<Bitmap> getBitmapRefCache(String key) {
        return getBitmapLruCache().get(key);
    }

    public void clearMetadataCache() {
        getMetadataLruCache().evictAll();
    }

    public void clearLibraryCache() {
        getLibraryLruCache().evictAll();
    }

    public void clearAll() {
        clearMetadataCache();
        clearLibraryCache();
    }

    public boolean hasMetadataCache(String key) {
        return getMetadataLruCache(key) != null;
    }

    public static String generateCloudKey(QueryArgs args) {
        String queryKey = args.libraryUniqueId;
        queryKey += args.getOrderByQuery();
        return queryKey;
    }
}
