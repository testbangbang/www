package com.onyx.cloud.store.request;

import com.onyx.cloud.CloudManager;
import com.onyx.cloud.model.Device;
import com.onyx.cloud.service.OnyxAccountService;
import com.onyx.cloud.service.ServiceFactory;

import retrofit2.Call;
import retrofit2.Response;

public class AddDeviceRequest extends BaseCloudRequest {
    private static final String TAG = AddDeviceRequest.class.getSimpleName();
    private Device device;
    private Device bindDevice;
    private String sessionToken;

    public AddDeviceRequest(final Device value, final String sessionToken) {
        this.device = value;
        this.sessionToken = sessionToken;
    }

    public final Device getBindedDevice() {
        return bindDevice;
    }

    public void execute(final CloudManager parent) throws Exception {
        Call<Device > call = ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .addDevice(device, sessionToken);
        Response<Device > response = call.execute();
        if (response.isSuccessful()) {
            bindDevice = response.body();
            dumpMessage(TAG + " installationMap", bindDevice.installationMap.toString());
        }
    }
}
