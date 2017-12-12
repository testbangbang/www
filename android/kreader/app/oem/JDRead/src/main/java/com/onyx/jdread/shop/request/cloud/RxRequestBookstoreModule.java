package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.data.database.BookDetailEntity;
import com.onyx.jdread.shop.cloud.api.GetBookstoreModuleService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookstoreModuleResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class RxRequestBookstoreModule extends RxBaseCloudRequest {

    private BaseRequestBean baseRequestBean;
    private BookstoreModuleResultBean bookstoreModuleResultBean;
    private ArrayList<BookDetailEntity> books;

    public BookstoreModuleResultBean getBookstoreModuleResultBean() {
        return bookstoreModuleResultBean;
    }

    public List<BookDetailEntity> getBooks() {
        return books;
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
        GetBookstoreModuleService getCommonService = init(CloudApiContext.getJDBooxBaseUrl());
        Call<BookstoreModuleResultBean> call = getCall(getCommonService);
        bookstoreModuleResultBean = done(call);
        checkQuestResult();
    }

    private void checkQuestResult() {
        if (bookstoreModuleResultBean != null && bookstoreModuleResultBean.resultList != null) {
            books = new ArrayList<>();
            for (ResultBookBean resultListBean : bookstoreModuleResultBean.resultList) {
                BookDetailEntity entity = new BookDetailEntity();
                entity.bookName = resultListBean.name;
                entity.imageUrl = resultListBean.imageUrl;
                entity.newImageUrl = resultListBean.newImageUrl;
                entity.author = resultListBean.author;
                entity.bookId = resultListBean.ebookId;
                entity.info = resultListBean.info;
                entity.star = resultListBean.star;
                entity.isFluentRead = resultListBean.isFluentRead;
                books.add(entity);
            }
        }
    }

    private BookstoreModuleResultBean done(Call<BookstoreModuleResultBean> call) {
        EnhancedCall<BookstoreModuleResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookstoreModuleResultBean.class);
    }

    private Call<BookstoreModuleResultBean> getCall(GetBookstoreModuleService getCommonService) {
        return getCommonService.getBookstoreModule(baseRequestBean.getAppBaseInfo().getRequestParamsMap(),
                CloudApiContext.BookstoreModule.MODULE_CHILD_INFO, baseRequestBean.getBody());
    }

    private GetBookstoreModuleService init(String URL) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(EnhancedCall.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GetBookstoreModuleService.class);
    }
}