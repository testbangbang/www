package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.AuthToken;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/5/31.
 */
public class AccountLoadFromCloudRequest<T extends NeoAccountBase> extends BaseCloudRequest {

    private static final String NAME_SECRET = "eefbb54a-ffd1-4e86-9513-f83e15b807c9";
    private static final String PASSWORD_SECRET = "807bb28a-623e-408c-97c5-61177091737b";


    private BaseAuthAccount authAccount;
    private T neoAccount;
    private String token;

    private Class<T> clazzType;

    public AccountLoadFromCloudRequest(BaseAuthAccount authAccount, Class<T> clazz) {
        this.authAccount = authAccount;
        this.clazzType = clazz;
    }

    public T getNeoAccount() {
        return neoAccount;
    }

    public String getToken() {
        return token;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        token = getAuthToken(parent);
        neoAccount = getContentAccount(parent);
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

    private T getContentAccount(CloudManager parent) throws Exception {
        T account = null;
        Response<ResponseBody> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .getAccount(ContentService.CONTENT_AUTH_PREFIX + parent.getToken()));
        if (response.isSuccessful()) {
            account = JSON.parseObject(response.body().string(), clazzType);
            account.token = parent.getToken();
            NeoAccountBase.parseName(account);
        }
        return account;
    }

    public void setAuthAccount(BaseAuthAccount authAccount) {
        this.authAccount = authAccount;
    }

    public static BaseAuthAccount createAuthAccountFromHardware(Context context) {
        String macAddress = NetworkUtil.getMacAddress(context);
        if (StringUtils.isNullOrEmpty(macAddress)) {
            return null;
        }
        return BaseAuthAccount.create(FileUtils.computeMD5(macAddress + NAME_SECRET),
                FileUtils.computeMD5(macAddress + PASSWORD_SECRET));
    }


}
