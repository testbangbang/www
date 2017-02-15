package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.WeChatUserInfo;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/1/22.
 */
public class WeChatUserInfoRequest extends BaseCloudRequest {

    private String accessToken;
    private String openId;

    private WeChatUserInfo userInfo;

    public WeChatUserInfoRequest(String token, String openid) {
        this.accessToken = token;
        this.openId = openid;
    }

    public WeChatUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<WeChatUserInfo> response = executeCall(ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .requestWeChatUserInfo(accessToken, openId));
        if (response.isSuccessful()) {
            userInfo = response.body();
        }
    }
}
