package com.onyx.android.sdk.data;

import android.content.Context;

import com.onyx.android.sdk.data.cache.DataCacheManager;
import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;

import java.util.List;

/**
 * Created by zhuzeng on 16/11/2016.
 */

public class DataManagerHelper {
    private DataProviderManager dataProviderManager = new DataProviderManager();
    private DataCacheManager dataCacheManager = new DataCacheManager();
    private MetadataHelper metadataHelper;
    private LibraryHelper libraryHelper;
    private ThumbnailHelper thumbnailHelper;

    public DataManagerHelper() {
        metadataHelper = new MetadataHelper(this);
        libraryHelper = new LibraryHelper(this);
        thumbnailHelper = new ThumbnailHelper(this);
    }

    public final DataProviderManager getDataProviderManager() {
        return dataProviderManager;
    }

    public DataProviderBase getDataProvider() {
        return getDataProviderManager().getDataProvider();
    }

    public final DataCacheManager getDataCacheManager() {
        return dataCacheManager;
    }

    public MetadataHelper getMetadataHelper() {
        return metadataHelper;
    }

    public LibraryHelper getLibraryHelper() {
        return libraryHelper;
    }

    public ThumbnailHelper getThumbnailHelper() {
        return thumbnailHelper;
    }

    public Metadata getMetadata(final Context context, final String mid) {
        return getMetadataHelper().getMetadata(context, mid);
    }

    public List<Metadata> getMetadataListByQueryArgs(final Context context, final QueryArgs queryArgs, boolean saveToCache) {
        final List<Metadata> list = getDataProvider().findMetadata(context, queryArgs);
        if (saveToCache) {
            getDataCacheManager().addToMetadataCache(list);
            getDataCacheManager().addToLibrary(queryArgs.libraryUniqueId, list);
        }
        return list;
    }

    public List<Metadata> getMetadataListByLibraryId(final Context context, final String libraryUniqueId, boolean saveToCache) {
        List<Metadata> list;
        final LibraryCache libraryCache = getDataCacheManager().getLibraryCache(libraryUniqueId);
        if (libraryCache != null) {
            list = libraryCache.getValueList();
        } else {
            list = getMetadataListByQueryArgs(context, QueryArgs.queryAll(), saveToCache);
        }
        return list;
    }

}
