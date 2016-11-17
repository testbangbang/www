package com.onyx.android.sdk.data;

import android.content.Context;
import android.graphics.Bitmap;

import com.onyx.android.sdk.data.cache.DataCacheManager;
import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.utils.MetadataQueryArgsBuilder;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 16/11/2016.
 */

public class DataManagerHelper {
    private DataProviderManager dataProviderManager = new DataProviderManager();
    private DataCacheManager dataCacheManager;

    public DataManagerHelper() {
        dataCacheManager = new DataCacheManager(getDataProvider());
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

    public void cacheUpdateMetaList(String oldUniqueId, String newUniqueId, List<Metadata> metadataList) {
        dataCacheManager.removeAll(oldUniqueId, metadataList);
        dataCacheManager.addAllToLibrary(newUniqueId, metadataList);
    }

    public void addMetadataCollections(Context context, Library library, List<Metadata> addList) {
        DataProviderBase provider = getDataProvider();
        for (Metadata metadata : addList) {
            provider.deleteMetadataCollection(context, library.getParentUniqueId(), metadata.getIdString());
            MetadataCollection collection = new MetadataCollection(library.getIdString(), metadata.getIdString());
            provider.addMetadataCollection(context, collection);
        }
    }

    public void updateCollections(Context context, String newLibraryUniqueId, String oldLibraryUniqueId) {
        if (newLibraryUniqueId == null) {
            getDataProvider().deleteMetadataCollection(context, oldLibraryUniqueId, null);
        } else {
            List<MetadataCollection> collections = getDataProvider().loadMetadataCollection(context, oldLibraryUniqueId);
            for (MetadataCollection collection : collections) {
                collection.setLibraryUniqueId(newLibraryUniqueId);
                getDataProvider().updateMetadataCollection(collection);
            }
        }
    }

    public void removeCollections(Context context, Library library, List<Metadata> removeList) {
        for (Metadata metadata : removeList) {
            getDataProvider().deleteMetadataCollection(context, library.getIdString(), metadata.getIdString());
            if (library.getParentUniqueId() != null) {
                MetadataCollection collection = new MetadataCollection(library.getParentUniqueId(), metadata.getIdString());
                getDataProvider().addMetadataCollection(context, collection);
            }
        }
    }

    public List<Metadata> getLibraryMetadataListOfAll(Context context, final QueryArgs args) {
        List<Metadata> list = new ArrayList<>();
        LibraryCache cache = dataCacheManager.getLibraryCache(args.libraryUniqueId);
        if (CollectionUtils.isNullOrEmpty(cache.getIdList())) {
            QueryArgs queryArgs = MetadataQueryArgsBuilder.libraryAllBookQuery(args.libraryUniqueId, args.sortBy, args.order);
            list = cache.getList(context, queryArgs);
            dataCacheManager.addAllToLibrary(args.libraryUniqueId, list);
            return list;
        }

        for (String id : cache.getIdList()) {
            Metadata metadata = dataCacheManager.getMetadataById(id);
            if (metadata == null) {
                continue;
            }
            list.add(metadata);
        }
        return list;
    }

    public List<Metadata> getLibraryMetadataListWithParentId(Context context, String parentId) {
        List<Metadata> list = new ArrayList<>();
        QueryArgs args = MetadataQueryArgsBuilder.libraryAllBookQuery(parentId, SortBy.Name, SortOrder.Asc);
        list.addAll(getLibraryMetadataList(context, args));
        return list;
    }

    public List<Metadata> getLibraryMetadataList(Context context, QueryArgs queryArgs) {
        List<Metadata> list = getLibraryMetadataListOfAll(context, queryArgs);
        return dataCacheManager.getMetadataList(list, queryArgs);
    }

    public List<Metadata> getMetadataList(Context context, QueryArgs queryArgs) {
        if (dataCacheManager.isLibraryCacheReady()) {
            List<Metadata> allList = dataCacheManager.getAllMetadataList();
            return dataCacheManager.getMetadataList(allList, queryArgs);
        }

        List<Metadata> list = new ArrayList<>();
        LibraryCache cache = dataCacheManager.getLibraryCache(queryArgs.libraryUniqueId);
        if (CollectionUtils.isNullOrEmpty(cache.getIdList())) {
            list = cache.getList(context, queryArgs);
            dataCacheManager.addAllToLibrary(queryArgs.libraryUniqueId, list);
            return list;
        }

        for (String id : cache.getIdList()) {
            Metadata metadata = dataCacheManager.getMetadataById(id);
            if (metadata == null) {
                continue;
            }
            list.add(metadata);
        }

        if (!CollectionUtils.isNullOrEmpty(list)) {
            if (queryArgs.filter == BookFilter.ALL && queryArgs.limit == Integer.MAX_VALUE &&
                    queryArgs.offset == 0) {
                dataCacheManager.setLibraryCacheReady(true);
            }
        }
        return list;
    }

    public void addToLibrary(final Context context, final Library library, final List<Metadata> addList) {
        addMetadataCollections(context, library, addList);
        LibraryCache cache = dataCacheManager.getLibraryCache(library.getIdString());
        if (CollectionUtils.isNullOrEmpty(cache.getIdList())) {
            dataCacheManager.removeAll(library.getParentUniqueId(), addList);
            return;
        }
        cacheUpdateMetaList(library.getParentUniqueId(), library.getIdString(), addList);
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
            args.libraryUniqueId = library.getParentUniqueId();
            bookList = getLibraryMetadataList(context, args);
            addMetadataCollections(context, library, bookList);
        }
        provider.addLibrary(library);
        return bookList;
    }

