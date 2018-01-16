package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BookCommentsRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCommentsResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;

/**
 * Created by jackdeng on 2017/12/18.
 */

public class RxRequestGetBookCommentList extends RxBaseCloudRequest {

    private BookCommentsRequestBean bookCommentsRequestBean;
    private BookCommentsResultBean bookCommentsResultBean;

    public void setBookCommentsRequestBean(BookCommentsRequestBean bookCommentsRequestBean) {
        this.bookCommentsRequestBean = bookCommentsRequestBean;
    }

    public BookCommentsResultBean getBookCommentsResultBean() {
        return bookCommentsResultBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        Call<BookCommentsResultBean> call = getCall(service);
        bookCommentsResultBean = done(call);
        checkQuestResult();
    }

    private BookCommentsResultBean done(Call<BookCommentsResultBean> call) {
        EnhancedCall<BookCommentsResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookCommentsResultBean.class);
    }

    private void checkQuestResult() {
        if (bookCommentsResultBean != null && bookCommentsResultBean.data != null) {

        }
    }

    private Call<BookCommentsResultBean> getCall(ReadContentService service) {
        return service.getBookCommentsList(bookCommentsRequestBean.bookId, bookCommentsRequestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
