package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.ProductCart;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by hehai on 17-9-7.
 */

public class RequestGetProducts extends BaseCloudRequest {
    private QueryResult<ProductCart<CloudMetadata>> carts;

    public RequestGetProducts() {
    }

    public QueryResult<ProductCart<CloudMetadata>> getCarts() {
        return carts;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        createOrders(parent);
    }

    private void createOrders(CloudManager parent) {
        try {
            Response<QueryResult<ProductCart<CloudMetadata>>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase()).getCartProducts());
            if (response != null) {
                carts = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
