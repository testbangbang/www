package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.entity.BaseShopRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.UpdateCartBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/20.
 */

public class RxRequestUpdateCart extends RxBaseCloudRequest {
    private BaseShopRequestBean baseShopRequestBean;
    private UpdateCartBean resultBean;

    public UpdateCartBean getResultBean() {
        return resultBean;
    }

    public void setBaseShopRequestBean(BaseShopRequestBean baseShopRequestBean) {
        this.baseShopRequestBean = baseShopRequestBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<UpdateCartBean> call = getCall(service);
        Response<UpdateCartBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
            checkQuestResult();
        }
        return this;
    }

    private void checkQuestResult() {

    }

    private Call<UpdateCartBean> getCall(ReadContentService service) {
        return service.updateCart(baseShopRequestBean.getBaseInfo().getRequestParamsMap(),
                baseShopRequestBean.getBody());
    }
}
