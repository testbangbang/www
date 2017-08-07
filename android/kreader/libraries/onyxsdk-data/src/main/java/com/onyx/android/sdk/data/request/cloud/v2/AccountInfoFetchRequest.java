package com.onyx.android.sdk.data.request.cloud.v2;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/7/13.
 */
public class AccountInfoFetchRequest extends BaseCloudRequest {

    private boolean tokenInvalid = false;
    private NeoAccountBase resultAccount;

    public NeoAccountBase getResultAccount() {
        return resultAccount;
    }

    public boolean isTokenInvalid() {
        return tokenInvalid;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        resultAccount = getAccountInfoFromCloudImpl(parent);
    }

    private NeoAccountBase getAccountInfoFromCloudImpl(CloudManager parent) throws Exception {
        NeoAccountBase account = null;
        Response<ResponseBody> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .getAccount());
        if (response.isSuccessful()) {
            account = JSON.parseObject(response.body().string(), NeoAccountBase.class);
            NeoAccountBase.parseInfo(account);
        } else if (response.code() == 401) {
            tokenInvalid = true;
        }
        return account;
    }
}
