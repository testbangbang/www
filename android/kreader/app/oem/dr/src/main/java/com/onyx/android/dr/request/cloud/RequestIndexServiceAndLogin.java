package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.action.AuthTokenAction;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;

/**
 * Created by hehai on 17-9-21.
 */

public class RequestIndexServiceAndLogin extends AutoNetWorkConnectionBaseCloudRequest {
    private BaseAuthAccount account;

    public RequestIndexServiceAndLogin(BaseAuthAccount account) {
        this.account = account;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        AuthTokenAction authTokenAction = new AuthTokenAction(account);
        authTokenAction.execute(DRApplication.getLibraryDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                invoke(getCallback(), request, e);
            }
        });
    }
}
