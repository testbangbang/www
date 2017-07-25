package com.onyx.einfo.net;

import android.content.Context;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.request.cloud.v2.LoginByHardwareInfoRequest;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by suicheng on 2017/6/12.
 */

public class AuthenticatorImpl implements Authenticator {

    private Context context;
    private CloudManager cloudManager;

    public AuthenticatorImpl(Context context, CloudManager cloudManager) {
        this.context = context;
        this.cloudManager = cloudManager;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        final LoginByHardwareInfoRequest accountLoadRequest = new LoginByHardwareInfoRequest<>(EduAccountProvider.CONTENT_URI,
                EduAccount.class);
        accountLoadRequest.setLoadOnlyFromCloud(true);
        try {
            accountLoadRequest.execute(cloudManager);
            if (accountLoadRequest.getAccount() == null) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return response.request().newBuilder().build();
    }
}
