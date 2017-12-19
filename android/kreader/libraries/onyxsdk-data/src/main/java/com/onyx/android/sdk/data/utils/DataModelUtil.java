package com.onyx.android.sdk.data.utils;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.liulishuo.filedownloader.i.IFileDownloadIPCCallback;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.FileModel;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.ModelType;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.dataprovider.R;
import com.onyx.android.sdk.utils.CollectionUtils;

import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-11-21.
 */

public class DataModelUtil {
    public static void libraryToDataModel(DataProviderBase dataProvider, EventBus eventBus, List<DataModel> dataModels, List<Library> libraryList, boolean loadEmpty, int defaultCoverRes) {
        if (CollectionUtils.isNullOrEmpty(libraryList)) {
            return;
        }
        Iterator<Library> iterator = libraryList.iterator();
        while (iterator.hasNext()) {
            Library library = iterator.next();
            long metadataCount = dataProvider.libraryMetadataCount(library);
            if (metadataCount == 0 && !loadEmpty) {
                iterator.remove();
                continue;
            }
            DataModel model = new DataModel(eventBus);
            model.type.set(ModelType.TYPE_LIBRARY);
            model.parentId.set(library.getParentUniqueId());
            model.id.set(library.getId());
            model.idString.set(library.getIdString());
            model.title.set(library.getName());
            model.desc.set(library.getDescription());
            model.checked.set(false);
            model.coverDefault.set(defaultCoverRes);
            model.childCount.set(metadataCount);
            dataModels.add(model);
        }
    }

    public static void metadataToDataModel(EventBus eventBus, List<DataModel> dataModels, List<Metadata> metadataList, Map<String, CloseableReference<Bitmap>> thumbnailMap, Map<String, Integer> defaultCoverResMap) {
        if (CollectionUtils.isNullOrEmpty(metadataList)) {
            return;
        }
        for (Metadata metadata : metadataList) {
            DataModel model = new DataModel(eventBus);
            model.type.set(ModelType.TYPE_METADATA);
            model.idString.set(metadata.getIdString());
            model.title.set(metadata.getName());
            model.desc.set(metadata.getDescription());
            model.absolutePath.set(metadata.getNativeAbsolutePath());
            CloseableReference<Bitmap> bitmap = thumbnailMap.get(metadata.getAssociationId());
            if (bitmap != null) {
                model.coverBitmap.set(bitmap);
            } else {
                model.coverDefault.set(defaultCoverResMap.get(metadata.getType()));
            }

            dataModels.add(model);
        }
    }

    public static boolean isBitmapValid(CloseableReference<Bitmap> refBitmap) {
        return refBitmap != null && refBitmap.isValid();
    }

    public static void setDefaultThumbnail(DataModel itemModel) {
        FileModel fileModel = itemModel.getFileModel();
        if (fileModel == null) {
            return;
        }
        int res;
        switch (fileModel.getType()) {
            case TYPE_DIRECTORY:
                res = R.drawable.directory;
                break;
            case TYPE_GO_UP:
                res = R.drawable.directory_go_up;
                break;
            case TYPE_SHORT_CUT:
                res = R.drawable.directory_shortcut;
                break;
            case TYPE_FILE:
                res = getDrawable(fileModel.getFile());
                break;
            default:
                res = R.drawable.unknown_document;
                break;
        }
        itemModel.coverDefault.set(res);
    }

    public static int getDrawable(File file) {
        Integer res = ThumbnailUtils.defaultThumbnailMapping().get(FilenameUtils.getExtension(file.getName()));
        if (res == null) {
            return ThumbnailUtils.thumbnailUnknown();
        }
        return res;
    }

    public static List<Metadata> dataModelToMetadata(List<DataModel> dataModelList) {
        List<Metadata> list = new ArrayList<>();
        for (DataModel dataModel : dataModelList) {
            Metadata metadata = new Metadata();
            metadata.setIdString(dataModel.idString.get());
            metadata.setTitle(dataModel.title.get());
            metadata.setDescription(dataModel.desc.get());
            metadata.setNativeAbsolutePath(dataModel.absolutePath.get());
            list.add(metadata);
        }
        return list;
    }
}