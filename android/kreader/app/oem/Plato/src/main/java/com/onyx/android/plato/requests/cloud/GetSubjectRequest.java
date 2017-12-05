package com.onyx.android.plato.requests.cloud;

import com.onyx.android.plato.cloud.bean.GetSubjectBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/11/4.
 */

public class GetSubjectRequest extends BaseCloudRequest {
    private GetSubjectBean subjects;

    public GetSubjectBean getSubjects() {
        return subjects;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
        Call<GetSubjectBean> call = getCall(service);
        Response<GetSubjectBean> response = call.execute();
        if (response.isSuccessful()) {
            subjects = response.body();
        }
    }

    private Call<GetSubjectBean> getCall(ContentService service) {
        Call<GetSubjectBean> call = service.getSubject();
        return call;
    }
}
