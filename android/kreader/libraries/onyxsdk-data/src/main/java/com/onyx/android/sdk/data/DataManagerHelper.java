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
    private DataCacheManager dataCacheManager = new DataCacheManager();
    private MetadataHelper metadataHelper;
    private LibraryHelper libraryHelper;

    public DataManagerHelper() {
        metadataHelper = new MetadataHelper(this);
        libraryHelper = new LibraryHelper(this);
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

    public boolean isMetadataCacheReady() {
        return getDataCacheManager().isMetadataCacheReady();
    }

    public Metadata getMetadata(final Context context, final String mid) {
        return getMetadataHelper().getMetadata(context, mid);
    }

    public List<Metadata> getCompleteMetadataListByQueryArgs(final Context context, final QueryArgs queryArgs, boolean saveToCache) {
        final List<Metadata> list = getDataProvider().findMetadata(context, queryArgs);
        if (saveToCache) {
            getDataCacheManager().addAllToMetadataCache(list);
            getDataCacheManager().addAllToLibrary(queryArgs.libraryUniqueId, list);
        }
        return list;
    }

    public void cacheUpdateMetaList(String oldUniqueId, String newUniqueId, List<Metadata> metadataList) {
        dataCacheManager.removeAll(oldUniqueId, metadataList);
        dataCacheManager.addAllToLibrary(newUniqueId, metadataList);
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

    public List<Metadata> getLibraryMetadataListOfAll(Context context, final QueryArgs queryArgs) {
        List<Metadata> list = new ArrayList<>();
        final LibraryCache cache = dataCacheManager.getLibraryCache(queryArgs.libraryUniqueId);
        if (CollectionUtils.isNullOrEmpty(cache.getValueList())) {
            QueryArgs args = MetadataQueryArgsBuilder.libraryAllBookQuery(queryArgs.libraryUniqueId, queryArgs.sortBy, queryArgs.order);
            list = cache.getValueList();
            dataCacheManager.addAllToLibrary(queryArgs.libraryUniqueId, list);
            return list;
        }

        for (Metadata metadata : cache.getValueList()) {
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

    public List<Metadata> getAllMetadataOfLibrary(final Context context, final String libraryUniqueId, boolean saveToCache) {
        List<Metadata> list;
        final LibraryCache libraryCache = getDataCacheManager().getLibraryCache(libraryUniqueId);
        if (libraryCache != null) {
            list = libraryCache.getValueList();
        } else {
            list = getCompleteMetadataListByQueryArgs(context, QueryArgs.queryAll(), saveToCache);
        }
        return list;
    }

    public List<Metadata> getMetadataList(Context context, final QueryArgs queryArgs, boolean saveToCache) {
        List<Metadata> list = getAllMetadataOfLibrary(context, queryArgs.libraryUniqueId, saveToCache);
        return list;
    }


    public void deepLoadAllLibrary(List<Library> list, String targetId) {
        List<Library> tmpList = getLibraryHelper().loadAllLibrary(targetId);
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
