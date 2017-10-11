package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.data.model.InformalEssayBean;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/10/9.
 */
public class CreateInformalEssayRequest extends AutoNetWorkConnectionBaseCloudRequest {
    private final InformalEssayBean param;
    private CreateInformalEssayBean bean = new CreateInformalEssayBean();

    public CreateInformalEssayRequest(InformalEssayBean param) {
        this.param = param;
    }

    public CreateInformalEssayBean getGroup() {
        return bean;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getMyGroup(parent);
    }

    private void getMyGroup(CloudManager parent) {
        try {
            Response<CreateInformalEssayBean> response = executeCall(ServiceFactory.getContentService(
                    parent.getCloudConf().getApiBase()).uploadInformalEssay(param));
            if (response != null) {
                bean = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
