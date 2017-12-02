package com.onyx.android.sdk.data.rxrequest.data.cloud.base;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.v1.OnyxBookStoreService;
import com.onyx.android.sdk.data.v1.ServiceFactory;

/**
 * Created by jackdeng on 2017/11/7.
 */

public abstract class RxBaseBookStoreRequest extends RxBaseCloudRequest {
    protected final String TAG = this.getClass().getSimpleName();
    private CloudManager cloudManager;
    private OnyxBookStoreService service;

    public RxBaseBookStoreRequest() {
        initService();
    }

    private void initService() {
        if (cloudManager == null) {
            cloudManager = new CloudManager();
        }

        if (service == null) {
            service = ServiceFactory.getBookStoreService(cloudManager.getCloudConf().getApiBase());
        }
    }

    public OnyxBookStoreService getService() {
        initService();
        return service;
    }
}