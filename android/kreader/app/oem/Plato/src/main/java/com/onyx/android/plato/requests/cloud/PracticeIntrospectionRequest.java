package com.onyx.android.plato.requests.cloud;

import com.onyx.android.plato.cloud.bean.IntrospectionRequestBean;
import com.onyx.android.plato.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/11/4.
 */

public class PracticeIntrospectionRequest extends BaseCloudRequest {
    private IntrospectionRequestBean requestBean;
    private SubmitPracticeResultBean resultBean;

    public PracticeIntrospectionRequest(IntrospectionRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public SubmitPracticeResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
        Call<SubmitPracticeResultBean> call = getCall(service);
        Response<SubmitPracticeResultBean> response = call.execute();
        if(response.isSuccessful()) {
            resultBean = response.body();
        }
    }

    private Call<SubmitPracticeResultBean> getCall(ContentService service) {
        Call<SubmitPracticeResultBean> call = service.practiceIntrospection(requestBean.id, requestBean.requestBody);
        return call;
    }
}
