package com.onyx.android.dr.request.cloud;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.v2.AuthToken;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.utils.StringUtils;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by hehai on 2017/6/30.
 */

public class LoginByAdminRequest extends BaseCloudRequest {
    private BaseAuthAccount authAccount;
    private NeoAccountBase neoAccount;

    public LoginByAdminRequest(BaseAuthAccount authAccount) {
        this.authAccount = authAccount;
    }

    public NeoAccountBase getNeoAccount() {
        return neoAccount;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        neoAccount = loginToCloud(getContext(), parent);
    }

    private NeoAccountBase loginToCloud(Context context, CloudManager parent) throws Exception {
        AuthToken authToken = getAuthTokenFromCloud(context, parent);
        if (authToken == null || StringUtils.isNullOrEmpty(authToken.token)) {
            return null;
        }
        return getAccountInfoFromCloudImpl(parent, authToken);
    }

    private NeoAccountBase getAccountInfoFromCloudImpl(CloudManager parent, AuthToken authToken) throws Exception {
        NeoAccountBase account = null;
        Response<ResponseBody> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .getAccount());
        if (response.isSuccessful()) {
            account = JSON.parseObject(response.body().string(), NeoAccountBase.class);
            if (authToken != null) {
                account.token = authToken.token;
                account.tokenExpiresIn = authToken.expires_in;
            }
            NeoAccountBase.parseName(account);
        }
        return account;
    }

    private AuthToken getAuthTokenFromCloud(Context context, CloudManager parent) throws Exception {
        if (authAccount == null) {
            return null;
        }
        AuthToken authToken = null;
        Response<AuthToken> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .getAccountToken(authAccount));
        if (response.isSuccessful()) {
            authToken = response.body();
            parent.setToken(authToken.token);
            updateTokenHeader(parent);
        }
        return authToken;
    }

    private void updateTokenHeader(final CloudManager cloudManager) {
        if (StringUtils.isNotBlank(cloudManager.getToken())) {
            ServiceFactory.addRetrofitTokenHeader(cloudManager.getCloudConf().getApiBase(),
                    Constant.HEADER_AUTHORIZATION,
                    ContentService.CONTENT_AUTH_PREFIX + cloudManager.getToken());
        }
    }
}
