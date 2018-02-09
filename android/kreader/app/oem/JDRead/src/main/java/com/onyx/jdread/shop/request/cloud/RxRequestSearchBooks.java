package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.SearchBooksRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import retrofit2.Call;

/**
 * Created by hehai on 17-3-30.
 */

public class RxRequestSearchBooks extends RxBaseCloudRequest {
    private SearchBooksRequestBean requestBean;
    private BookModelBooksResultBean resultBean;

    public BookModelBooksResultBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(SearchBooksRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getServiceNoCookie(CloudApiContext.getJDBooxBaseUrl());
        Call<BookModelBooksResultBean> call = getCall(getCommonService);
        resultBean = done(call);
        checkRequestResult();
    }

    private BookModelBooksResultBean done(Call<BookModelBooksResultBean> call) {
        EnhancedCall<BookModelBooksResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookModelBooksResultBean.class);
    }

    private void checkRequestResult() {
        if (resultBean != null && resultBean.result_code != 0) {
            ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
        }
    }

    private Call<BookModelBooksResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getSearchBooks(requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
