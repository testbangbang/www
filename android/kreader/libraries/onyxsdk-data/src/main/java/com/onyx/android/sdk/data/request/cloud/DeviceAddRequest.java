package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Device;

import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Call;
import retrofit2.Response;

public class DeviceAddRequest extends BaseCloudRequest {
    private static final String TAG = DeviceAddRequest.class.getSimpleName();
    private Device device;
    private Device bindDevice;

    public DeviceAddRequest(final Device value) {
        this.device = value;
    }

    public final Device getBoundDevice() {
        return bindDevice;
    }

    public void execute(final CloudManager parent) throws Exception {
        Call<Device> call = ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .addDevice(device, getAccountSessionToken());
        Response<Device> response = executeCall(call);
        if (response.isSuccessful()) {
            bindDevice = response.body();
            dumpMessage(TAG + " installationMap", bindDevice.installationMap.toString());
        }
    }
}
