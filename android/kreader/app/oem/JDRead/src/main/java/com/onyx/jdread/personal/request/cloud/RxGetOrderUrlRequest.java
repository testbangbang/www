package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.api.GetOrderUrlService;
import com.onyx.jdread.personal.cloud.entity.GetOrderRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetOrderUrlResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by li on 2017/12/30.
 */

public class RxGetOrderUrlRequest extends RxBaseCloudRequest {
    private GetOrderUrlResultBean resultBean;
    private GetOrderRequestBean requestBean;

    public void setRequestBean(GetOrderRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public GetOrderUrlResultBean getOrderUrlResultBean() {
        return resultBean;
    }

    @Override
    public Object call() throws Exception {
        GetOrderUrlService service = init(CloudApiContext.JD_BASE_URL);
        Call<GetOrderUrlResultBean> call = getCall(service);
        Response<GetOrderUrlResultBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }
        return this;
    }

    private Call<GetOrderUrlResultBean> getCall(GetOrderUrlService service) {
        return service.getOrderUrl(CloudApiContext.NewBookDetail.GET_TOKEN, requestBean.getBody(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }

    private GetOrderUrlService init(String url) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url)
                .client(CloudApiContext.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GetOrderUrlService.class);
    }
}
