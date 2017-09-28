package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.GetBookReportListRequestBean;
import com.onyx.android.sdk.data.model.v2.GetSharedImpressionResult;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by li on 2017/9/28.
 */

public class GetSharedImpressionRequest extends AutoNetWorkConnectionBaseCloudRequest {
    private String libraryId;
    private GetBookReportListRequestBean requestBean;
    private GetSharedImpressionResult result;

    public GetSharedImpressionRequest(String libraryId, GetBookReportListRequestBean requstBean) {
        this.requestBean = requstBean;
        this.libraryId = libraryId;
    }

    public GetSharedImpressionResult getResult() {
        return result;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<GetSharedImpressionResult> response = executeCall(ServiceFactory.getContentService(parent.
                getCloudConf().getApiBase()).getSharedImpression(libraryId, requestBean.offset,
                requestBean.limit, requestBean.sortBy, requestBean.order));

        if (response != null) {
            result = response.body();
        }
    }
}
