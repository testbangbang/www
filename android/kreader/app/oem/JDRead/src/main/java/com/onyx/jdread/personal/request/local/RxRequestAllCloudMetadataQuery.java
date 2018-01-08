package com.onyx.jdread.personal.request.local;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.LocalDataProvider;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;

import java.util.List;

/**
 * Created by li on 2018/1/6.
 */

public class RxRequestAllCloudMetadataQuery extends RxBaseDBRequest {
    private List<Metadata> metadatas;

    public RxRequestAllCloudMetadataQuery(DataManager dm) {
        super(dm);
    }

    @Override
    public Object call() throws Exception {
        LocalDataProvider localDataProvider = (LocalDataProvider) getDataProvider();
        metadatas = localDataProvider.findCloudMetadata();
        return this;
    }

    public List<Metadata> getMetadatas() {
        return metadatas;
    }
}
