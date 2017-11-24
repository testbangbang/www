package com.onyx.android.sdk.data.utils;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.ModelType;
import com.onyx.android.sdk.dataprovider.R;
import com.onyx.android.sdk.utils.CollectionUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-11-21.
 */

public class DataModelUtil {
    public static void libraryToDataModel(EventBus eventBus, List<DataModel> dataModels, List<Library> libraryList) {
        if (CollectionUtils.isNullOrEmpty(libraryList)) {
            return;
        }
        for (Library library : libraryList) {
            DataModel model = new DataModel(eventBus);
            model.type.set(ModelType.Library);
            model.parentId.set(library.getParentUniqueId());
            model.id.set(library.getId());
            model.idString.set(library.getIdString());
            model.title.set(library.getName());
            model.desc.set(library.getDescription());
            model.checked.set(false);
            model.coverDefault.set(R.drawable.library_default_cover);
            dataModels.add(model);
        }
    }

    public static void metadataToDataModel(EventBus eventBus, List<DataModel> dataModels, List<Metadata> metadataList, List<DataModel> selectedList, Map<String, CloseableReference<Bitmap>> thumbnailMap) {
        if (CollectionUtils.isNullOrEmpty(metadataList)) {
            return;
        }
        for (Metadata metadata : metadataList) {
            DataModel model = new DataModel(eventBus);
            model.type.set(ModelType.Metadata);
            model.idString.set(metadata.getIdString());
            model.title.set(metadata.getName());
            model.desc.set(metadata.getDescription());
            model.absolutePath.set(metadata.getNativeAbsolutePath());
            model.checked.set(isSelected(selectedList, metadata));
            CloseableReference<Bitmap> bitmap = thumbnailMap.get(metadata.getAssociationId());
            if (bitmap != null) {
                model.coverBitmap.set(bitmap);
            } else {
                model.coverDefault.set(R.drawable.book_default_cover);
            }

            dataModels.add(model);
        }
    }

    private static boolean isSelected(List<DataModel> selectedList, Metadata metadata) {
        if (CollectionUtils.isNullOrEmpty(selectedList)) {
            return false;
        }
        for (DataModel dataModel : selectedList) {
            if (dataModel.idString.get().equals(metadata.getIdString())) {
                return true;
            }
        }
        return false;
    }
}
