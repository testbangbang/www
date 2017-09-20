package com.onyx.android.dr.interfaces;

import com.onyx.android.sdk.data.model.v2.VerifyCode;

/**
 * Created by hehai on 17-9-18.
 */

public interface ForgetPasswordView {
    void setVerifyCode(VerifyCode code);

    void setResult(String message);
}
