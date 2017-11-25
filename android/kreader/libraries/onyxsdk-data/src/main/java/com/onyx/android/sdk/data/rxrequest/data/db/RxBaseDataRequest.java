package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.rx.RxRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by john on 29/10/2017.
 */

public abstract class RxBaseDataRequest extends RxRequest {
    protected final String TAG = this.getClass().getSimpleName();

    private DataManager dataManager;

    public RxBaseDataRequest(final DataManager dm) {
        dataManager = dm;
    }

    public final DataManager getDataManager() {
        return dataManager;
    }

    public Scheduler generateScheduler() {
        final ExecutorService svc = dataManager.getRequestManager().getExecutorByIdentifier("library").getSingleThreadPool();
        return Schedulers.from(svc);
    }
}
