package com.onyx.android.sdk.data;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.MetaDataUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by suicheng on 2016/9/5.
 */
public class DataCacheManager {

    // global library cache.
    private HashMap<String, Metadata> pathHashMap = new HashMap<>();
    private HashMap<String, Metadata> md5HashMap = new HashMap<>();
    private HashMap<String, List<Metadata>> libraryMapHashMap = new HashMap<>();
    private boolean isLibraryCacheReady = false;

    public final HashMap<String, Metadata> getPathHashMap() {
        return pathHashMap;
    }

    public final HashMap<String, Metadata> getMd5HashMap() {
        return md5HashMap;
    }

    public final HashMap<String, List<Metadata>> getLibraryMapHashMap() {
        return libraryMapHashMap;
    }

    private List<Metadata> getLibraryMapList(String parentId) {
        List<Metadata> list = libraryMapHashMap.get(parentId);
        if (CollectionUtils.isNullOrEmpty(list)) {
            list = new ArrayList<>();
            libraryMapHashMap.put(parentId, list);
        }
        return list;
    }

    private void addToLibraryList(String parentId, Metadata metadata) {
        List<Metadata> list = getLibraryMapList(parentId);
        if (list.contains(metadata)) {
            list.remove(metadata);
        }
        list.add(metadata);
    }

    public void add(final String parentId, final Metadata metadata) {
        if (metadata == null) {
            return;
        }
        pathHashMap.put(metadata.getNativeAbsolutePath(), metadata);
        if (StringUtils.isNotBlank(metadata.getIdString())) {
            md5HashMap.put(metadata.getIdString(), metadata);
        }
        addToLibraryList(parentId, metadata);
    }

    public void addAll(final String parentId, List<Metadata> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        for (Metadata metadata : list) {
            add(parentId, metadata);
        }
    }

    private void removeFromHashMap(Metadata metadata) {
        pathHashMap.remove(metadata.getNativeAbsolutePath());
        md5HashMap.remove(metadata.getIdString());
    }

    //only remove metadata belonged to libraryListMap
    private void removeFromLibraryList(String parentId, Metadata metadata) {
        List<Metadata> list = getLibraryMapList(parentId);
        list.remove(metadata);
    }

    public boolean remove(final String parentId, final String path) {
        Metadata metadata = pathHashMap.get(path);
        if (metadata != null) {
            removeFromHashMap(metadata);
            removeFromLibraryList(parentId, metadata);
            return true;
        }
        return false;
    }

    public boolean removeAll(final String parentId, final List<Metadata> list) {
        for (Metadata metadata : list) {
            remove(parentId, metadata.getNativeAbsolutePath());
        }
        return true;
    }

    public void clear() {
        pathHashMap.clear();
        md5HashMap.clear();
        libraryMapHashMap.clear();
        setLibraryCacheReady(false);
    }

    public void setLibraryCacheReady(boolean ready) {
        isLibraryCacheReady = ready;
    }

    public boolean isLibraryCacheReady() {
        return (isLibraryCacheReady && !isEmpty());
    }

    public boolean isEmpty() {
        return pathHashMap.isEmpty();
    }

    public Metadata get(final String path) {
        return pathHashMap.get(path);
    }

    public Metadata getByMd5(final String md5) {
        return md5HashMap.get(md5);
    }

    public boolean contains(final String path) {
        return pathHashMap.containsKey(path);
    }

    public boolean containMd5(final String md5) {
        return md5HashMap.containsKey(md5);
    }

    public List<Metadata> getMetadataList(final String parentId) {
        return getLibraryMapList(parentId);
    }

    public List<Metadata> getMetadataList(final String parentId, QueryCriteria criteria) {
        List<Metadata> list = getMetadataList(parentId);
        List<Metadata> containList = new ArrayList<>();
        for (Metadata metadata : list) {
            if (MetaDataUtils.criteriaContains(metadata, criteria)) {
                containList.add(metadata);
            }
        }
        return containList;
    }

    public List<Metadata> getMetadataList(final QueryArgs args) {
        if (args == null || args.filter == null) {
            return getAll(args);
        }
        switch (args.filter) {
            case ALL:
                return getAll(args);
            case READED:
                return getReaded(args);
            case READING:
                return getReading(args);
            case NEW_BOOKS:
                return getNewBookList(args);
            case TAG:
                return getByTag(args);
            case SEARCH:
                return getBySearch(args);
            default:
                return getAll(args);
        }
    }

    public boolean containType(Metadata metadata, QueryArgs args) {
        if (args.contentType != null && !args.contentType.contains(metadata.getType())) {
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

    public List<Metadata> getListByContentTypes(final QueryArgs args) {
        List<Metadata> list = getLibraryMapList(args.parentId);
        List<Metadata> typeList = new ArrayList<>();
        for (Metadata metadata : list) {
            addMetadataToList(typeList, metadata, containType(metadata, args));
        }
        return typeList;
    }

    public List<Metadata> getAll(final QueryArgs args) {
        List<Metadata> list = getListByContentTypes(args);
        return sortMetadataList(list, args);
    }

    public List<Metadata> getReaded(final QueryArgs args) {
        List<Metadata> list = getLibraryMapList(args.parentId);
        List<Metadata> readList = new ArrayList<>();
        for (Metadata metadata : list) {
            if (!containType(metadata, args)) {
                continue;
            }
            addMetadataToList(readList, metadata, metadata.isReaded());
        }
        return sortMetadataList(readList, args);
    }

    public List<Metadata> getReading(final QueryArgs args) {
        List<Metadata> list = getLibraryMapList(args.parentId);
        List<Metadata> readingList = new ArrayList<>();

        for (Metadata metadata : list) {
            if (!containType(metadata, args)) {
                continue;
            }
            addMetadataToList(readingList, metadata, metadata.isReading());
        }
        return sortMetadataList(readingList, args);
    }

    public List<Metadata> getNewBookList(final QueryArgs args) {
        List<Metadata> list = getLibraryMapList(args.parentId);
        List<Metadata> newBookList = new ArrayList<>();

        for (Metadata metadata : list) {
            if (!containType(metadata, args)) {
                continue;
            }
            addMetadataToList(newBookList, metadata, metadata.isNew());
        }
        return sortMetadataList(newBookList, args);
    }

    public List<Metadata> getByTag(final QueryArgs args) {
        List<Metadata> list = getLibraryMapList(args.parentId);
        List<Metadata> tagList = new ArrayList<>();

        for (Metadata metadata : list) {
            if (!containType(metadata, args)) {
                continue;
            }
            addMetadataToList(tagList, metadata, MetaDataUtils.safelyContains(args.tags, metadata.getTags()));
        }
        return sortMetadataList(tagList, args);
    }

    public List<Metadata> getBySearch(final QueryArgs args) {
        List<Metadata> list = getLibraryMapList(args.parentId);
        List<Metadata> searchList = new ArrayList<>();

        for (Metadata metadata : list) {
            if (!containType(metadata, args)) {
                continue;
            }
            addMetadataToList(searchList, metadata, MetaDataUtils.safelyContains(metadata, args.query));
        }
        return sortMetadataList(searchList, args);
    }
}
