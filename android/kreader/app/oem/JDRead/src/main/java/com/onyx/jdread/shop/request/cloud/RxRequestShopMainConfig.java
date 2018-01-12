package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.ShopMainConfigRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookShopMainConfigResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class RxRequestShopMainConfig extends RxBaseCloudRequest {

    private ShopMainConfigRequestBean requestBean;
    private BookShopMainConfigResultBean resultBean;

    public BookShopMainConfigResultBean getResultBean() {
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
        ReadContentService getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        Call<BookShopMainConfigResultBean> call = getCall(getCommonService);
        resultBean = done(call);
    }

    private BookShopMainConfigResultBean done(Call<BookShopMainConfigResultBean> call) {
        EnhancedCall<BookShopMainConfigResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookShopMainConfigResultBean.class);
    }

    private Call<BookShopMainConfigResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getShopMainConfig(requestBean.getCid(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
