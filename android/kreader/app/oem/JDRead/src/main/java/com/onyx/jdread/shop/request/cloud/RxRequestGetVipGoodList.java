package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.GetVipGoodListRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetVipGoodsListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class RxRequestGetVipGoodList extends RxBaseCloudRequest {

    private GetVipGoodListRequestBean requestBean;
    private GetVipGoodsListResultBean resultBean;

    public GetVipGoodsListResultBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(GetVipGoodListRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService;
        if (requestBean.withCookie) {
            getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        } else {
            getCommonService = CloudApiContext.getServiceNoCookie(CloudApiContext.getJDBooxBaseUrl());
        }
        Call<GetVipGoodsListResultBean> call = getCall(getCommonService);
        resultBean = done(call);
        checkRequestResult();
    }

    private void checkRequestResult() {
        if (resultBean != null) {
        }
    }

    private GetVipGoodsListResultBean done(Call<GetVipGoodsListResultBean> call) {
        EnhancedCall<GetVipGoodsListResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, GetVipGoodsListResultBean.class);
    }

    private Call<GetVipGoodsListResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getVipGoodList(requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
