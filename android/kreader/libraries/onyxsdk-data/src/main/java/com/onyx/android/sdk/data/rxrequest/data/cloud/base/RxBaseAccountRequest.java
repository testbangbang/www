package com.onyx.android.sdk.data.rxrequest.data.cloud.base;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.v1.OnyxAccountService;
import com.onyx.android.sdk.data.v1.ServiceFactory;

/**
 * Created by jackdeng on 2017/11/7.
 */

public abstract class RxBaseAccountRequest extends RxBaseCloudRequest {
    protected final String TAG = this.getClass().getSimpleName();
    private CloudManager cloudManager;
    private OnyxAccountService service;

    public RxBaseAccountRequest() {
        initService();
    }

    private void initService() {
        if (cloudManager == null) {
            cloudManager = new CloudManager();
        }

        if (service == null) {
            service = ServiceFactory.getAccountService(cloudManager.getCloudConf().getApiBase());
        }
    }

    public OnyxAccountService getService() {
        initService();
        return service;
    }
}