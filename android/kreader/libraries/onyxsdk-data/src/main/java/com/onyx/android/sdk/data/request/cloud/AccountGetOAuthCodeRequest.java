package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.OAuthAccountData;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/1/22.
 */
public class AccountGetOAuthCodeRequest extends BaseCloudRequest {

    private OAuthAccountData oauthAccount;
    private String thirdPlatform;
    private String oauthCode;

    public AccountGetOAuthCodeRequest(String thirdPlatform, OAuthAccountData data) {
        this.thirdPlatform = thirdPlatform;
        this.oauthAccount = data;
    }

    public String getOauthCode() {
        return oauthCode;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<ResponseBody> response = executeCall(ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .requestOnyxOauthCode(thirdPlatform, oauthAccount));
        if (response.isSuccessful()) {
            oauthCode = response.body().string();
        }
    }
}
