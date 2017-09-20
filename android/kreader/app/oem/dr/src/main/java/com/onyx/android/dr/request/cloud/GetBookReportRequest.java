package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by li on 2017/9/20.
 */

public class GetBookReportRequest extends BaseCloudRequest {
    private String id;
    private CreateBookReportResult result;

    public GetBookReportRequest(String id) {
        this.id = id;
    }

    public CreateBookReportResult getResult() {
        return result;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        try {
            Response<CreateBookReportResult> response = executeCall(ServiceFactory.getContentService(parent
                    .getCloudConf().getApiBase()).getImpression(id));

            if(response != null) {
                result = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
