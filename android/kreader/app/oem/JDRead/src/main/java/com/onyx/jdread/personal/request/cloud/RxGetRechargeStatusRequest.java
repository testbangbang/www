package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetRechargeStatusBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/26.
 */

public class RxGetRechargeStatusRequest extends RxBaseCloudRequest {
    private JDAppBaseInfo baseInfo;
    private GetRechargeStatusBean rechargeStatusBean;

    public GetRechargeStatusBean getRechargeStatusBean() {
        return rechargeStatusBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<GetRechargeStatusBean> call = getCall(service);
        Response<GetRechargeStatusBean> response = call.execute();
        if (response.isSuccessful()) {
            rechargeStatusBean = response.body();
        }
        return this;
    }

    private Call<GetRechargeStatusBean> getCall(ReadContentService service) {
        return service.getRechargeStatus(baseInfo.getRequestParamsMap());
    }

    public void setBaseInfo(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}
