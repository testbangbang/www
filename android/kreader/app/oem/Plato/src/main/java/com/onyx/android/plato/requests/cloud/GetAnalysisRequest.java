package com.onyx.android.plato.requests.cloud;

import com.onyx.android.plato.cloud.bean.GetAnalysisBean;
import com.onyx.android.plato.cloud.bean.PracticeParseRequestBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/11/28.
 */

public class GetAnalysisRequest extends BaseCloudRequest {
    private PracticeParseRequestBean requestBean;
    private GetAnalysisBean resultBean;

    public GetAnalysisRequest(PracticeParseRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public GetAnalysisBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
        Call<GetAnalysisBean> call = getCall(service);
        Response<GetAnalysisBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }
    }

    private Call<GetAnalysisBean> getCall(ContentService service) {
        Call<GetAnalysisBean> call = service.getAnalysis(requestBean.id, requestBean.pid);
        return call;
    }
}
