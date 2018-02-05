package com.onyx.jdread.personal.request.cloud;


import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.entity.jdbean.SaltResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/9.
 */

public class RxGetSaltRequest extends RxBaseCloudRequest {
    private JDAppBaseInfo requestBean;
    private SaltResultBean saltResultBean;

    public RxGetSaltRequest(JDAppBaseInfo requestBean) {
        this.requestBean = requestBean;
    }

    public SaltResultBean getSaltResultBean() {
        return saltResultBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getServiceForNoLogin(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<SaltResultBean> call = getCall(service);
        Response<SaltResultBean> response = call.execute();
        if (response.isSuccessful()) {
            saltResultBean = response.body();
        }
        return this;
    }

    private Call<SaltResultBean> getCall(ReadContentService service) {
        return service.getSalt(requestBean.getRequestParamsMap());
    }
}
