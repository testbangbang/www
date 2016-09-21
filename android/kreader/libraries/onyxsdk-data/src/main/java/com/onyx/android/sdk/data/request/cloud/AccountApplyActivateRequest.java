package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.StringUtils;

import retrofit2.Call;

/**
 * Created by suicheng on 2016/9/20.
 */
public class AccountApplyActivateRequest extends BaseCloudRequest {

    private String sessionToken;

    public AccountApplyActivateRequest(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call call = ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .applyActivateAccount(sessionToken);
        call.execute();
    }
}
