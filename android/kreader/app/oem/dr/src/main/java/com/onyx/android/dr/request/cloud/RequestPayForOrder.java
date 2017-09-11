package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.PayBean;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by hehai on 17-9-7.
 */

public class RequestPayForOrder extends AutoNetWorkConnectionBaseCloudRequest {
    private String id;
    private PayBean payBean;

    public RequestPayForOrder(String id) {
        this.id = id;
    }

    public PayBean getPayBean() {
        return payBean;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        createOrders(parent);
    }

    private void createOrders(CloudManager parent) {
        try {
            Response<PayBean> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase()).pay(id));
            if (response != null) {
                payBean = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
