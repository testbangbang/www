package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.AuthToken;
import com.onyx.android.sdk.data.model.v2.ContentAccount;
import com.onyx.android.sdk.data.model.v2.ContentAuthAccount;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/5/18.
 */

public class LoadAccountFromCloudRequest extends BaseCloudRequest {

    private ContentAuthAccount authAccount;
    private ContentAccount contentAccount;
    private String token;

    public LoadAccountFromCloudRequest(ContentAuthAccount authAccount) {
        this.authAccount = authAccount;
    }

    public ContentAccount getContentAccount() {
        return contentAccount;
    }

    public String getToken() {
        return token;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        token = getAuthToken(parent);
        contentAccount = getContentAccount(parent);
    }

    private String getAuthToken(CloudManager parent) throws Exception {
        String token = null;
        Response<AuthToken> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .getAccountToken(authAccount));
        if (response.isSuccessful()) {
            token = response.body().token;
            parent.setToken(token);
        }
        return token;
    }

    private ContentAccount getContentAccount(CloudManager parent) throws Exception {
        ContentAccount account = null;
        Response<ContentAccount> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .getAccount(ContentService.CONTENT_AUTH_PREFIX + parent.getToken()));
        if (response.isSuccessful()) {
            account = response.body();
        }
        return account;
    }

    public void setAuthAccount(ContentAuthAccount authAccount) {
        this.authAccount = authAccount;
    }
}
