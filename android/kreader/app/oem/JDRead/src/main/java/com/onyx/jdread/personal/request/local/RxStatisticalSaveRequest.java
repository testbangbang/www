package com.onyx.jdread.personal.request.local;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.StatisticalData;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;

/**
 * Created by li on 2018/3/19.
 */

public class RxStatisticalSaveRequest extends RxBaseDBRequest {
    private StatisticalData data;

    public RxStatisticalSaveRequest(DataManager dm, StatisticalData data) {
        super(dm);
        this.data = data;
    }

    @Override
    public Object call() throws Exception {
        getDataProvider().saveStatisticData(data);
        return this;
    }
}