    public void clearLibrary(Context context, Library library) {
        List<Metadata> resultList = getLibraryMetadataListOfAll(context, MetadataQueryArgsBuilder.libraryAllBookQuery(library.getIdString(), SortBy.Name, SortOrder.Desc));

        if (resultList.size() <= 0) {
            return;
        }
        dataCacheManager.addAllToLibrary(library.getParentUniqueId(), resultList);
        updateCollections(context, library.getParentUniqueId(), library.getIdString());
    }

    public List<Library> loadAllLibrary(List<Library> list, String parentId) {
        List<Library> tmpList = getDataProvider().loadAllLibrary(parentId);
        if (tmpList.size() > 0) {
            list.addAll(tmpList);
        }
        return tmpList;
    }

    public void deepLoadAllLibrary(List<Library> list, String targetId) {
        List<Library> tmpList = loadAllLibrary(list, targetId);
        for (Library library : tmpList) {
            deepLoadAllLibrary(list, library.getIdString());
        }
    }

    public void deleteLibrary(Context context, Library library) {
        DataProviderBase providerBase = getDataProvider();
        List<Library> libraryList = new ArrayList<>();
        deepLoadAllLibrary(libraryList, library.getIdString());
        libraryList.add(0, library);

        for (Library tmp : libraryList) {
            providerBase.deleteLibrary(tmp);
            List<Metadata> list = dataCacheManager.getMetadataList(tmp.getIdString());
            if (CollectionUtils.isNullOrEmpty(list)) {
                list = getLibraryMetadataListWithParentId(context, library.getIdString());
            } else {
                dataCacheManager.removeAll(tmp.getIdString(), list);
            }

            if (list.size() <= 0) {
                continue;
            }
            dataCacheManager.addAllToLibrary(library.getParentUniqueId(), list);
            updateCollections(context, library.getParentUniqueId(), tmp.getIdString());
        }
    }

    public void saveLibrary(Library library) {
        getDataProvider().addLibrary(library);
    }

    public void modifyLibrary(Context context, Library library, boolean modifyCriteria) {
        saveLibrary(library);
        if (modifyCriteria) {
            DataProviderBase providerBase = getDataProvider();
            List<Metadata> list = dataCacheManager.getMetadataList(library.getIdString());
            if (CollectionUtils.isNullOrEmpty(list)) {
                list.addAll(getLibraryMetadataListWithParentId(context, library.getIdString()));
            }
            if (!CollectionUtils.isNullOrEmpty(list)) {
                dataCacheManager.removeAll(library.getIdString(), list);
                dataCacheManager.addAllToLibrary(library.getParentUniqueId(), list);
                removeCollections(context, library, list);
            }
            QueryArgs criteria = QueryArgs.fromQueryString(library.getQueryString());
            criteria.libraryUniqueId = library.getParentUniqueId();
            MetadataQueryArgsBuilder.generateQueryArgs(criteria);
            MetadataQueryArgsBuilder.generateMetadataInQueryArgs(criteria);
            list = providerBase.findMetadata(context, criteria);
            if (!CollectionUtils.isNullOrEmpty(list)) {
                dataCacheManager.addAllToLibrary(library.getIdString(), list);
                dataCacheManager.removeAll(library.getParentUniqueId(), list);
                addMetadataCollections(context, library, list);
            }
        }
    }

    public void removeFromLibrary(Context context, Library library, List<Metadata> removeList) {
        removeCollections(context, library, removeList);
        cacheUpdateMetaList(library.getIdString(), library.getParentUniqueId(), removeList);
    }

    public Thumbnail loadThumbnail(Context context, String path, String md5, OnyxThumbnail.ThumbnailKind kind) {
        if (StringUtils.isNullOrEmpty(md5)) {
            try {
                md5 = FileUtils.computeMD5(new File(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getDataProvider().loadThumbnail(context, md5, kind);
    }

    public Bitmap loadThumbnailBitmap(Context context, Thumbnail thumbnail) {
        return getDataProvider().loadThumbnailBitmap(context, thumbnail);
    }

    public List<Bitmap> loadThumbnailBitmapList(Context context, final List<File> fileList, int limit, OnyxThumbnail.ThumbnailKind kind) {
        List<Bitmap> thumbnailList = new ArrayList<>();
        Bitmap bitmap = null;
        int thumbCount = 0;
        for (File file : fileList) {
            if (file.isDirectory()) {
                continue;
            }
            Thumbnail thumbnail = loadThumbnail(context, file.getAbsolutePath(), null, kind);
            if (thumbCount++ < Math.min(limit, fileList.size())) {
                bitmap = loadThumbnailBitmap(context, thumbnail);
            }
            thumbnailList.add(bitmap == null ?
                    ThumbnailUtils.loadDefaultThumbnailFromExtension(context, FileUtils.getFileExtension(file)) :
                    bitmap);
        }
        return thumbnailList;
    }

}
