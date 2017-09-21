package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by li on 2017/9/20.
 */

public class DeleteBookReportRequest extends BaseCloudRequest {
    private String id;
    private String result;

    public DeleteBookReportRequest(String id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        try {
            Response<String> response = executeCall(ServiceFactory.getContentService(parent.
                    getCloudConf().getApiBase()).deleteImpression(id));

            if(response != null) {
                result = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
