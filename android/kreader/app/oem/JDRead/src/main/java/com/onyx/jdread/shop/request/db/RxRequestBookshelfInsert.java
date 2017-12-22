package com.onyx.jdread.shop.request.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;

/**
 * Created by jackdeng on 2017/12/21.
 */

public class RxRequestBookshelfInsert extends RxBaseDBRequest {

    private Metadata metadata;

    public RxRequestBookshelfInsert(DataManager dm, Metadata metadata) {
        super(dm);
        this.metadata = metadata;
    }

    @Override
    public Object call() throws Exception {
        insert();
        return this;
    }

    public void insert() {
        if (metadata != null) {
            getDataProvider().saveMetadata(getAppContext(), metadata);
        }
    }
}
