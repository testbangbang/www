package com.onyx.android.plato.requests.cloud;

import com.onyx.android.plato.cloud.bean.PracticeParseRequestBean;
import com.onyx.android.plato.cloud.bean.PracticeParseResultBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/11/3.
 */

public class GetPracticeParseRequest extends BaseCloudRequest {
    private PracticeParseRequestBean requestBean;
    private PracticeParseResultBean resultBean;

    public GetPracticeParseRequest(PracticeParseRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public PracticeParseResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
        Call<PracticeParseResultBean> call = getCall(service);
        Response<PracticeParseResultBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }
    }

    private Call<PracticeParseResultBean> getCall(ContentService service) {
        Call<PracticeParseResultBean> call = service.getPracticeParse(requestBean.id, requestBean.pid, requestBean.studentId);
        return call;
    }
}
