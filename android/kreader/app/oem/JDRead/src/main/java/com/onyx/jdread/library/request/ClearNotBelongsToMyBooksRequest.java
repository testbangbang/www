package com.onyx.jdread.library.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.List;

/**
 * Created by hehai on 18-3-16.
 */

public class ClearNotBelongsToMyBooksRequest extends RxBaseDBRequest {
    private String userPin;

    public ClearNotBelongsToMyBooksRequest(DataManager dm, String userPin) {
        super(dm);
        this.userPin = userPin;
    }

    @Override
    public ClearNotBelongsToMyBooksRequest call() throws Exception {
        QueryArgs queryArgs = new QueryArgs();
        queryArgs.conditionGroup.and(Metadata_Table.extension.isNotNull()).and(Metadata_Table.extension.notEq(userPin));
        DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        try {
            List<Metadata> metadataList = getDataProvider().findMetadataByQueryArgs(getAppContext(), queryArgs);
            for (Metadata metadata : metadataList) {
                getDataProvider().removeMetadata(getAppContext(), metadata);
                getDataProvider().deleteMetadataCollectionByDocId(getAppContext(), metadata.getIdString());
                FileUtils.deleteFile(metadata.getNativeAbsolutePath());
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        return this;
    }
}
