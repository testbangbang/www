package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.api.GetBookDetailService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huxiaomao on 2016/12/19.
 */

public class RxRequestBookDetail extends RxBaseCloudRequest {
    private static final String TAG = RxRequestBookDetail.class.getSimpleName();
    private BaseRequestBean baseRequestBean;
    private BookDetailResultBean bookDetailResultBean;

    public BookDetailResultBean getBookDetailResultBean() {
        return bookDetailResultBean;
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
        GetBookDetailService getCommonService = init(CloudApiContext.getJdBaseUrl());
        Call<BookDetailResultBean> call = getCall(getCommonService);
        bookDetailResultBean = done(call);
    }

    private BookDetailResultBean done(Call<BookDetailResultBean> call) {
        EnhancedCall<BookDetailResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookDetailResultBean.class);
    }

    private Call<BookDetailResultBean> getCall(GetBookDetailService getCommonService) {
        return getCommonService.getBookDetail(baseRequestBean.getAppBaseInfo().getRequestParamsMap(),
                CloudApiContext.NewBookDetail.API_NEW_BOOK_DETAIL,
                baseRequestBean.getBody()
        );
    }

    private GetBookDetailService init(String URL) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(EnhancedCall.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GetBookDetailService.class);
    }
}