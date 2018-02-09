package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.GetBookDetailRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import retrofit2.Call;

/**
 * Created by huxiaomao on 2016/12/19.
 */

public class RxRequestBookDetail extends RxBaseCloudRequest {
    private GetBookDetailRequestBean requestBean;
    private BookDetailResultBean bookDetailResultBean;

    public BookDetailResultBean getBookDetailResultBean() {
        return bookDetailResultBean;
    }

    public void setRequestBean(GetBookDetailRequestBean requestBean) {
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
        Call<BookDetailResultBean> call = getCall(getCommonService);
        bookDetailResultBean = done(call);
        checkResult();
    }

    private void checkResult() {
        if (bookDetailResultBean != null && bookDetailResultBean.result_code != 0) {
            ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(bookDetailResultBean.message));
        }
    }

    private BookDetailResultBean done(Call<BookDetailResultBean> call) {
        EnhancedCall<BookDetailResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookDetailResultBean.class);
    }

    private Call<BookDetailResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getBookDetail(requestBean.bookId, requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
