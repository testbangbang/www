package com.onyx.jdread.personal.request.local;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;

/**
 * Created by li on 2018/3/19.
 */

public class RxDeleteStatisticsRequest extends RxBaseDBRequest {
    private String cloudId;

    public RxDeleteStatisticsRequest(DataManager dm, String cloudId) {
        super(dm);
        this.cloudId = cloudId;
    }

    @Override
    public Object call() throws Exception {
        getDataProvider().deleteStatisticDataByCloudId(cloudId);
        return this;
    }
}
