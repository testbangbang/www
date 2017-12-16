package com.onyx.android.eschool.net;

import android.content.Context;
import android.util.Log;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.v2.LoginByHardwareInfoRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by suicheng on 2017/6/12.
 */

public class AuthenticatorImpl implements Authenticator {

    private Context appContext;
    private CloudManager cloudManager;

    public AuthenticatorImpl(Context appContext, CloudManager cloudManager) {
        this.appContext = appContext.getApplicationContext();
        this.cloudManager = cloudManager;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        Log.e(getClass().getSimpleName(), "execute Authenticator");

        resetAuthenticator(cloudManager);
        boolean success = reLogin(appContext, cloudManager);
        if (!success) {
            return null;
        }
        restoreAuthenticator(cloudManager);

        Request.Builder builder = response.request().newBuilder();
        addHeader(builder, Constant.HEADER_AUTHORIZATION,
                ContentService.CONTENT_AUTH_PREFIX + cloudManager.getToken());
        return builder.build();
    }

    private boolean reLogin(Context context, CloudManager cloudManager) {
        final LoginByHardwareInfoRequest accountLoadRequest = new LoginByHardwareInfoRequest<>(EduAccountProvider.CONTENT_URI,
                EduAccount.class);
        accountLoadRequest.setLoadOnlyFromCloud(true);
        accountLoadRequest.setContext(context);
        try {
            accountLoadRequest.execute(cloudManager);
            NeoAccountBase account = accountLoadRequest.getAccount();
            if (!NeoAccountBase.isValid(account) || accountLoadRequest.getException() != null) {
                return false;
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "exec Authenticator", e);
            return false;
        }
        return true;
    }

    private void resetAuthenticator(CloudManager cloudManager) {
        ServiceFactory.removeClient(cloudManager.getCloudConf().getApiBase());
    }

    private void restoreAuthenticator(CloudManager cloudManager) {
        ServiceFactory.addAuthenticator(cloudManager.getCloudConf().getApiBase(), this);
    }

    private void addHeader(Request.Builder builder, String headerKey, String headerValue) {
        builder.removeHeader(headerKey);
        builder.addHeader(headerKey, headerValue);
    }
}
