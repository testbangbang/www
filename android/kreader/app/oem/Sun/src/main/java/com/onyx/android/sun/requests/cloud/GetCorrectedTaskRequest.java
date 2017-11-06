package com.onyx.android.sun.requests.cloud;

import com.onyx.android.sun.cloud.bean.GetCorrectedTaskRequestBean;
import com.onyx.android.sun.cloud.bean.GetCorrectedTaskResultBean;
import com.onyx.android.sun.cloud.service.ContentService;
import com.onyx.android.sun.common.CloudApiContext;
import com.onyx.android.sun.requests.requestTool.BaseCloudRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/11/3.
 */

public class GetCorrectedTaskRequest extends BaseCloudRequest {
    private GetCorrectedTaskRequestBean requestBean;
    private GetCorrectedTaskResultBean taskBean;

    public GetCorrectedTaskRequest(GetCorrectedTaskRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public GetCorrectedTaskResultBean getTaskBean() {
        return taskBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
        Call<GetCorrectedTaskResultBean> call = getCall(service);
        Response<GetCorrectedTaskResultBean> response = call.execute();
        if (response.isSuccessful()) {
            taskBean = response.body();
        }
    }

    private Call<GetCorrectedTaskResultBean> getCall(ContentService service) {
        Call<GetCorrectedTaskResultBean> call = service.getCorrectedTask(requestBean.practiceId, requestBean.studentId);
        return call;
    }
}
