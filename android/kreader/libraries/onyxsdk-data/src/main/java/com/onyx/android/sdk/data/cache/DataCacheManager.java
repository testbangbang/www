package com.onyx.android.sdk.data.cache;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.MetaDataUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/5.
 */
public class DataCacheManager {

    private MetadataCache metadataCache;
    private LibraryCacheManager libraryCacheManager;
    private boolean isLibraryCacheReady = false;

    public DataCacheManager() {
        metadataCache = new MetadataCache();
        libraryCacheManager = new LibraryCacheManager();
    }

    public LibraryCache getLibraryCache(final String libraryUniqueId) {
        return libraryCacheManager.getLibraryCache(libraryUniqueId);
    }

    public void addMetadataToLibrary(final String libraryId, final Metadata metadata) {
        if (metadata == null) {
            return;
        }
        addToMetadataCache(metadata);
        addToLibrary(libraryId, metadata);
    }

    public void addToMetadataCache(final Metadata metadata) {
        metadataCache.add(metadata);
    }

    private void removeFromMetadataCache(Metadata metadata) {
        metadataCache.remove(metadata);
    }

    public void addToLibrary(final String libraryId, final Metadata metadata) {
        final LibraryCache libraryCache = getLibraryCache(libraryId);
        if (libraryCache != null) {
            libraryCache.add(metadata);
        }
    }

    public void addAllToLibrary(final String libraryId, final List<Metadata> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        for (Metadata metadata : list) {
            addMetadataToLibrary(libraryId, metadata);
        }
    }

    public void removeFromLibrary(final String libraryId, final String id) {
        final LibraryCache libraryCache = getLibraryCache(libraryId);
        if (libraryCache != null) {
            libraryCache.removeId(id);
        }
    }

    public List<Metadata> getMetadataList(final String libraryId) {
        LibraryCache cache = getLibraryCache(libraryId);
        return cache.getValueList();
    }

    public boolean remove(final String libraryId, final String metadataId) {
        Metadata metadata = metadataCache.getById(metadataId);
        if (metadata != null) {
            removeFromMetadataCache(metadata);
            removeFromLibrary(libraryId, metadataId);
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

    public Metadata getMetadataById(final String mid) {
        return metadataCache.getById(mid);
    }

    public boolean containsId(final String md5) {
        return metadataCache.containsId(md5);
    }

    public List<Metadata> getAllMetadataList() {
        return metadataCache.list();
    }

    public List<Metadata> getMetadataList(final List<Metadata> originList, final QueryArgs args) {
        List<Metadata> list;
        switch (args.filter) {
            case ALL:
                list = getAll(originList, args);
                break;
            case READ:
                list = getRead(originList, args);
                break;
            case READING:
                list = getReading(originList, args);
                break;
            case NEW_BOOKS:
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
            addMetadataToList(readList, metadata, metadata.isReaded());
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
