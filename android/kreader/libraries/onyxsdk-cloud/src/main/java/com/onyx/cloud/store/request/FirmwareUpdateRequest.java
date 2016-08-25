package com.onyx.cloud.store.request;

import com.alibaba.fastjson.JSON;
import com.onyx.cloud.CloudManager;
import com.onyx.cloud.model.Firmware;
import com.onyx.cloud.service.OnyxOTAService;
import com.onyx.cloud.service.ServiceFactory;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by zhuzeng on 11/21/15.
 */
public class FirmwareUpdateRequest extends BaseCloudRequest {
    private Firmware fw;
    private Firmware resultFirmware;

    public FirmwareUpdateRequest(final Firmware current) {
        fw = current;
    }

    public final Firmware getResultFirmware() {
        return resultFirmware;
    }

    @Override
    public void execute(final CloudManager parent) throws Exception {
        Call<Firmware> call = ServiceFactory.getOTAService(parent.getCloudConf().getApiBase())
                .firmwareUpdate(JSON.toJSONString(fw));
        Response<Firmware> response = call.execute();
        if (response.isSuccessful()) {
            resultFirmware = response.body();
        }
    }

}
