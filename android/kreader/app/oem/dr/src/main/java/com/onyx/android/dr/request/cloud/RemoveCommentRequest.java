package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by li on 2017/9/20.
 */

public class RemoveCommentRequest extends BaseCloudRequest {
    private String id;
    private String commentId;
    private CreateBookReportResult result;

    public RemoveCommentRequest(String id, String commentId) {
        this.id = id;
        this.commentId = commentId;
    }

    public CreateBookReportResult getResult() {
        return result;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<CreateBookReportResult> response = executeCall(ServiceFactory.getContentService(parent.
                getCloudConf().getApiBase()).removeComment(id, commentId));

        if(response != null) {
            result = response.body();
        }
    }
}
