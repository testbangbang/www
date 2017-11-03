package com.onyx.android.sdk.data.rxrequest.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDataRequest;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hehai on 17-11-1.
 */

public abstract class RxBaseFSRequest extends RxBaseDataRequest {

    public RxBaseFSRequest(DataManager dm) {
        super(dm);
    }

    @Override
    protected Scheduler switchScheduler() {
        return Schedulers.io();
    }
}
