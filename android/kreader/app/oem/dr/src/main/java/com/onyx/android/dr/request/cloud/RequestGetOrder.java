package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.ProductOrder;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by hehai on 17-9-7.
 */

public class RequestGetOrder extends BaseCloudRequest {
    private String orderId;
    private ProductOrder<CloudMetadata> order;

    public RequestGetOrder(String orderId) {
        this.orderId = orderId;
    }

    public ProductOrder<CloudMetadata> getOrder() {
        return order;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        createOrders(parent);
    }

    private void createOrders(CloudManager parent) {
        try {
            Response<ProductOrder<CloudMetadata>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase()).getOrder(orderId));
            if (response != null) {
                order = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
