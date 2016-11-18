package com.onyx.android.sdk.data.cache;

import com.onyx.android.sdk.data.provider.DataProviderBase;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by suicheng on 2016/9/15.
 * All library cache is maintained
 */
public class LibraryCacheManager {

    private Map<String, LibraryCache> memoryCacheMap = new LinkedHashMap<>();
    private DataProviderBase dataProvider;
    public static final String ROOT_LIBRARY_TAG = "root";

    public LibraryCacheManager(DataProviderBase dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void clearLibrary(final String libraryId) {
        memoryCacheMap.get(libraryId).clear();
    }

    public void clear() {
        memoryCacheMap.clear();
    }

    public void add(final String id, final LibraryCache cache) {
        memoryCacheMap.put(id, cache);
    }

    public LibraryCache getLibraryCache(final String libraryId) {
        LibraryCache cache = memoryCacheMap.get(libraryId);
        if (cache == null) {
            cache = new LibraryCache(dataProvider);
            memoryCacheMap.put(libraryId, cache);
        }
        return cache;
    }

    public LibraryCache getRootLibrary() {
        return getLibraryCache(ROOT_LIBRARY_TAG);
    }
}
