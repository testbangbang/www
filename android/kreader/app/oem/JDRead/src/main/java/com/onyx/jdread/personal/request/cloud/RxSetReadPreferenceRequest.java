package com.onyx.jdread.personal.request.cloud;


import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.entity.jdbean.SetReadPreferenceBean;
import com.onyx.jdread.shop.cloud.entity.BaseShopRequestBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/22.
 */

public class RxSetReadPreferenceRequest extends RxBaseCloudRequest {
    private BaseShopRequestBean requestBean;
    private SetReadPreferenceBean resultBean;

    public SetReadPreferenceBean getResultBean() {
        return resultBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<SetReadPreferenceBean> call = getCall(service);
        Response<SetReadPreferenceBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }
        return this;
    }

    private Call<SetReadPreferenceBean> getCall(ReadContentService service) {
        return service.setReadPreference(requestBean.getBaseInfo().getRequestParamsMap(),
                requestBean.getBody());
    }

    public void setRequestBean(BaseShopRequestBean requestBean) {
        this.requestBean = requestBean;
    }
}
