package com.onyx.android.sdk.data.rxrequest.data.cloud.base;

import com.onyx.android.sdk.data.DataBundle;
import com.onyx.android.sdk.data.v1.OnyxAccountService;
import com.onyx.android.sdk.data.v1.ServiceFactory;

/**
 * Created by jackdeng on 2017/11/7.
 */

public abstract class RxBaseAccountRequest extends RxBaseCloudRequest {
    protected final String TAG = this.getClass().getSimpleName();
    private DataBundle dataBundle;
    private OnyxAccountService service;

    public RxBaseAccountRequest() {
        initService();
    }

    private void initService() {
        if (dataBundle == null) {
            dataBundle = new DataBundle();
        }

        if (service == null) {
            service = ServiceFactory.getAccountService(dataBundle.getCloudManager().getCloudConf().getApiBase());
        }
    }

    public OnyxAccountService getService() {
        initService();
        return service;
    }
}