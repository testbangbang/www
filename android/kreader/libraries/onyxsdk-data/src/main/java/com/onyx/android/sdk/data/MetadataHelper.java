package com.onyx.android.sdk.data;

import android.content.Context;

import com.onyx.android.sdk.data.model.Metadata;

import java.util.List;

/**
 * Created by zhuzeng on 20/11/2016.
 */

public class MetadataHelper {

    private DataManagerHelper parent;

    public MetadataHelper(final DataManagerHelper p) {
        parent = p;
    }

    public DataManagerHelper getParent() {
        return parent;
    }

    public Metadata getMetadata(final String mid) {
        Metadata metadata = getParent().getDataCacheManager().getMetadataById(mid);
        if (metadata != null) {
            return metadata;
        }
        metadata = getParent().getDataProvider().findMetadataByMD5(null, mid);
        getParent().getDataCacheManager().addToMetadataCache(metadata);
        return metadata;
    }

    public List<Metadata> getAllMetadataListByQueryArgs(final Context context, final QueryArgs queryArgs, boolean saveToCache) {
        final List<Metadata> list = getParent().getDataProvider().findMetadata(context, queryArgs);
        if (saveToCache) {
            getParent().getDataCacheManager().addAllToMetadataCache(list);
            getParent().getDataCacheManager().addAllToLibrary(queryArgs.libraryUniqueId, list);
        }
        return list;
    }

    public List<Metadata> collectMetadataListByQueryArgs(final Context context, final QueryArgs queryArgs, boolean saveToCache) {
        final List<Metadata> list = getParent().getDataProvider().findMetadata(context, queryArgs);
        if (saveToCache) {
            getParent().getDataCacheManager().addAllToMetadataCache(list);
            getParent().getDataCacheManager().addAllToLibrary(queryArgs.libraryUniqueId, list);
        }
        return list;
    }


    public List<Metadata> filter(final List<Metadata> list, final QueryArgs queryArgs) {
        return null;
    }

    public List<Metadata> sortInPlace(final List<Metadata> list, final QueryArgs queryArgs) {
        return list;
    }


}
