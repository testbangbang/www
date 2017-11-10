package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.data.model.v2.AddCommentRequestBean;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by li on 2017/9/20.
 */

public class AddInformalCommentRequest extends AutoNetWorkConnectionBaseCloudRequest {
    private String id;
    private AddCommentRequestBean requestBean;
    private CreateInformalEssayBean result;

    public AddInformalCommentRequest(String id, AddCommentRequestBean requestBean) {
        this.id = id;
        this.requestBean = requestBean;
    }

    public CreateInformalEssayBean getResult() {
        return result;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<CreateInformalEssayBean> response = executeCall(ServiceFactory.getContentService(parent.
                getCloudConf().getApiBase()).addInformalComment(id, requestBean));

        if(response != null) {
            result = response.body();
        }
    }
}
