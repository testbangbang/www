package com.onyx.android.sdk.data;

import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.cache.LibraryCacheManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.utils.MetaDataUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/5.
 */
public class DataCacheManager {

    private MetadataCache metadataCache = new MetadataCache();
    private LibraryCacheManager libraryCacheManager;
    private boolean isLibraryCacheReady = false;

    public DataCacheManager(DataProviderBase dataProvider) {
        libraryCacheManager = new LibraryCacheManager(dataProvider);
    }

    public LibraryCache getLibraryCache(final String libraryUniqueId) {
        return libraryCacheManager.getLibraryCache(libraryUniqueId);
    }

    public void add(final String parentLibraryId, final Metadata metadata) {
        if (metadata == null) {
            return;
        }
        metadataCache.add(metadata.getIdString(), metadata);
        addToLibrary(parentLibraryId, metadata);
    }

    public void addToLibrary(final String parentLibraryId, final Metadata metadata) {
        final LibraryCache libraryCache = getLibraryCache(parentLibraryId);
        if (libraryCache != null) {
            libraryCache.addId(metadata.getIdString());
        }
    }

    public void addAll(final String parentId, List<Metadata> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        for (Metadata metadata : list) {
            add(parentId, metadata);
        }
    }

    private void removeFromCache(Metadata metadata) {
        metadataCache.remove(metadata);
    }

    public boolean remove(final String parentId, final String md5) {
        Metadata metadata = metadataCache.getById(md5);
        if (metadata != null) {
            removeFromCache(metadata);
            getLibraryCache(parentId).removeId(md5);
            return true;
        }
        return false;
    }

    public boolean removeAll(final String parentId, final List<Metadata> list) {
        for (Metadata metadata : list) {
            remove(parentId, metadata.getIdString());
        }
        return true;
    }

    public void clear() {
        metadataCache.clear();
        libraryCacheManager.clear();
        setLibraryCacheReady(false);
    }

    public void setLibraryCacheReady(boolean ready) {
        isLibraryCacheReady = ready;
    }

    public boolean isLibraryCacheReady() {
        return (isLibraryCacheReady && !isEmpty());
    }

    public boolean isEmpty() {
        return metadataCache.isEmpty();
    }

    public Metadata getById(final String md5) {
        return metadataCache.getById(md5);
    }

    public boolean containsId(final String md5) {
        return metadataCache.containsId(md5);
    }

    public List<Metadata> getAllMetadataList() {
        return metadataCache.list();
    }

    public List<Metadata> getMetadataList(final String parentId) {
        LibraryCache cache = getLibraryCache(parentId);
        List<Metadata> list = new ArrayList<>();
        for (String id : cache.getIdList()) {
            list.add(metadataCache.getById(id));
        }
        return list;
    }

    public List<Metadata> getMetadataList(final List<Metadata> originList, final QueryArgs args) {
        List<Metadata> list;
        switch (args.filter) {
            case ALL:
                list = getAll(originList, args);
                break;
            case FINISHED:
                list = getRead(originList, args);
                break;
            case READING:
                list = getReading(originList, args);
                break;
            case NEW:
                list = getNewBookList(originList, args);
                break;
            case TAG:
                list = getByTag(originList, args);
                break;
            case SEARCH:
                list = getBySearch(originList, args);
                break;
            default:
                list = getAll(originList, args);
                break;
        }
        if (args.isAllSetContentEmpty()) {
            return sortMetadataList(list, args);
        }

        List<Metadata> resultList = new ArrayList<>();
        for (Metadata metadata : list) {
            if (MetaDataUtils.criteriaContains(metadata, args)) {
                resultList.add(metadata);
            }
        }
        return sortMetadataList(resultList, args);
    }

    public boolean containType(Metadata metadata, QueryArgs args) {
        if (args.fileType != null && args.fileType.size() > 0 && !args.fileType.contains(metadata.getType())) {
            return false;
        }
        return true;
    }

    private void addMetadataToList(List<Metadata> list, Metadata metadata, boolean isConfirm) {
        if (!isConfirm) {
            return;
        }
        list.add(metadata);
    }

    private List<Metadata> sortMetadataList(List<Metadata> list, QueryArgs args) {
        if (list.size() <= 0) {
            return list;
        }
        if (args.offset >= list.size() || args.limit == 0) {
            return new ArrayList<>();
        }
        MetaDataUtils.sort(list, args.sortBy, args.order);
        if (args.offset == 0 && args.limit >= list.size()) {
            return list;
        }
        List<Metadata> sortList = new ArrayList<>();
        for (int i = 0; i < Math.min(args.limit, list.size() - args.offset); i++) {
            sortList.add(list.get(i + args.offset));
        }
        return sortList;
    }

    public List<Metadata> getAll(final List<Metadata> originList, final QueryArgs args) {
        List<Metadata> typeList = new ArrayList<>();
        for (Metadata metadata : originList) {
            addMetadataToList(typeList, metadata, containType(metadata, args));
        }
        return typeList;
    }

    public List<Metadata> getRead(final List<Metadata> originList, final QueryArgs args) {
        List<Metadata> readList = new ArrayList<>();
        for (Metadata metadata : originList) {
            if (!containType(metadata, args)) {
                continue;
            }
            addMetadataToList(readList, metadata, metadata.isFinished());
        }
        return readList;
    }

    public List<Metadata> getReading(final List<Metadata> originList, final QueryArgs args) {
        List<Metadata> readingList = new ArrayList<>();
        for (Metadata metadata : originList) {
            if (!containType(metadata, args)) {
                continue;
            }
            addMetadataToList(readingList, metadata, metadata.isReading());
        }
        return readingList;
    }

    public List<Metadata> getNewBookList(final List<Metadata> originList, final QueryArgs args) {
        List<Metadata> newBookList = new ArrayList<>();
        for (Metadata metadata : originList) {
            if (!containType(metadata, args)) {
                continue;
            }
            addMetadataToList(newBookList, metadata, metadata.isNew());
        }
        return newBookList;
    }

    public List<Metadata> getByTag(final List<Metadata> originList, final QueryArgs args) {
        List<Metadata> tagList = new ArrayList<>();
        for (Metadata metadata : originList) {
            if (!containType(metadata, args)) {
                continue;
            }
            addMetadataToList(tagList, metadata, MetaDataUtils.safelyContains(args.tags, metadata.getTags()));
        }
        return tagList;
    }

    public List<Metadata> getBySearch(final List<Metadata> originList, final QueryArgs args) {
        List<Metadata> searchList = new ArrayList<>();
        for (Metadata metadata : originList) {
            if (!containType(metadata, args)) {
                continue;
            }
            addMetadataToList(searchList, metadata, MetaDataUtils.safelyContains(metadata, args.query));
        }
        return searchList;
    }
}
