package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.ReadingRateBean;
import com.onyx.android.sdk.data.model.v2.UploadReadingRateBean;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/10/9.
 */
public class CreateReadingRateRequest extends AutoNetWorkConnectionBaseCloudRequest {
    private final ReadingRateBean param;
    private UploadReadingRateBean bean = new UploadReadingRateBean();

    public CreateReadingRateRequest(ReadingRateBean param) {
        this.param = param;
    }

    public UploadReadingRateBean getGroup() {
        return bean;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getMyGroup(parent);
    }

    private void getMyGroup(CloudManager parent) {
        try {
            Response<UploadReadingRateBean> response = executeCall(ServiceFactory.getContentService(
                    parent.getCloudConf().getApiBase()).uploadReadingRate(param));
            if (response != null) {
                bean = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
