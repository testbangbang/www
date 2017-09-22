package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.ProductOrder;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.ProductRequestBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by hehai on 17-9-7.
 */

public class RequestRemoveProduct extends AutoNetWorkConnectionBaseCloudRequest {
    private ProductRequestBean product;
    private ProductOrder<CloudMetadata> order;

    public RequestRemoveProduct(ProductRequestBean product) {
        this.product = product;
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
            Response<ProductOrder<CloudMetadata>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase()).removeProduct(product));
            if (response != null) {
                order = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
