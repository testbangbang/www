package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.StringUtils;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/10/19.
 */

public class AccountOAuthRequest extends BaseCloudRequest {

    private String code;
    private OnyxAccount onyxAccount;

    public AccountOAuthRequest(String tokenCode) {
        code = tokenCode;
    }

    public OnyxAccount getOnyxAccount() {
        return onyxAccount;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (StringUtils.isNullOrEmpty(code)) {
            return;
        }
        Response<OnyxAccount> response = executeCall(ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .getOAuthAccount(code));
        if (response.isSuccessful()) {
            onyxAccount = response.body();
        }
    }
}
