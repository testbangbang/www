package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.rx.RxRequest;

/**
 * Created by john on 29/10/2017.
 */

public abstract class RxBaseDataRequest extends RxRequest {


    private DataManager dataManager;

    public RxBaseDataRequest(final DataManager dm) {
        dataManager = dm;
    }

    public final DataManager getDataManager() {
        return dataManager;
    }
}
