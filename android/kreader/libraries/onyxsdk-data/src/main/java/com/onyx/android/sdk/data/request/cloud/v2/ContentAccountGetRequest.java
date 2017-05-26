package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.ContentAccount;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/5/18.
 */

public class ContentAccountGetRequest<T> extends BaseCloudRequest {

    private ContentAccount account;

    public ContentAccountGetRequest() {
    }

    public ContentAccount getAccount() {
        return account;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<ContentAccount> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .getAccount(ContentService.CONTENT_AUTH_PREFIX + parent.getToken()));
        if (response.isSuccessful()) {
            account = response.body();
        }
    }
}
