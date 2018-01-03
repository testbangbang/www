package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.api.GetCategoryV2BooksService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryV2BooksResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hehai on 17-3-30.
 */

public class RxRequestCategoryV2Books extends RxBaseCloudRequest {
    private BaseRequestBean baseRequestBean;
    private CategoryV2BooksResultBean categoryV2BooksResultBean;

    public CategoryV2BooksResultBean getCategoryV2BooksResultBean() {
        return categoryV2BooksResultBean;
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
        GetCategoryV2BooksService getCommonService = init(CloudApiContext.getJDBooxBaseUrl());
        Call<CategoryV2BooksResultBean> call = getCall(getCommonService);
        categoryV2BooksResultBean = done(call);
        checkQuestResult();
    }

    private CategoryV2BooksResultBean done(Call<CategoryV2BooksResultBean> call) {
        EnhancedCall<CategoryV2BooksResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, CategoryV2BooksResultBean.class);
    }

    private void checkQuestResult() {
        if (categoryV2BooksResultBean != null && categoryV2BooksResultBean.bookList != null) {
        }
    }

    private Call<CategoryV2BooksResultBean> getCall(GetCategoryV2BooksService getCommonService) {
        return getCommonService.getCategoryBookListV2(baseRequestBean.getAppBaseInfo().getRequestParamsMap(),
                CloudApiContext.CategoryBookListV2.CATEGORY_BOOK_LIST_V2,
                baseRequestBean.getBody());
    }

    private GetCategoryV2BooksService init(String URL) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(EnhancedCall.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GetCategoryV2BooksService.class);
    }
}
