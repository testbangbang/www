package com.onyx.android.sdk.data;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.cache.BitmapReferenceLruCache;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2016/11/16.
 */

public class DataManagerHelper {

    private static DataProviderBase getDataProviderBase() {
        return DataProviderManager.getLocalDataProvider();
    }

    public static void addCollections(Context context, Library library, List<Metadata> addList) {
        DataProviderBase providerBase = getDataProviderBase();
        for (Metadata metadata : addList) {
            providerBase.deleteMetadataCollection(context, library.getParentUniqueId(), metadata.getIdString());
            MetadataCollection collection = new MetadataCollection();
            collection.setLibraryUniqueId(library.getIdString());
            collection.setDocumentUniqueId(metadata.getIdString());
            providerBase.addMetadataCollection(context, collection);
        }
    }

    public static void updateCollections(Context context, String newLibraryUniqueId, String oldLibraryUniqueId) {
        if (newLibraryUniqueId == null) {
            getDataProviderBase().deleteMetadataCollection(context, oldLibraryUniqueId, null);
        } else {
            List<MetadataCollection> collections = getDataProviderBase().loadMetadataCollection(context, oldLibraryUniqueId);
            for (MetadataCollection collection : collections) {
                collection.setLibraryUniqueId(newLibraryUniqueId);
                getDataProviderBase().updateMetadataCollection(collection);
            }
        }
    }

    public static void removeCollections(Context context, Library library, List<Metadata> removeList) {
        for (Metadata metadata : removeList) {
            getDataProviderBase().deleteMetadataCollection(context, library.getIdString(), metadata.getIdString());
            if (library.getParentUniqueId() != null) {
                MetadataCollection collection = new MetadataCollection();
                collection.setLibraryUniqueId(library.getParentUniqueId());
                collection.setDocumentUniqueId(metadata.getIdString());
                getDataProviderBase().addMetadataCollection(context, collection);
            }
        }
    }

    public static List<Library> loadLibraryList(DataManager dataManager, List<Library> list, String parentId) {
        List<Library> tmpList = dataManager.getRemoteContentProvider().loadAllLibrary(parentId);
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

    public static List<Bitmap> loadThumbnailBitmapList(Context context, final List<File> fileList, int limit, OnyxThumbnail.ThumbnailKind kind) {
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
                                                           QueryArgs queryArgs, boolean reCache) {
        String queryKey = queryArgs.conditionGroup.getQuery() + queryArgs.libraryUniqueId +
                queryArgs.getOrderByQueryWithLimitOffset();
        List<Metadata> list = null;
        if (!reCache) {
            list = dataManager.getCacheManager().getMetadataLruCache(queryKey);
        }
        if (list == null) {
            list = dataManager.getRemoteContentProvider().findMetadataByQueryArgs(context, queryArgs);
            dataManager.getCacheManager().addToMetadataCache(queryKey, list);
        }
        return list;
    }

    public static List<Library> loadLibraryListWithCache(Context context, DataManager dataManager,
                                                         String libraryUniqueId, boolean reCache) {
        String queryKey = String.valueOf(libraryUniqueId);
        List<Library> list = null;
        if (!reCache) {
            list = dataManager.getCacheManager().getLibraryLruCache(queryKey);
        }
        if (list == null) {
            list = dataManager.getRemoteContentProvider().loadAllLibrary(libraryUniqueId);
            dataManager.getCacheManager().addToLibraryCache(queryKey, list);
        }
        return list;
    }

    public static Thumbnail getThumbnailEntry(Context context, DataManager dataManager, String associationId) {
        return dataManager.getRemoteContentProvider().getThumbnailEntry(context, associationId,
                OnyxThumbnail.ThumbnailKind.Large);
    }

    public static String getThumbnailPath(Context context, DataManager dataManager, String associationId) {
        Thumbnail thumbnail = getThumbnailEntry(context, dataManager, associationId);
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
        String path = getThumbnailPath(context, dataManager, associationId);
        if (StringUtils.isNullOrEmpty(path)) {
            return null;
        }
        try {
            refBitmap = ThumbnailUtils.decodeFile(new File(path));
            if (refBitmap != null && refBitmap.isValid()) {
                bitmapLruCache.put(associationId, refBitmap);
                return refBitmap.clone();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
}
