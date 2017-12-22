package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.api.GetBookModuleService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        GetBookModuleService getCommonService = init(CloudApiContext.getJDBooxBaseUrl());
        Call<BookModelResultBean> call = getCall(getCommonService);
        bookModelResultBean = done(call);
    }

    private BookModelResultBean done(Call<BookModelResultBean> call) {
        EnhancedCall<BookModelResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookModelResultBean.class);
    }

    private Call<BookModelResultBean> getCall(GetBookModuleService getCommonService) {
        return getCommonService.getBookShopModule(baseRequestBean.getAppBaseInfo().getRequestParamsMap(),
                CloudApiContext.BookShopModule.MODULE_CHILD_INFO, baseRequestBean.getBody());
    }

    private GetBookModuleService init(String URL) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(EnhancedCall.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GetBookModuleService.class);
    }
}