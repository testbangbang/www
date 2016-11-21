package com.onyx.android.sdk.data;

import android.content.Context;

import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.utils.MetadataQueryArgsBuilder;
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
        final DataProviderBase provider = getDataProvider();
        for (Metadata metadata : addList) {
            MetadataCollection collection = new MetadataCollection(lid, metadata.getIdString());
            provider.addMetadataCollection(context, collection);
        }
    }

    public void removeCollections(Context context, final String lid, final List<Metadata> removeList) {
        for (Metadata metadata : removeList) {
            getDataProvider().deleteMetadataCollection(context, lid, metadata.getIdString());
        }
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

    public List<Metadata> buildLibrary(Context context, Library library, QueryArgs args) {
        List<Metadata> bookList = new ArrayList<>();
        boolean isCriteriaContentEmpty = true;
        DataProviderBase provider = getDataProvider();
        if (args != null && !args.isAllSetContentEmpty()) {
            library.setQueryString(QueryArgs.toQueryString(args));
            isCriteriaContentEmpty = false;
        }

        if (!isCriteriaContentEmpty) {
            bookList = getMetadataListOfLibrary(context, library.getParentUniqueId());
            addCollections(context, library.getIdString(), bookList);
        }
        provider.addLibrary(library);
        return bookList;
    }

    public void clearLibrary(Context context, Library library) {
        final List<Metadata> resultList = getMetadataListOfLibrary(context, library.getParentUniqueId());

        if (resultList.size() <= 0) {
            return;
        }
        getParent().getDataCacheManager().removeAll(library.getParentUniqueId(), resultList);
        removeCollections(context, library.getIdString(), resultList);
    }

    public List<Library> loadAllLibrary(final String parentId) {
        return getDataProvider().loadAllLibrary(parentId);
    }

    public void saveLibrary(Library library) {
        getDataProvider().addLibrary(library);
    }

    public void modifyLibrary(Context context, Library library, boolean modifyCriteria) {
        saveLibrary(library);
        if (modifyCriteria) {
            DataProviderBase providerBase = getDataProvider();
            List<Metadata> list = getParent().getDataCacheManager().getMetadataList(library.getIdString());
            if (CollectionUtils.isNullOrEmpty(list)) {
                list.addAll(getMetadataListOfLibrary(context, library.getIdString()));
            }
            if (!CollectionUtils.isNullOrEmpty(list)) {
                getParent().getDataCacheManager().removeAll(library.getIdString(), list);
                getParent().getDataCacheManager().addAllToLibrary(library.getParentUniqueId(), list);
                removeCollections(context, library.getIdString(), list);
            }
            QueryArgs criteria = QueryArgs.fromQueryString(library.getQueryString());
            criteria.libraryUniqueId = library.getParentUniqueId();
            MetadataQueryArgsBuilder.generateQueryArgs(criteria);
            MetadataQueryArgsBuilder.generateMetadataInQueryArgs(criteria);
            list = providerBase.findMetadata(context, criteria);
            if (!CollectionUtils.isNullOrEmpty(list)) {
                getParent().getDataCacheManager().addAllToLibrary(library.getIdString(), list);
                getParent().getDataCacheManager().removeAll(library.getParentUniqueId(), list);
                addCollections(context, library.getIdString(), list);
            }
        }
    }



}
