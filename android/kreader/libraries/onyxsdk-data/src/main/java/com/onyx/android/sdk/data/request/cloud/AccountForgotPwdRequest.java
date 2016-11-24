package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.StringUtils;

import retrofit2.Call;

/**
 * Created by suicheng on 2016/9/20.
 */
public class AccountForgotPwdRequest extends BaseCloudRequest {

    private OnyxAccount account;

    /**
     * OnyxAccount.email 必须
     */
    public AccountForgotPwdRequest(OnyxAccount account) {
        this.account = account;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call call = ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .forgotAccountPwd(account);
        executeCall(call);
    }
}
