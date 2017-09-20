package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.GetBookReportList;
import com.onyx.android.sdk.data.model.v2.GetBookReportListRequestBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by li on 2017/9/19.
 */

public class GetBookReportListRequest extends BaseCloudRequest {
    private GetBookReportListRequestBean requestBean;
    private GetBookReportList bookReportList;

    public GetBookReportListRequest(GetBookReportListRequestBean requstBean) {
        this.requestBean = requstBean;
    }

    public GetBookReportList getBookReportList() {
        return bookReportList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        try {
            Response<GetBookReportList> response = executeCall(ServiceFactory.getContentService(parent.
                    getCloudConf().getApiBase()).getImpressionsList(requestBean.offset,
                    requestBean.limit,requestBean.sortBy,requestBean.order));

            if(response != null) {
                bookReportList = response.body();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
