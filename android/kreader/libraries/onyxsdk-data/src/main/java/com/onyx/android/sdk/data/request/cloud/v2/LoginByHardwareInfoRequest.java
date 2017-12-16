package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.v2.AuthToken;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.data.v2.MacHeaderInterceptor;
import com.onyx.android.sdk.data.v2.TokenHeaderInterceptor;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import okhttp3.Interceptor;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/6/10.
 */
public class LoginByHardwareInfoRequest<T extends NeoAccountBase> extends BaseCloudRequest {
    private static final String TAG = "LoginByHardwareRequest";

    private static final String NAME_SECRET = "eefbb54a-ffd1-4e86-9513-f83e15b807c9";
    private static final String PASSWORD_SECRET = "807bb28a-623e-408c-97c5-61177091737b";

    private int localLoadRetryCount = 1;
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
            account = reLoginToCloud(parent);
            return;
        }
        account = LoginToLocal(parent, localLoadRetryCount);
        if (account == null) {
            account = LoginToCloud(getContext(), parent);
        } else {
            T updatedAccount = getAccountInfoFromCloud(parent);
            saveUpdatedAccount(updatedAccount);
            if (updatedAccount != null) {
                account = updatedAccount;
            }
        }
    }

    private void saveUpdatedAccount(T updatedAccount) {
        T oldAccount = loadLocalAccount(localLoadRetryCount);
        if (updatedAccount != null && oldAccount != null) {
            updatedAccount.token = oldAccount.token;
            updatedAccount.tokenExpiresIn = oldAccount.tokenExpiresIn;
            updatedAccount.setCreatedAt(oldAccount.getCreatedAt());
        }
        if (updatedAccount != null) {
            accountSaveToDb(updatedAccount);
        }
    }

    private T getAccountInfoFromCloud(CloudManager parent) {
        T cloudAccount = null;
        try {
            cloudAccount = getAccountInfoFromCloudImpl(parent, null);
        } catch (Exception e) {
        }
        return cloudAccount;
    }

    private T reLoginToCloud(CloudManager parent) {
        try {
            deleteAllAccount();
            resetToken(parent);
            account = LoginToCloud(getContext(), parent);
        } catch (Exception e) {
            Log.e(TAG, "reLoginToCloud", e);
            account = null;
        }
        return account;
    }

    private void resetToken(CloudManager parent) {
        parent.setToken(null);
    }

    private void deleteAllAccount() {
        FlowManager.getContext().getContentResolver().delete(providerUri, null, null);
    }

    private T LoginToLocal(CloudManager parent, int retryCount) {
        T account = null;
        for (int i = 0; i < retryCount; i++) {
            account = LoginToLocal(parent);
            if (account != null) {
                break;
            }
            if (retryCount > 1) {
                TestUtils.sleep(300);
            }
        }
        return account;
    }

    private T LoginToLocal(CloudManager parent) {
        T account = loadLocalAccount();
        if (!NeoAccountBase.isValid(account) || account.isTokenTimeExpired()) {
            return null;
        }
        NeoAccountBase.parseName(account);
        parent.setToken(account.token);
        updateHeadersInterceptor(parent);
        return account;
    }

    private T loadLocalAccount() {
        T account = null;
        try {
            account = ContentUtils.querySingle(providerUri, clazzType, ConditionGroup.clause(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return account;
    }

    private T loadLocalAccount(int retryCount) {
        T account = null;
        for (int i = 0; i < retryCount; i++) {
            account = loadLocalAccount();
            if (account != null) {
                break;
            }
            if (retryCount > 1) {
                TestUtils.sleep(300);
            }
        }
        return account;
    }

    private T LoginToCloud(Context context, CloudManager parent) throws Exception {
        AuthToken authToken = getAuthTokenFromCloud(context, parent);
        if (authToken == null || StringUtils.isNullOrEmpty(authToken.token)) {
            return null;
        }
        T account = getAccountInfoFromCloudImpl(parent, authToken);
        accountSaveToDb(account);
        return account;
    }

    private void accountSaveToDb(T account) {
        if (account == null) {
            return;
        }
        try {
            deleteAllAccount();
            account.beforeSave();
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
            updateHeadersInterceptor(parent);
        }
        return authToken;
    }

    private T getAccountInfoFromCloudImpl(CloudManager parent, AuthToken authToken) throws Exception {
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

    public static BaseAuthAccount createAuthAccountFromHardware(Context context) {
        String macAddress = NetworkUtil.getMacAddress(context);
        if (StringUtils.isNullOrEmpty(macAddress)) {
            return null;
        }
        return BaseAuthAccount.create(FileUtils.computeMD5(macAddress + NAME_SECRET),
                FileUtils.computeMD5(macAddress + PASSWORD_SECRET));
    }

    private void updateHeadersInterceptor(final CloudManager cloudManager) {
        Interceptor[] interceptors = new Interceptor[2];
        interceptors[0] = new TokenHeaderInterceptor(Constant.HEADER_AUTHORIZATION, ContentService.CONTENT_AUTH_PREFIX + cloudManager.getToken());
        interceptors[1] = new MacHeaderInterceptor(Constant.MAC_TAG, NetworkUtil.getMacAddress(getContext()));
        ServiceFactory.addRetrofitInterceptor(cloudManager.getCloudConf().getApiBase(), interceptors);
    }

    public void setLoadOnlyFromCloud(boolean loadOnlyFromCloud) {
        this.loadOnlyFromCloud = loadOnlyFromCloud;
    }

    public void setLocalLoadRetryCount(int retryCount) {
        this.localLoadRetryCount = retryCount <= 0 ? 1 : retryCount;
    }
}
