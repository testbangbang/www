package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BookModelRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class RxRequestBookModule extends RxBaseCloudRequest {

    private BookModelRequestBean requestBean;
    private BookModelBooksResultBean bookModelResultBean;

    public BookModelBooksResultBean getBookModelResultBean() {
        return bookModelResultBean;
    }

    public void setRequestBean(BookModelRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        Call<BookModelBooksResultBean> call = getCall(getCommonService);
        bookModelResultBean = done(call);
    }

    private BookModelBooksResultBean done(Call<BookModelBooksResultBean> call) {
        EnhancedCall<BookModelBooksResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookModelBooksResultBean.class);
    }

    private Call<BookModelBooksResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getBookShopModule(requestBean.getfType(), requestBean.getModuleId(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
