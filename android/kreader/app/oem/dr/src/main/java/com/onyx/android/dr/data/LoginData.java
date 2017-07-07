package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.cloud.LoginByAdminRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;

/**
 * Created by hehai on 17-6-30.
 */

public class LoginData {
    public void login(LoginByAdminRequest request, BaseCallback callback) {
        final CloudRequestChain requestChain = new CloudRequestChain();
        requestChain.addRequest(request, callback);
        requestChain.execute(DRApplication.getInstance(), DRApplication.getCloudStore().getCloudManager());
    }
}
