package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.cloud.RequestPhoneVerify;
import com.onyx.android.dr.request.cloud.RequestResetPassword;
import com.onyx.android.sdk.common.request.BaseCallback;

/**
 * Created by hehai on 17-9-19.
 */

public class ForgetPasswordData {
    public void getVerificationCode(RequestPhoneVerify req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void setNewPassword(RequestResetPassword req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
