package com.onyx.jdread.library.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-12-21.
 */

public class RxDeleteMetadataFromMultipleLibraryRequest extends RxBaseDBRequest {
    private Map<String, List<Metadata>> chosenItemsMap;

    public RxDeleteMetadataFromMultipleLibraryRequest(DataManager dm, Map<String, List<Metadata>> chosenItemsMap) {
        super(dm);
        this.chosenItemsMap = chosenItemsMap;
    }

    @Override
    public RxDeleteMetadataFromMultipleLibraryRequest call() throws Exception {
        for (Map.Entry<String, List<Metadata>> entry : chosenItemsMap.entrySet()) {
            deleteBookList(entry.getValue(), entry.getKey());
        }
        return this;
    }

    private void deleteBookList(List<Metadata> list, String libraryId) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for (Metadata metadata : list) {
            getDataProvider().removeMetadata(getAppContext(), metadata);
            if (StringUtils.isNotBlank(libraryId)) {
                getDataProvider().deleteMetadataCollection(getAppContext(), libraryId, metadata.getAssociationId());
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();

        for (Metadata metadata : list) {
            FileUtils.deleteFile(metadata.getNativeAbsolutePath());
        }
    }
}
