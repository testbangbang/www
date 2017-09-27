package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.v2.CloudMetadataCollection;
import com.onyx.android.sdk.data.model.v2.CloudMetadataCollection_Table;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

/**
 * Created by li on 2017/9/27.
 */

public class GetBookLibraryIdRequest extends BaseDataRequest {
    private String bookId;
    private CloudMetadataCollection metadataCollection;

    public GetBookLibraryIdRequest(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        metadataCollection = new Select().from(CloudMetadataCollection.class).where(CloudMetadataCollection_Table.documentUniqueId.eq(bookId)).querySingle();
    }

    public CloudMetadataCollection getMetadataCollection() {
        return metadataCollection;
    }
}
