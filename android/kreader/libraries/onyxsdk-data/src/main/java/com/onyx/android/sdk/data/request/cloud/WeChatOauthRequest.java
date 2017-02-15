package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.WeChatOauthResp;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/1/22.
 */
public class WeChatOauthRequest extends BaseCloudRequest {
    private static final String grantType = "authorization_code";

    public String appId;
    public String secretId;
    public String code;

    private WeChatOauthResp resp;

    public WeChatOauthRequest(String appId, String secretId, String code) {
        this.appId = appId;
        this.secretId = secretId;
        this.code = code;
    }

    public WeChatOauthResp getResp() {
        return resp;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<WeChatOauthResp> response = executeCall
                (ServiceFactory.getAccountService(parent.getCloudConf().getApiBase()).
                        requestWeChatToken(appId, secretId, code, grantType));
        if (response.isSuccessful()) {
            this.resp = response.body();
        }
    }
}
