package com.onyx.jdread.library.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;

/**
 * Created by hehai on 18-1-19.
 */

public class RxClearSearchHistory extends RxBaseDBRequest {
    public RxClearSearchHistory(DataManager dm) {
        super(dm);
    }

    @Override
    public RxClearSearchHistory call() throws Exception {
        getDataProvider().clearSearchHistory();
        return this;
    }
}
