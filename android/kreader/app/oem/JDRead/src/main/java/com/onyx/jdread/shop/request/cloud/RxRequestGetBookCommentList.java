package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.shop.cloud.api.GetBookCommentListService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BookCommentsRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCommentsResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        GetBookCommentListService service = init(CloudApiContext.getJdBaseUrl());
        Call<BookCommentsResultBean> call = getCall(service);
        bookCommentsResultBean = done(call);
        checkQuestResult();
    }

    private BookCommentsResultBean done(Call<BookCommentsResultBean> call) {
        EnhancedCall<BookCommentsResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookCommentsResultBean.class);
    }

    private void checkQuestResult() {
        if (bookCommentsResultBean != null && !StringUtils.isNullOrEmpty(bookCommentsResultBean.getCode())) {
            switch (bookCommentsResultBean.getCode()) {

            }
        }
    }

    private Call<BookCommentsResultBean> getCall(GetBookCommentListService service) {
        return service.getBookCommentsList(CloudApiContext.NewBookDetail.NEW_BOOK_REVIEW,
                bookCommentsRequestBean.getBody(),
                bookCommentsRequestBean.getAppBaseInfo().getRequestParamsMap());
    }

    private GetBookCommentListService init(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(EnhancedCall.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GetBookCommentListService.class);
    }
}
