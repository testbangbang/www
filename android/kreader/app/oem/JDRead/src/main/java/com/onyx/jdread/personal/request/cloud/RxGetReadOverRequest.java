package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.api.GetReadOverService;
import com.onyx.jdread.personal.cloud.entity.GetReadInfoRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadOverInfoBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by li on 2018/1/2.
 */

public class RxGetReadOverRequest extends RxBaseCloudRequest {
    private GetReadInfoRequestBean requestBean;
    private ReadOverInfoBean readOverInfoBean;

    public void setRequestBean(GetReadInfoRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public ReadOverInfoBean getReadOverInfoBean() {
        return readOverInfoBean;
    }

    @Override
    public Object call() throws Exception {
        GetReadOverService service = init(CloudApiContext.JD_BOOK_STATISTIC_URL);
        Call<ReadOverInfoBean> call = getCall(service);
        Response<ReadOverInfoBean> response = call.execute();
        if (response.isSuccessful()) {
            readOverInfoBean = response.body();
        }
        return this;
    }

    private Call<ReadOverInfoBean> getCall(GetReadOverService service) {
        return service.getReadOverBook(requestBean.getUserName(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }

    private GetReadOverService init(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(CloudApiContext.getClient())
                .build();
        return retrofit.create(GetReadOverService.class);
    }
}
