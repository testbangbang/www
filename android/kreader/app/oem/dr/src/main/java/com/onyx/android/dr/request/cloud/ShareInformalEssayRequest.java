package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.ShareBookReportRequestBean;
import com.onyx.android.sdk.data.model.v2.ShareBookReportResult;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by li on 2017/9/27.
 */

public class ShareInformalEssayRequest extends AutoNetWorkConnectionBaseCloudRequest {
    private ShareBookReportRequestBean requestBean;
    private String id;
    private ShareBookReportResult result;

    public ShareInformalEssayRequest(String libraryId, ShareBookReportRequestBean bean) {
        this.id = libraryId;
        this.requestBean = bean;

    }
    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<ShareBookReportResult> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .shareInformalEssay(id, requestBean));
        if(response != null) {
            result = response.body();
        }
    }

    public ShareBookReportResult getResult() {
        return result;
    }
}
