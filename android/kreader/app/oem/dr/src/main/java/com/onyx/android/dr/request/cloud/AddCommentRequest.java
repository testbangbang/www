package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.AddCommentRequestBean;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by li on 2017/9/20.
 */

public class AddCommentRequest extends BaseCloudRequest {
    private String id;
    private AddCommentRequestBean requestBean;
    private CreateBookReportResult result;

    public AddCommentRequest(String id, AddCommentRequestBean requestBean) {
        this.id = id;
        this.requestBean = requestBean;
    }

    public CreateBookReportResult getResult() {
        return result;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<CreateBookReportResult> response = executeCall(ServiceFactory.getContentService(parent.
                getCloudConf().getApiBase()).addComment(id, requestBean));

        if(response != null) {
            result = response.body();
        }
    }
}
