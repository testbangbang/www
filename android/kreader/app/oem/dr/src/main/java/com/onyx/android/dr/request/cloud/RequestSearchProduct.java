package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by hehai on 17-9-7.
 */

public class RequestSearchProduct extends AutoNetWorkConnectionBaseCloudRequest {
    private QueryResult<CloudMetadata> result;
    private String text;

    public RequestSearchProduct(String text) {
        this.text = text;
    }

    public QueryResult<CloudMetadata> getResult() {
        return result;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        createOrders(parent);
    }

    private void createOrders(CloudManager parent) {
        try {
            Response<QueryResult<CloudMetadata>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase()).search(text));
            if (response != null) {
                result = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
