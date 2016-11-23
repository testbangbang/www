package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/10/24.
 */

public class AccountSignOutRequest extends BaseCloudRequest {

    private boolean result = false;

    public boolean getResult() {
        return result;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response response = executeCall(ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .signout(getAccountSessionToken()));
        result = response.isSuccessful();
    }
}
