package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelResultBean;

import retrofit2.Call;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class RxRequestBookModule extends RxBaseCloudRequest {

    private BaseRequestBean baseRequestBean;
    private BookModelResultBean bookModelResultBean;

    public BookModelResultBean getBookModelResultBean() {
        return bookModelResultBean;
    }

    public void setBaseRequestBean(BaseRequestBean baseRequestBean) {
        this.baseRequestBean = baseRequestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        Call<BookModelResultBean> call = getCall(getCommonService);
        bookModelResultBean = done(call);
    }

    private BookModelResultBean done(Call<BookModelResultBean> call) {
        EnhancedCall<BookModelResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookModelResultBean.class);
    }

    private Call<BookModelResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getBookShopModule(baseRequestBean.getAppBaseInfo().getRequestParamsMap(),
                CloudApiContext.BookShopModule.MODULE_CHILD_INFO, baseRequestBean.getBody());
    }
}
