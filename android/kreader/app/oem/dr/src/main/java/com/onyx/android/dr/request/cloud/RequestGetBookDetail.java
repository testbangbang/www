package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by hehai on 17-7-31.
 */

public class RequestGetBookDetail extends BaseCloudRequest {
    private String idString;
    private CloudMetadata cloudMetadata;

    public RequestGetBookDetail(String idString) {
        this.idString = idString;
    }

    public CloudMetadata getCloudMetadata() {
        return cloudMetadata;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getMyGroup(parent);
    }

    private void getMyGroup(CloudManager parent) {
        try {
            Response<CloudMetadata> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                    .loadBook(idString));
            if (response != null) {
                cloudMetadata = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
