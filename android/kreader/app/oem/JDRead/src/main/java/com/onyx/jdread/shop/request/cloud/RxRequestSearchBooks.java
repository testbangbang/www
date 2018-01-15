package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.SearchBooksRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import java.util.HashMap;
import java.util.Map;

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
        setBaseRequestBeanParams();
        ReadContentService getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        Call<BookModelBooksResultBean> call = getCall(getCommonService);
        resultBean = done(call);
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

    private BookModelBooksResultBean done(Call<BookModelBooksResultBean> call) {
        EnhancedCall<BookModelBooksResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookModelBooksResultBean.class);
    }

    private void checkQuestResult() {
        if (resultBean != null && resultBean.data.items != null) {

        }
    }

    private Call<BookModelBooksResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getSearchBooks(requestBean.getAppBaseInfo().getRequestParamsMap(),requestBean.getQueryArgsMap());
    }
}
