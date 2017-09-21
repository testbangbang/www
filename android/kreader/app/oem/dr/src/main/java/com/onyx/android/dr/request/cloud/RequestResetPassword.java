package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.NewPassword;
import com.onyx.android.sdk.data.model.v2.VerifyCode;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by hehai on 17-9-7.
 */

public class RequestResetPassword extends BaseCloudRequest {
    private NewPassword newPassword;
    private VerifyCode verifyCode;
    private String token;

    public RequestResetPassword(String token, NewPassword newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    public VerifyCode getVerifyCode() {
        return verifyCode;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        createOrders(parent);
    }

    private void createOrders(CloudManager parent) {
        try {
            Response<VerifyCode> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase()).setPassword(token, newPassword));
            if (response != null) {
                verifyCode = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
