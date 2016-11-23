package com.onyx.android.sdk.data;

import android.content.Context;

import com.onyx.android.sdk.data.model.Metadata;

import java.io.File;
import java.util.ArrayList;
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

    public Metadata getMetadata(final Context context, final String mid) {
        Metadata metadata = getParent().getDataCacheManager().getMetadataById(mid);
        if (metadata != null) {
            return metadata;
        }
        metadata = getParent().getDataProvider().findMetadataByMD5(context, mid);
        getParent().getDataCacheManager().addToMetadataCache(metadata);
        return metadata;
    }

    public List<Metadata> getAllMetadataListByQueryArgs(final Context context, final QueryArgs queryArgs, boolean saveToCache) {
        final List<Metadata> list = getParent().getDataProvider().findMetadata(context, queryArgs);
        if (saveToCache) {
            getParent().getDataCacheManager().addToMetadataCache(list);
            getParent().getDataCacheManager().addToLibrary(queryArgs.libraryUniqueId, list);
        }
        return list;
    }

    public List<Metadata> collectMetadataListByQueryArgs(final Context context, final QueryArgs queryArgs, boolean saveToCache) {
        final List<Metadata> list = getParent().getDataProvider().findMetadata(context, queryArgs);
        if (saveToCache) {
            getParent().getDataCacheManager().addToMetadataCache(list);
            getParent().getDataCacheManager().addToLibrary(queryArgs.libraryUniqueId, list);
        }
        return list;
    }

    public List<Metadata> saveList(final DataManager dataManager, final Context context, final List<File> list) {
        final List<Metadata> metadataList = new ArrayList<>();
        for(File file : list) {
            final Metadata metadata = Metadata.createFromFile(file);
            dataManager.getDataManagerHelper().getDataProvider().saveMetadata(context, metadata);
            metadataList.add(metadata);
        }
        return metadataList;
    }

    public List<Metadata> filter(final List<Metadata> list, final QueryArgs queryArgs) {
        return null;
    }

    public List<Metadata> sortInPlace(final List<Metadata> list, final QueryArgs queryArgs) {
        return list;
    }


}
