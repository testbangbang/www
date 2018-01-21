package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.ShopMainConfigRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class RxRequestShopMainConfig extends RxBaseCloudRequest {

    private ShopMainConfigRequestBean requestBean;
    private BookModelConfigResultBean resultBean;

    public BookModelConfigResultBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(ShopMainConfigRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getServiceNoCookie(CloudApiContext.getJDBooxBaseUrl());
        Call<BookModelConfigResultBean> call = getCall(getCommonService);
        resultBean = done(call);
    }

    private BookModelConfigResultBean done(Call<BookModelConfigResultBean> call) {
        EnhancedCall<BookModelConfigResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookModelConfigResultBean.class);
    }

    private Call<BookModelConfigResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getShopMainConfig(requestBean.getCid(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
