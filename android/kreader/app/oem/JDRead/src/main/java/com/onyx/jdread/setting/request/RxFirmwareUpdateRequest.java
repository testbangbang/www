package com.onyx.jdread.setting.request;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/12/25.
 */

public class RxFirmwareUpdateRequest extends RxBaseCloudRequest {
    private Firmware fw;
    private CloudManager cloudManager;
    private Firmware resultFirmware;

    public RxFirmwareUpdateRequest(CloudManager cloudManager, Firmware current) {
        this.cloudManager = cloudManager;
        this.fw = current;
    }

    public Firmware getResultFirmware() {
        return resultFirmware;
    }

    public boolean isResultFirmwareValid() {
        return resultFirmware != null &&
                resultFirmware.downloadUrlList != null &&
                resultFirmware.downloadUrlList.size() > 0;
    }

    @Override
    public Object call() throws Exception {
        Call<Firmware> call = ServiceFactory.getOTAService(cloudManager.getCloudConf().getApiBase())
                .firmwareUpdate(JSON.toJSONString(fw));
        Response<Firmware> response = call.execute();
        if (response.isSuccessful()) {
            resultFirmware = response.body();
        }
        return this;
    }
}
