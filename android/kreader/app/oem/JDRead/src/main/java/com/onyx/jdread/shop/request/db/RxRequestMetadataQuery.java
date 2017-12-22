package com.onyx.jdread.shop.request.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;

/**
 * Created by 12 on 2017/4/14.
 */

public class RxRequestMetadataQuery extends RxBaseDBRequest {

    private String absolutePath;
    private Metadata metadata;

    public RxRequestMetadataQuery(DataManager dm, String absolutePath) {
        super(dm);
        this.absolutePath = absolutePath;
    }

    public Metadata getMetadataResult() {
        return metadata;
    }

    private void queryBook() {
        metadata = getDataProvider().findMetadataByPath(getAppContext(), absolutePath);
    }

    @Override
    public Object call() throws Exception {
        queryBook();
        return this;
    }
}
