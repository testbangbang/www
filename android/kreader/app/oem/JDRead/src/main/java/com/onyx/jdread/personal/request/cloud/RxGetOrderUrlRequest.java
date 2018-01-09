package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.CloudApiContext;
import com.onyx.jdread.main.servie.ReadContentService;
import com.onyx.jdread.personal.cloud.entity.GetOrderRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetOrderUrlResultBean;

import retrofit2.Call;
import retrofit2.Response;

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
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BASE_URL);
        Call<GetOrderUrlResultBean> call = getCall(service);
        Response<GetOrderUrlResultBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }
        return this;
    }

    private Call<GetOrderUrlResultBean> getCall(ReadContentService service) {
        return service.getOrderUrl(CloudApiContext.NewBookDetail.GET_TOKEN, requestBean.getBody(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
