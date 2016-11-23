package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.v1.ServiceFactory;

/**
 * Created by suicheng on 2016/9/20.
 */
public class AccountApplyActivateRequest extends BaseCloudRequest {

    public AccountApplyActivateRequest() {
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        executeCall(ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .applyActivateAccount(getAccountSessionToken()));
    }
}
