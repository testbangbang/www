package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.servie.ReadContentService;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ShoppingCartBookIdsBean;
import com.onyx.jdread.main.common.CloudApiContext;

import retrofit2.Call;
import retrofit2.Response;

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
        ReadContentService service = CloudApiContext.getService(CloudApiContext.getJdBaseUrl());
        Call<ShoppingCartBookIdsBean> call = getCall(service);
        Response<ShoppingCartBookIdsBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }
        return this;
    }

    private Call<ShoppingCartBookIdsBean> getCall(ReadContentService service) {
        return service.getCartBookIds(CloudApiContext.NewBookDetail.SHOPPING_CART,
                requestBean.getBody(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
