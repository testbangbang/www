package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.AuthToken;
import com.onyx.android.sdk.data.model.v2.ContentAuthAccount;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/5/18.
 */

public class ContentAccountTokenRequest extends BaseCloudRequest {

    private ContentAuthAccount account;
    private AuthToken authToken;

    public ContentAccountTokenRequest(ContentAuthAccount account) {
        this.account = account;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<AuthToken> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .getAccountToken(account));
        if (response.isSuccessful()) {
            authToken = response.body();
            parent.setToken(authToken.token);
        }
    }

    public void setAccount(ContentAuthAccount account) {
        this.account = account;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
