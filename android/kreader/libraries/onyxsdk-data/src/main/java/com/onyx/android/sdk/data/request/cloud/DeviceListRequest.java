package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/28.
 */
public class DeviceListRequest extends BaseCloudRequest {

    private List<Device> deviceList;

    public DeviceListRequest() {
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<List<Device>> response = ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .getBoundDeviceList(getAccountSessionToken()).execute();
        if (response.isSuccessful()) {
            deviceList = response.body();
        }
    }
}
