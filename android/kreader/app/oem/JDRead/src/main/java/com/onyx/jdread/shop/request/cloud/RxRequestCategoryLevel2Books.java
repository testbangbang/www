package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.CategoryLevel2BooksRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryLevel2BooksResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by hehai on 17-3-30.
 */

public class RxRequestCategoryLevel2Books extends RxBaseCloudRequest {
    private CategoryLevel2BooksRequestBean requestBean;
    private CategoryLevel2BooksResultBean categoryLevel2BooksResultBean;

    public CategoryLevel2BooksResultBean getCategoryLevel2BooksResultBean() {
        return categoryLevel2BooksResultBean;
    }

    public void setRequestBean(CategoryLevel2BooksRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        setBaseRequestBeanParams();
        ReadContentService getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        Call<CategoryLevel2BooksResultBean> call = getCall(getCommonService);
        categoryLevel2BooksResultBean = done(call);
        checkQuestResult();
    }

    private void setBaseRequestBeanParams() {
        if (requestBean != null) {
            Map<String, String> queryArgs = new HashMap<>();
            queryArgs.put(CloudApiContext.SearchBook.SEARCH_TYPE, requestBean.search_type);
            queryArgs.put(CloudApiContext.SearchBook.CATE_ID, requestBean.cid);
            queryArgs.put(CloudApiContext.SearchBook.SORT, requestBean.sort);
            queryArgs.put(CloudApiContext.SearchBook.CURRENT_PAGE, requestBean.page);
            queryArgs.put(CloudApiContext.SearchBook.PAGE_SIZE, requestBean.page_size);
            requestBean.setQueryArgsMap(queryArgs);
        }
    }

    private CategoryLevel2BooksResultBean done(Call<CategoryLevel2BooksResultBean> call) {
        EnhancedCall<CategoryLevel2BooksResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, CategoryLevel2BooksResultBean.class);
    }

    private void checkQuestResult() {
        if (categoryLevel2BooksResultBean != null && categoryLevel2BooksResultBean.bookList != null) {
        }
    }

    private Call<CategoryLevel2BooksResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getCategoryLevel2BookList(requestBean.getAppBaseInfo().getRequestParamsMap(),requestBean.getQueryArgsMap());
    }
}
