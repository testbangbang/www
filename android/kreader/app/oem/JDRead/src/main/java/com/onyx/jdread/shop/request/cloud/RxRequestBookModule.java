package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.data.database.BookDetailEntity;
import com.onyx.jdread.shop.cloud.api.GetBookModuleService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelResultBean;
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

public class RxRequestBookModule extends RxBaseCloudRequest {

    private BaseRequestBean baseRequestBean;
    private BookModelResultBean bookModelResultBean;
    private ArrayList<BookDetailEntity> books;

    public BookModelResultBean getBookModelResultBean() {
        return bookModelResultBean;
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
        GetBookModuleService getCommonService = init(CloudApiContext.getJDBooxBaseUrl());
        Call<BookModelResultBean> call = getCall(getCommonService);
        bookModelResultBean = done(call);
        checkQuestResult();
    }

    private void checkQuestResult() {
        if (bookModelResultBean != null && bookModelResultBean.resultList != null) {
            books = new ArrayList<>();
            for (ResultBookBean resultListBean : bookModelResultBean.resultList) {
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