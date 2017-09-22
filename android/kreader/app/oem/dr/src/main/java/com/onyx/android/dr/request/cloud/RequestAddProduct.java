package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.ProductCart;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.ProductRequestBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by hehai on 17-9-7.
 */

public class RequestAddProduct extends AutoNetWorkConnectionBaseCloudRequest {
    private ProductRequestBean product;
    private ProductCart<CloudMetadata> cart;

    public RequestAddProduct(ProductRequestBean product) {
        this.product = product;
    }

    public ProductCart<CloudMetadata> getCart() {
        return cart;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        createOrders(parent);
    }

    private void createOrders(CloudManager parent) {
        try {
            Response<ProductCart<CloudMetadata>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase()).addProduct(product));
            if (response != null) {
                cart = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
