package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.VerifyResult;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.data.utils.ResultCode;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/7/22.
 */
public class PhoneVerifyRequest extends BaseCloudRequest {

    private String phone;
    private VerifyResult verifyResult;

    public PhoneVerifyRequest(String phone) {
        this.phone = phone;
    }

    public VerifyResult getVerifyResult() {
        return verifyResult;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<ResponseBody> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(
                parent.getCloudConf().getApiBase()).applyPhoneVerify(phone));
        if (response.isSuccessful()) {
            verifyResult = JSONObjectParseUtils.parseObject(response.body().string(), VerifyResult.class);
        }
    }
}
