package com.onyx.jdread.personal.request.local;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.StatisticalData;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;

import java.util.List;

/**
 * Created by li on 2018/3/19.
 */

public class RxGetStatisticsDataRequest extends RxBaseDBRequest {
    private List<StatisticalData> list;

    public RxGetStatisticsDataRequest(DataManager dm) {
        super(dm);
    }

    @Override
    public Object call() throws Exception {
        list = getDataProvider().findAllStatistics();
        return this;
    }

    public List<StatisticalData> getList() {
        return list;
    }
}
