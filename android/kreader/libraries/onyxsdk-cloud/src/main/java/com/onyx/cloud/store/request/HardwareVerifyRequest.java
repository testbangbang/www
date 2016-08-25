package com.onyx.cloud.store.request;

import com.alibaba.fastjson.JSON;
import com.onyx.cloud.CloudManager;
import com.onyx.cloud.model.Prohibit;
import com.onyx.cloud.service.OnyxHardwareService;
import com.onyx.cloud.service.ServiceFactory;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by zhuzeng on 11/21/15.
 */
public class HardwareVerifyRequest extends BaseCloudRequest {
    private Prohibit prohibit;

    public HardwareVerifyRequest(final Prohibit hw) {
        prohibit = hw;
    }

    @Override
    public void execute(final CloudManager parent) throws Exception {
        Call call = ServiceFactory.getHardwareService(parent.getCloudConf().getApiBase())
                .hardwareVerify(JSON.toJSONString(prohibit));
        Response response = call.execute();
        if (response.isSuccessful()) {
            prohibit.shouldStop = 0;
        }
    }

    public final Prohibit getProhibit() {
        return prohibit;
    }
}
