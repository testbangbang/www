package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by suicheng on 2016/9/20.
 */
public class AccountResetPwdRequest extends BaseCloudRequest {
    private Map<String, String> map = new HashMap<>();

    public AccountResetPwdRequest(String token, String password) {
        map.put(Constant.TOKEN_TAG, token);
        map.put(Constant.PASSWORD_TAG, password);
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call call = ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .resetAccountPwd(map);
        executeCall(call);
    }
}
