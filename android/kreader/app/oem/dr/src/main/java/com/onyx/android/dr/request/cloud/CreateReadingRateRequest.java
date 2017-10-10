package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.CreateReadingRateBean;
import com.onyx.android.sdk.data.model.ReadingRateBean;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/10/9.
 */
public class CreateReadingRateRequest extends AutoNetWorkConnectionBaseCloudRequest {
    private final ReadingRateBean param;
    private CreateReadingRateBean bean = new CreateReadingRateBean();

    public CreateReadingRateRequest(ReadingRateBean param) {
        this.param = param;
    }

    public CreateReadingRateBean getGroup() {
        return bean;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getMyGroup(parent);
    }

    private void getMyGroup(CloudManager parent) {
        try {
            Response<CreateReadingRateBean> response = executeCall(ServiceFactory.getContentService(
                    parent.getCloudConf().getApiBase()).uploadReadingRate(param));
            if (response != null) {
                bean = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
