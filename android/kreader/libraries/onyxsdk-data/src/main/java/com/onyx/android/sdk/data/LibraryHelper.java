package com.onyx.android.sdk.data;

import android.content.Context;

import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 20/11/2016.
 */

public class LibraryHelper {

    private DataManagerHelper parent;

    public LibraryHelper(final DataManagerHelper p) {
        parent = p;
    }

    public DataManagerHelper getParent() {
        return parent;
    }

    public DataProviderBase getDataProvider() {
        return getParent().getDataProvider();
    }

    public void addCollections(Context context, final String lid, final List<Metadata> addList) {
        if (StringUtils.isNullOrEmpty(lid) || CollectionUtils.isNullOrEmpty(addList)) {
            return;
        }

        final DataProviderBase provider = getDataProvider();
        for (Metadata metadata : addList) {
            MetadataCollection collection = new MetadataCollection(lid, metadata.getIdString());
            provider.addMetadataCollection(context, collection);
        }
    }

    public void removeCollections(Context context, final String lid, final List<Metadata> removeList) {
        if (StringUtils.isNullOrEmpty(lid) || CollectionUtils.isNullOrEmpty(removeList)) {
            return;
        }
        for (Metadata metadata : removeList) {
            getDataProvider().deleteMetadataCollection(context, lid, metadata.getIdString());
        }
    }

    public void clearCollections(Context context, final String lid) {
        if (StringUtils.isNullOrEmpty(lid)) {
            return;
        }
        getDataProvider().clearMetadataCollection(context, lid);
    }

    public List<Metadata> getMetadataListOfLibrary(Context context, final String libraryUniqueId) {
        if (StringUtils.isNullOrEmpty(libraryUniqueId)) {
            return null;
        }

        final LibraryCache cache = getParent().getDataCacheManager().getLibraryCache(libraryUniqueId);
        if (!cache.isEmpty()) {
            return cache.getValueList();
        }

        final List<Metadata> list = new ArrayList<>();
        List<MetadataCollection> collectionList = getDataProvider().loadMetadataCollection(context, libraryUniqueId);
        for(MetadataCollection entry : collectionList) {
            final Metadata metadata = getParent().getMetadata(context, entry.getDocumentUniqueId());
            list.add(metadata);
            cache.add(metadata);
        }
        return list;
    }

    public void moveToLibrary(final Context context,
                              final String prevLibraryUniqueId,
                              final String newLibraryUniqueId,
                              final List<Metadata> list) {
        final LibraryCache prevLibCache = getParent().getDataCacheManager().getLibraryCache(prevLibraryUniqueId);
        if (prevLibCache != null) {
            prevLibCache.removeAll(list);
        }
        removeCollections(context, prevLibraryUniqueId, list);

        final LibraryCache libCache = getParent().getDataCacheManager().getLibraryCache(newLibraryUniqueId);
        if (libCache != null) {
            libCache.addAll(list);
        }
        addCollections(context, newLibraryUniqueId, list);
    }

    public void moveToLibrary(final Context context,
                              final String prevLibraryUniqueId,
                              final String newLibraryUniqueId) {
        final LibraryCache prevLibCache = getParent().getDataCacheManager().getLibraryCache(prevLibraryUniqueId);
        final List<Metadata> list = prevLibCache.getValueList();
        moveToLibrary(context, prevLibraryUniqueId, newLibraryUniqueId, list);
    }

    public List<Metadata> rebuildLibrary(Context context, final Library library, final QueryArgs args) {
        clearCollections(context, library.getIdString());
        if (args != null && !args.isAllSetContentEmpty()) {
            library.setQueryString(QueryArgs.toQueryString(args));
        }
        final List<Metadata> list = getParent().getMetadataListByQueryArgs(context, args, true);
        addCollections(context, library.getIdString(), list);
        getDataProvider().updateLibrary(library);
        return list;
    }

    public List<Library> loadAllLibrary(final String parentId) {
        return getDataProvider().loadAllLibrary(parentId);
    }

    public void saveLibrary(Library library) {
        getDataProvider().addLibrary(library);
    }


}
