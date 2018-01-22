package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.shop.cloud.entity.UpdateCartRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.UpdateCartBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/20.
 */

public class RxRequestUpdateCart extends RxBaseCloudRequest {
    private UpdateCartRequestBean updateCartRequestBean;
    private UpdateCartBean resultBean;

    public UpdateCartBean getResultBean() {
        return resultBean;
    }

    public void setUpdateCartRequestBean(UpdateCartRequestBean updateCartRequestBean) {
        this.updateCartRequestBean = updateCartRequestBean;
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
        return service.updateCart(updateCartRequestBean.getBaseInfo().getRequestParamsMap(),
                updateCartRequestBean.getBody());
    }
}
