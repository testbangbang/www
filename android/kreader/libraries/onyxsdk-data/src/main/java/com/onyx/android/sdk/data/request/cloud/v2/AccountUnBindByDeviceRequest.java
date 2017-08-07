package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/7/12.
 */
public class AccountUnBindByDeviceRequest extends BaseCloudRequest {

    private NeoAccountBase account;
    private DeviceBind deviceBind;
    private boolean successResult;

    public AccountUnBindByDeviceRequest(NeoAccountBase account, DeviceBind deviceBind) {
        this.account = account;
        this.deviceBind = deviceBind;
    }

    public boolean isSuccessResult() {
        return successResult;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<ResponseBody> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(
                parent.getCloudConf().getApiBase()).unbindUserByDevice(account._id, deviceBind));
        successResult = response.isSuccessful();
    }
}
