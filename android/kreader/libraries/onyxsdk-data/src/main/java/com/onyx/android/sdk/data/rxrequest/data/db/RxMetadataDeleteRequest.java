package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hehai on 17-12-13.
 */

public class RxMetadataDeleteRequest extends RxBaseDBRequest {
    private List<Metadata> list = new ArrayList<>();
    private String libraryId;

    public RxMetadataDeleteRequest(DataManager dm, List<Metadata> list, String libraryId) {
        super(dm);
        this.list = list;
        this.libraryId = libraryId;
    }

    @Override
    public RxMetadataDeleteRequest call() throws Exception {
        list.clear();
        deleteBookList();
        return this;
    }

    private void deleteBookList() {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for (Metadata metadata : list) {
            getDataProvider().removeMetadata(getAppContext(), metadata);
            if (StringUtils.isNotBlank(libraryId)) {
                getDataProvider().deleteMetadataCollection(getAppContext(), libraryId, metadata.getIdString());
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();

        for (Metadata metadata : list) {
            FileUtils.deleteFile(metadata.getNativeAbsolutePath());
        }
    }
}
