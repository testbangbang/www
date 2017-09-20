package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.ForgetPasswordData;
import com.onyx.android.dr.interfaces.ForgetPasswordView;
import com.onyx.android.dr.request.cloud.RequestPhoneVerify;
import com.onyx.android.dr.request.cloud.RequestResetPassword;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.NewPassword;
import com.onyx.android.sdk.data.model.v2.VerifyCode;
import com.onyx.android.sdk.utils.FileUtils;

/**
 * Created by hehai on 17-9-18.
 */

public class ForgetPasswordPresenter {
    private ForgetPasswordView forgetPasswordView;
    private ForgetPasswordData forgetPasswordData;

    public ForgetPasswordPresenter(ForgetPasswordView forgetPasswordView) {
        this.forgetPasswordView = forgetPasswordView;
        forgetPasswordData = new ForgetPasswordData();
    }

    public void getVerificationCode(String phone) {
        final RequestPhoneVerify req = new RequestPhoneVerify(phone);
        forgetPasswordData.getVerificationCode(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                VerifyCode verifyCode = req.getVerifyCode();
                if (verifyCode != null) {
                    forgetPasswordView.setVerifyCode(verifyCode);
                }
            }
        });
    }

    public void setNewPassword(VerifyCode verify, String password) {
        NewPassword newPassword = new NewPassword();
        newPassword.newPassword = password;
        final RequestResetPassword req = new RequestResetPassword(verify.nonceStr + FileUtils.computeMD5(verify.code), newPassword);
        forgetPasswordData.setNewPassword(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (req.getVerifyCode() != null) {
                    forgetPasswordView.setResult(req.getVerifyCode().message);
                }
            }
        });
    }
}
