package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.api.GetReadTotalService;
import com.onyx.jdread.personal.cloud.entity.GetReadInfoRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadTotalInfoBean;
import com.onyx.jdread.personal.common.CloudApiContext;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by li on 2017/12/29.
 */

public class RxGetReadTotalRequest extends RxBaseCloudRequest {
    private GetReadInfoRequestBean requestBean;
    private ReadTotalInfoBean readTotalInfoBean;

    public void setRequestBean(GetReadInfoRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public ReadTotalInfoBean getReadTotalInfoBean() {
        return readTotalInfoBean;
    }

    @Override
    public Object call() throws Exception {
        GetReadTotalService service = init(CloudApiContext.getJdBaseUrl());
        Call<ReadTotalInfoBean> call = getCall(service);
        Response<ReadTotalInfoBean> response = call.execute();
        if (response.isSuccessful()) {
            readTotalInfoBean = response.body();
        }
        return this;
    }

    private Call<ReadTotalInfoBean> getCall(GetReadTotalService service) {
        return service.getReadTotalBook(CloudApiContext.NewBookDetail.READ_TOTAL_BOOK,
                requestBean.getUserName(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }

    private GetReadTotalService init(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(CloudApiContext.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GetReadTotalService.class);
    }
}
