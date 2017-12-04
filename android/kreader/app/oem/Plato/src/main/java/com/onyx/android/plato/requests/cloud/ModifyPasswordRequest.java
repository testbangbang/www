package com.onyx.android.plato.requests.cloud;

import com.onyx.android.plato.cloud.bean.ModifyPasswordBean;
import com.onyx.android.plato.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/12/4.
 */

public class ModifyPasswordRequest extends BaseCloudRequest {
    private ModifyPasswordBean requestBean;
    private SubmitPracticeResultBean resultBean;
    private String errorBean;

    public ModifyPasswordRequest(ModifyPasswordBean requestBean) {
        this.requestBean = requestBean;
    }

    public SubmitPracticeResultBean getResultBean() {
        return resultBean;
    }

    public String getErrorBean() {
        return errorBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
        Call<SubmitPracticeResultBean> call = getCall(service);
        Response<SubmitPracticeResultBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }else {
            errorBean = response.errorBody().string();
        }
    }

    private Call<SubmitPracticeResultBean> getCall(ContentService service) {
        Call<SubmitPracticeResultBean> call = service.modifyPassword(requestBean);
        return call;
    }
}
