package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.provider.DataProviderBase;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hehai on 17-11-1.
 */

public abstract class RxBaseDBRequest extends RxBaseDataRequest {
    private static DataProviderBase dataProvider;

    public RxBaseDBRequest(DataManager dm) {
        super(dm);
    }

    public DataProviderBase getDataProvider() {
        if (dataProvider != null) {
            return dataProvider;
        }
        return getDataManager().getRemoteContentProvider();
    }

    public void setDataProvider(DataProviderBase dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    protected Scheduler switchScheduler() {
        return Schedulers.io();
    }
}
