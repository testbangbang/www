package com.onyx.einfo.net;

import android.content.Context;
import android.util.Log;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.v2.LoginByHardwareInfoRequest;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.einfo.InfoApp;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by suicheng on 2017/6/12.
 */

public class AuthenticatorImpl implements Authenticator {
    private static final String TAG = "AuthenticatorImpl";

    private Context appContext;
    private CloudManager cloudManager;

    public AuthenticatorImpl(Context appContext, CloudManager cloudManager) {
        this.appContext = appContext.getApplicationContext();
        this.cloudManager = cloudManager;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        Log.e(TAG, "execute AuthenticatorImpl");
        Request.Builder builder = response.request().newBuilder();
        final LoginByHardwareInfoRequest accountLoadRequest = new LoginByHardwareInfoRequest<>(EduAccountProvider.CONTENT_URI,
                EduAccount.class);
        accountLoadRequest.setLoadOnlyFromCloud(true);
        accountLoadRequest.setContext(appContext);
        try {
            accountLoadRequest.execute(cloudManager);
            NeoAccountBase account = accountLoadRequest.getAccount();
            if (!NeoAccountBase.isValid(account) || accountLoadRequest.getException() != null) {
                return null;
            }
            builder.removeHeader(Constant.HEADER_AUTHORIZATION);
            builder.addHeader(Constant.HEADER_AUTHORIZATION,
                    ContentService.CONTENT_AUTH_PREFIX + cloudManager.getToken());
        } catch (Exception e) {
            Log.e(TAG, "exec authenticate", e);
            return null;
        }
        return builder.build();
    }
}
