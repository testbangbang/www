package com.onyx.android.sun.requests.cloud;

import com.onyx.android.sun.cloud.bean.GetSubjectBean;
import com.onyx.android.sun.cloud.service.ContentService;
import com.onyx.android.sun.common.CloudApiContext;
import com.onyx.android.sun.requests.requestTool.BaseCloudRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/11/4.
 */

public class GetExerciseTypeRequest extends BaseCloudRequest {
    private GetSubjectBean exerciseTypes;

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
        Call<GetSubjectBean> call = getCall(service);
        Response<GetSubjectBean> response = call.execute();
        if(response.isSuccessful()) {
            exerciseTypes = response.body();
        }
    }

    private Call<GetSubjectBean> getCall(ContentService service) {
        Call<GetSubjectBean> call = service.getExerciseType();
        return call;
    }

    public GetSubjectBean getExerciseTypes() {
        return exerciseTypes;
    }
}
