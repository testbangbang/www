package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;
import android.net.Uri;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.v2.AuthToken;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/6/10.
 */
public class LoginByHardwareInfoRequest<T extends NeoAccountBase> extends BaseCloudRequest {
    private static final String NAME_SECRET = "eefbb54a-ffd1-4e86-9513-f83e15b807c9";
    private static final String PASSWORD_SECRET = "807bb28a-623e-408c-97c5-61177091737b";

    private boolean loadOnlyFromCloud = false;
    private Uri providerUri;
    private T account;

    private Class<T> clazzType;

    public LoginByHardwareInfoRequest(Uri providerUri, Class<T> clazz) {
        this.providerUri = providerUri;
        this.clazzType = clazz;
    }

    public T getAccount() {
        return account;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (loadOnlyFromCloud) {
            account = LoginToCloud(getContext(), parent);
            return;
        }
        account = LoginToLocal(parent);
        if (account == null) {
            account = LoginToCloud(getContext(), parent);
        } else {
            T updatedAccount = getAccountInfoFromCloud(parent, account);
            if (updatedAccount != null) {
                account = updatedAccount;
            }
        }
    }

    private T getAccountInfoFromCloud(CloudManager parent, T oldAccount) {
        T cloudAccount = null;
        try {
            cloudAccount = getAccountInfoFromCloud(parent);
            if (oldAccount != null) {
                cloudAccount.token = oldAccount.token;
                cloudAccount.tokenExpiresIn = oldAccount.tokenExpiresIn;
                cloudAccount.setCreatedAt(oldAccount.getCreatedAt());
            }
            accountSaveToDb(cloudAccount);
        } catch (Exception e) {
        }
        return cloudAccount;
    }

    private T LoginToLocal(CloudManager parent) {
        T account = null;
        try {
            account = ContentUtils.querySingle(providerUri, clazzType, ConditionGroup.clause(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!NeoAccountBase.isValid(account) || account.isTokenTimeExpired()) {
            return null;
        }
        NeoAccountBase.parseName(account);
        parent.setToken(account.token);
        updateTokenHeader(parent);
        return account;
    }

    private T LoginToCloud(Context context, CloudManager parent) throws Exception {
        AuthToken authToken = getAuthTokenFromCloud(context, parent);
        if (authToken == null || StringUtils.isNullOrEmpty(authToken.token)) {
            return null;
        }
        T account = getAccountInfoFromCloud(parent, authToken);
        accountSaveToDb(account);
        return account;
    }

    private void accountSaveToDb(T account) {
        if (account == null) {
            return;
        }
        try {
            account.beforeSave();
            FlowManager.getContext().getContentResolver().delete(providerUri, null, null);
            ContentUtils.insert(providerUri, account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AuthToken getAuthTokenFromCloud(Context context, CloudManager parent) throws Exception {
        BaseAuthAccount authAccount = createAuthAccountFromHardware(context);
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

    private T getAccountInfoFromCloud(CloudManager parent, AuthToken authToken) throws Exception {
        T account = null;
        Response<ResponseBody> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .getAccount());
        if (response.isSuccessful()) {
            account = JSON.parseObject(response.body().string(), clazzType);
            if (authToken != null) {
                account.token = authToken.token;
                account.tokenExpiresIn = authToken.expires_in;
            }
            NeoAccountBase.parseName(account);
        }
        return account;
    }

    private T getAccountInfoFromCloud(CloudManager parent) throws Exception {
        return getAccountInfoFromCloud(parent, null);
    }

    public static BaseAuthAccount createAuthAccountFromHardware(Context context) {
        String macAddress = NetworkUtil.getMacAddress(context);
        if (StringUtils.isNullOrEmpty(macAddress)) {
            return null;
        }
        return BaseAuthAccount.create(FileUtils.computeMD5(macAddress + NAME_SECRET),
                FileUtils.computeMD5(macAddress + PASSWORD_SECRET));
    }

    private void updateTokenHeader(final CloudManager cloudManager) {
        if (StringUtils.isNotBlank(cloudManager.getToken())) {
            ServiceFactory.addRetrofitTokenHeader(cloudManager.getCloudConf().getApiBase(),
                    Constant.HEADER_AUTHORIZATION,
                    ContentService.CONTENT_AUTH_PREFIX + cloudManager.getToken());
        }
    }

    public void setLoadOnlyFromCloud(boolean loadOnlyFromCloud) {
        this.loadOnlyFromCloud = loadOnlyFromCloud;
    }
}
