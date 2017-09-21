package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.data.ForgetPasswordData;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.interfaces.ForgetPasswordView;
import com.onyx.android.dr.manager.LeanCloudManager;
import com.onyx.android.dr.request.cloud.RequestPhoneVerify;
import com.onyx.android.dr.request.cloud.RequestResetPassword;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.model.v2.NewPassword;
import com.onyx.android.sdk.data.model.v2.VerifyCode;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;
import com.onyx.android.sdk.data.request.cloud.v2.CloudIndexServiceRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;

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
        final CloudIndexServiceRequest indexServiceRequest = new CloudIndexServiceRequest(DeviceConfig.sharedInstance(DRApplication.getInstance()).getCloudMainIndexServerApi(),
                createIndexService(DRApplication.getInstance()));
        CloudRequestChain requestChain = new CloudRequestChain();
        requestChain.addRequest(indexServiceRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DRApplication.getInstance().setHaveIndexService(indexServiceRequest.getResultIndexService() != null);
            }
        });

        final RequestPhoneVerify req = new RequestPhoneVerify(phone);
        requestChain.addRequest(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                VerifyCode verifyCode = req.getVerifyCode();
                if (verifyCode != null) {
                    forgetPasswordView.setVerifyCode(verifyCode);
                }
            }
        });

        requestChain.execute(DRApplication.getInstance(), DRApplication.getCloudStore().getCloudManager());
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

    private IndexService createIndexService(Context context) {
        IndexService authService = new IndexService();
        authService.mac = NetworkUtil.getMacAddress(context);
        authService.installationId = LeanCloudManager.getInstallationId();
        return authService;
    }
}
