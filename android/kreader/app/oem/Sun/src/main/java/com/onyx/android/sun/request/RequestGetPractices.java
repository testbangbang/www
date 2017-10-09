package com.onyx.android.sun.request;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sun.cloud.bean.PracticesRequestBean;
import com.onyx.android.sun.cloud.bean.PracticesResultBean;
import com.onyx.android.sun.cloud.service.GetPracticesService;
import com.onyx.android.sun.common.CloudApiContext;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hehai on 17-10-9.
 */

public class RequestGetPractices extends BaseCloudRequest {
    private PracticesRequestBean requestBean;
    private PracticesResultBean resultBean;

    public void setRequestBean(PracticesRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public PracticesResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        executeCloudRequest();
    }

    private void executeCloudRequest() {
        GetPracticesService service = init(CloudApiContext.SUN_BASE_URL);
        Call<PracticesResultBean> call = getCall(service);
        try {
            Response<PracticesResultBean> response = call.execute();
            resultBean = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Call<PracticesResultBean> getCall(GetPracticesService getPracticesService) {
        return getPracticesService.getPractice(requestBean.status, requestBean.studentId,
                requestBean.page, requestBean.size,
                requestBean.course, requestBean.type,
                requestBean.starttime, requestBean.endtime);
    }

    private GetPracticesService init(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GetPracticesService.class);
    }
}
