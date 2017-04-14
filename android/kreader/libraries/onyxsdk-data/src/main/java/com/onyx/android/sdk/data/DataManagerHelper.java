package com.onyx.android.sdk.data;

import android.content.Context;
import android.graphics.Bitmap;

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
import java.util.List;

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
        List<Library> tmpList = dataManager.getDataProviderBase().loadAllLibrary(parentId);
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

    public static Thumbnail loadThumbnail(Context context, String path, String md5, OnyxThumbnail.ThumbnailKind kind) {
        if (StringUtils.isNullOrEmpty(md5)) {
            try {
                md5 = FileUtils.computeMD5(new File(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getDataProviderBase().loadThumbnail(context, md5, kind);
    }

    public static Bitmap loadThumbnailBitmap(Context context, Thumbnail thumbnail) {
        return getDataProviderBase().loadThumbnailBitmap(context, thumbnail);
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

    public static Metadata getMetadataByMD5(Context context, String md5) {
        return new Select().from(Metadata.class).where(Metadata_Table.idString.eq(md5)).querySingle();
    }

    public static void deleteAllLibrary(Context context, DataManager dataManager, String parentUniqueId,
                                        List<Library> libraryList) {
        boolean isDeleteMetaCollection = StringUtils.isNullOrEmpty(parentUniqueId);
        for (Library tmp : libraryList) {
            if (isDeleteMetaCollection) {
                dataManager.getDataProviderBase().deleteMetadataCollection(context, tmp.getIdString());
            } else {
                List<MetadataCollection> list = dataManager.getDataProviderBase().loadMetadataCollection(context, tmp.getIdString());
                for (MetadataCollection metadataCollection : list) {
                    metadataCollection.setLibraryUniqueId(parentUniqueId);
                    metadataCollection.save();
                }
            }
            tmp.delete();
        }
    }

    public static void deleteMetadataCollection(Context context, DataManager dataManager, String libraryIdString){
        dataManager.getDataProviderBase().deleteMetadataCollection(context, libraryIdString);
    }

    public static MetadataCollection loadMetadataCollection(Context context, DataManager dataManager, String libraryIdString, String metaIdString) {
        return dataManager.getDataProviderBase().loadMetadataCollection(context, libraryIdString, metaIdString);
    }

    public static List<MetadataCollection> loadMetadataCollection(Context context, DataManager dataManager, String libraryIdString) {
        return dataManager.getDataProviderBase().loadMetadataCollection(context, libraryIdString);
    }
}
