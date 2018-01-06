package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.api.GetShoppingCartIdsService;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ShoppingCartBookIdsBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by li on 2018/1/5.
 */

public class RxRequestGetServiceCartIds extends RxBaseCloudRequest {
    private BaseRequestBean requestBean;
    private ShoppingCartBookIdsBean resultBean;

    public void setRequestBean(BaseRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public ShoppingCartBookIdsBean getResultBean() {
        return resultBean;
    }

    @Override
    public Object call() throws Exception {
        GetShoppingCartIdsService service = init(CloudApiContext.getJdBaseUrl());
        Call<ShoppingCartBookIdsBean> call = getCall(service);
        Response<ShoppingCartBookIdsBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }
        return this;
    }

    private Call<ShoppingCartBookIdsBean> getCall(GetShoppingCartIdsService service) {
        return service.getCartBookIds(CloudApiContext.NewBookDetail.SHOPPING_CART,
                requestBean.getBody(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }

    private GetShoppingCartIdsService init(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(CloudApiContext.getClient())
                .build();
        return retrofit.create(GetShoppingCartIdsService.class);
    }
}
