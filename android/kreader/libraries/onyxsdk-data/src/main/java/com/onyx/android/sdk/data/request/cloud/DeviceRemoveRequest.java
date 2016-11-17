package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/28.
 */
public class DeviceRemoveRequest extends BaseCloudRequest {

    private String deviceId;
    private boolean isSuccess;

    public DeviceRemoveRequest(final String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean getResult() {
        return isSuccess;
    }

    public void execute(final CloudManager parent) throws Exception {
        Response<ResponseBody> response = executeCall(ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .removeBoundDevice(deviceId, getAccountSessionToken()));
        isSuccess = response.isSuccessful();
    }
}
