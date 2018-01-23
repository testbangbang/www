package com.onyx.jdread.personal.request.cloud;


import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.entity.BaseShopRequestBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/22.
 */

public class RxSetReadPreferenceRequest extends RxBaseCloudRequest {
    private BaseShopRequestBean requestBean;

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getServiceForString(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<String> call = getCall(service);
        Response<String> response = call.execute();
        if (response.isSuccessful()) {
            String body = response.body();
        }
        return this;
    }

    private Call<String> getCall(ReadContentService service) {
        return service.setReadPreference(requestBean.getBaseInfo().getRequestParamsMap(),
                requestBean.getBody());
    }

    public void setRequestBean(BaseShopRequestBean requestBean) {
        this.requestBean = requestBean;
    }
}
