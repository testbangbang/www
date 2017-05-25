package com.onyx.android.sdk.data;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.cache.BitmapReferenceLruCache;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.manager.CacheManager;
import com.onyx.android.sdk.data.model.CloudMetadataCollection;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.data.v1.OnyxFileDownloadService;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/11/16.
 */

public class DataManagerHelper {

    private static DataProviderBase getDataProviderBase() {
        return DataProviderManager.getLocalDataProvider();
    }

    public static List<Library> loadLibraryList(DataManager dataManager, List<Library> list, String parentId) {
        List<Library> tmpList = dataManager.getRemoteContentProvider().loadAllLibrary(parentId, null);
        if (tmpList.size() > 0) {
            list.addAll(tmpList);
        }
        return tmpList;
    }

    public static void loadLibraryRecursive(DataManager dataManager, List<Library> list, String targetId) {
        List<Library> tmpList = loadLibraryList(dataManager, list, targetId);
        for (Library library : tmpList) {
            loadLibraryRecursive(dataManager, list, library.getIdString());
        }
    }

    public static Thumbnail loadThumbnail(Context context, String path, String associationId, OnyxThumbnail.ThumbnailKind kind) {
        if (StringUtils.isNullOrEmpty(associationId)) {
            try {
                associationId = FileUtils.computeMD5(new File(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getDataProviderBase().getThumbnailEntry(context, associationId, kind);
    }

    public static Bitmap loadThumbnailBitmap(Context context, Thumbnail thumbnail) {
        return getDataProviderBase().getThumbnailBitmap(context, thumbnail.getIdString(), OnyxThumbnail.ThumbnailKind.Original);
    }

    public static Metadata getMetadataByCloudReference(Context context, final String cloudReference) {
        return new Select().from(Metadata.class).where(Metadata_Table.cloudId.eq(cloudReference)).querySingle();
    }

    public static Metadata getMetadataByHashTag(Context context, String hashTag) {
        return new Select().from(Metadata.class).where(Metadata_Table.hashTag.eq(hashTag)).querySingle();
    }

    public static void deleteAllLibrary(Context context, DataManager dataManager, String parentUniqueId,
                                        List<Library> libraryList) {
        DataProviderBase providerBase = dataManager.getRemoteContentProvider();
        boolean isDeleteMetaCollection = StringUtils.isNullOrEmpty(parentUniqueId);
        for (Library tmp : libraryList) {
            if (isDeleteMetaCollection) {
                providerBase.deleteMetadataCollection(context, tmp.getIdString());
            } else {
                List<MetadataCollection> list = providerBase.loadMetadataCollection(context, tmp.getIdString());
                for (MetadataCollection metadataCollection : list) {
                    metadataCollection.setLibraryUniqueId(parentUniqueId);
                    providerBase.updateMetadataCollection(metadataCollection);
                }
            }
            providerBase.deleteLibrary(tmp);
        }
    }

    public static void deleteMetadataCollection(Context context, DataManager dataManager, String libraryIdString){
        dataManager.getRemoteContentProvider().deleteMetadataCollection(context, libraryIdString);
    }

    public static MetadataCollection loadMetadataCollection(Context context, DataManager dataManager, String libraryIdString, String metaIdString) {
        return dataManager.getRemoteContentProvider().loadMetadataCollection(context, libraryIdString, metaIdString);
    }

    public static List<MetadataCollection> loadMetadataCollection(Context context, DataManager dataManager, String libraryIdString) {
        return dataManager.getRemoteContentProvider().loadMetadataCollection(context, libraryIdString);
    }

    public static List<Metadata> loadMetadataListWithCache(Context context, DataManager dataManager,
                                                           QueryArgs queryArgs, boolean loadFromCache) {
        String queryKey = queryArgs.conditionGroup.getQuery() + queryArgs.libraryUniqueId +
                queryArgs.getOrderByQueryWithLimitOffset();
        List<Metadata> list = null;
        if (loadFromCache) {
            list = dataManager.getCacheManager().getMetadataLruCache(queryKey);
        }
        if (list == null) {
            list = dataManager.getRemoteContentProvider().findMetadataByQueryArgs(context, queryArgs);
            if (!CollectionUtils.isNullOrEmpty(list)) {
                dataManager.getCacheManager().addToMetadataCache(queryKey, list);
            }
        }
        return list;
    }

    public static List<Library> loadLibraryListWithCache(Context context, DataManager dataManager,
                                                         String libraryUniqueId, boolean loadFromCache) {
        String queryKey = String.valueOf(libraryUniqueId);
        List<Library> list = null;
        if (loadFromCache) {
            list = dataManager.getCacheManager().getLibraryLruCache(queryKey);
        }
        if (list == null) {
            list = dataManager.getRemoteContentProvider().loadAllLibrary(libraryUniqueId, null);
            if (!CollectionUtils.isNullOrEmpty(list)) {
                dataManager.getCacheManager().addToLibraryCache(queryKey, list);
            }
        }
        return list;
    }

    public static OnyxThumbnail.ThumbnailKind getDefaultThumbnailKind() {
        return OnyxThumbnail.ThumbnailKind.Large;
    }

    public static Thumbnail getThumbnailEntry(Context context, DataProviderBase dataProvider, String associationId) {
        return dataProvider.getThumbnailEntry(context, associationId, getDefaultThumbnailKind());
    }

    public static String getThumbnailPath(Context context, DataProviderBase dataProvider, String associationId) {
        Thumbnail thumbnail = getThumbnailEntry(context, dataProvider, associationId);
        if (thumbnail == null) {
            return null;
        }
        if (!FileUtils.fileExist(thumbnail.getImageDataPath())) {
            return null;
        }
        return thumbnail.getImageDataPath();
    }

    public static Map<String, CloseableReference<Bitmap>> loadThumbnailBitmapsWithCache(Context context, DataManager dataManager,
                                                                                        List<Metadata> metadataList) {
        Map<String, CloseableReference<Bitmap>> map = new HashMap<>();
        for (Metadata metadata : metadataList) {
            CloseableReference<Bitmap> refBitmap = loadThumbnailBitmapWithCache(context, dataManager, metadata);
            if (refBitmap == null) {
                continue;
            }
            map.put(metadata.getAssociationId(), refBitmap);
        }
        return map;
    }

    public static CloseableReference<Bitmap> loadThumbnailBitmapWithCache(Context context, DataManager dataManager,
                                                                          Metadata metadata) {
        BitmapReferenceLruCache bitmapLruCache = dataManager.getCacheManager().getBitmapLruCache();
        String associationId = metadata.getAssociationId();
        if (StringUtils.isNullOrEmpty(associationId)) {
            return null;
        }
        CloseableReference<Bitmap> refBitmap = bitmapLruCache.get(associationId);
        if (refBitmap != null) {
            return refBitmap.clone();
        }
        return decodeFileAndCache(context, dataManager.getRemoteContentProvider(), bitmapLruCache, associationId);
    }

    private static CloseableReference<Bitmap> decodeFileAndCache(File file, BitmapReferenceLruCache bitmapLruCache, String associationId) {
        CloseableReference<Bitmap> refBitmap = null;
        try {
            refBitmap = ThumbnailUtils.decodeFile(file);
            if (refBitmap != null && refBitmap.isValid()) {
                bitmapLruCache.put(associationId, refBitmap);
                return refBitmap.clone();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return refBitmap;
    }

    private static CloseableReference<Bitmap> decodeFileAndCache(Context context, DataProviderBase dataProvider,
                                                                BitmapReferenceLruCache bitmapLruCache,
                                                                String associationId) {
        CloseableReference<Bitmap> refBitmap = null;
        String path = getThumbnailPath(context, dataProvider, associationId);
        if (StringUtils.isNotBlank(path)) {
            refBitmap = decodeFileAndCache(new File(path), bitmapLruCache, associationId);
        }
        return refBitmap;
    }

    public static List<Library> loadParentLibraryList(Context context, DataManager dataManager, Library library) {
        List<Library> parentLibraryList = new ArrayList<>();
        if (library == null || StringUtils.isNullOrEmpty(library.getParentUniqueId())) {
            return parentLibraryList;
        }
        String parentId = library.getParentUniqueId();
        while (StringUtils.isNotBlank(parentId)) {
            Library parentLibrary = dataManager.getRemoteContentProvider().loadLibrary(parentId);
            if (parentLibrary == null) {
                break;
            }
            parentLibraryList.add(parentLibrary);
            parentId = parentLibrary.getParentUniqueId();
        }
        Collections.reverse(parentLibraryList);
        return parentLibraryList;
    }

    public static void updateCloudCacheList(List<Metadata> cacheList, QueryResult<Metadata> result, QueryArgs queryArgs) {
        if (result == null || CollectionUtils.isNullOrEmpty(result.list)) {
            return;
        }
        int contentSize = queryArgs.offset + CollectionUtils.getSize(result.list);
        for (int i = queryArgs.offset; i < contentSize && i < CollectionUtils.getSize(cacheList); i++) {
            cacheList.set(i, result.list.get(i - queryArgs.offset));
        }
    }

    public static QueryResult<Metadata> cloudMetadataFromDataProvider(Context context, DataProviderBase dataProvider,
                                                                      QueryArgs queryArgs) {
        int limit = queryArgs.limit;
        queryArgs.limit = queryArgs.getCloudFetchLimit();
        QueryResult<Metadata> result = dataProvider.findMetadataResultByQueryArgs(context, queryArgs);
        queryArgs.limit = limit;
        return result;
    }

    public static boolean cloudMetadataFromCache(QueryResult<Metadata> result, QueryArgs queryArgs, List<Metadata> cacheList) {
        result.count = CollectionUtils.getSize(cacheList);
        result.list = new ArrayList<>();
        boolean success = true;
        for (int i = queryArgs.offset; i < (queryArgs.limit + queryArgs.offset)
                && i < (CollectionUtils.getSize(cacheList)); i++) {
            Metadata metadata = cacheList.get(i);
            if (metadata == null) {
                success = false;
                break;
            }
            result.list.add(metadata);
        }
        return success;
    }

    public static Map<String, CloseableReference<Bitmap>> loadCloudThumbnailBitmapsWithCache(Context context, CloudManager cloudManager,
                                                                                             List<Metadata> metadataList) {
        Map<String, CloseableReference<Bitmap>> map = new HashMap<>();
        for (Metadata metadata : metadataList) {
            CloseableReference<Bitmap> refBitmap = loadCloudThumbnailBitmapWithCache(context, cloudManager, metadata);
            if (refBitmap == null) {
                continue;
            }
            map.put(metadata.getAssociationId(), refBitmap);
        }
        return map;
    }

    public static CloseableReference<Bitmap> loadCloudThumbnailBitmapWithCache(Context context, CloudManager cloudManager,
                                                                               Metadata metadata) {
        BitmapReferenceLruCache bitmapLruCache = cloudManager.getCacheManager().getBitmapLruCache();
        String associationId = metadata.getAssociationId();
        if (StringUtils.isNullOrEmpty(associationId)) {
            return null;
        }
        CloseableReference<Bitmap> refBitmap = bitmapLruCache.get(associationId);
        if (refBitmap != null) {
            return refBitmap.clone();
        }
        return null;
    }

    public static void saveCloudCollection(Context context, DataProviderBase dataProvider, String libraryId, String associationId) {
        CloudMetadataCollection collection = new CloudMetadataCollection();
        collection.setDocumentUniqueId(associationId);
        collection.setLibraryUniqueId(libraryId);
        saveCollection(context, dataProvider, collection);
    }

    public static void saveCollection(Context context, DataProviderBase dataProvider, MetadataCollection collection) {
        MetadataCollection findCollection = dataProvider.findMetadataCollection(context, collection.getDocumentUniqueId());
        if (findCollection == null) {
            if (StringUtils.isNullOrEmpty(collection.getLibraryUniqueId())) {
                return;
            }
            dataProvider.addMetadataCollection(context, collection);
        } else {
            if (StringUtils.isNullOrEmpty(collection.getLibraryUniqueId())) {
                dataProvider.deleteMetadataCollection(context, collection.getLibraryUniqueId(), collection.getDocumentUniqueId());
            } else {
                collection.setLibraryUniqueId(collection.getLibraryUniqueId());
                dataProvider.updateMetadataCollection(collection);
            }
        }
    }

    public static void saveCloudMetadataAndCollection(Context context, DataProviderBase dataProvider,
                                                      QueryArgs queryArgs, QueryResult<Metadata> queryResult) {
        final DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for (Metadata metadata : queryResult.list) {
            dataProvider.saveMetadata(context, metadata);
            saveCloudCollection(context, dataProvider, queryArgs.libraryUniqueId, metadata.getAssociationId());
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }
}
