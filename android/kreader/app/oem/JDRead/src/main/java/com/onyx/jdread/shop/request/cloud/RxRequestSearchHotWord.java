package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.GetBookDetailRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.SearchHotWord;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;

/**
 * Created by huxiaomao on 2016/12/19.
 */

public class RxRequestSearchHotWord extends RxBaseCloudRequest {
    private GetBookDetailRequestBean requestBean;
    private SearchHotWord searchHotWord;

    public void setRequestBean(GetBookDetailRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public SearchHotWord getSearchHotWord() {
        return searchHotWord;
    }

    @Override
    public RxRequestSearchHotWord call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        Call<SearchHotWord> call = getCall(getCommonService);
        searchHotWord = done(call);
    }

    private SearchHotWord done(Call<SearchHotWord> call) {
        EnhancedCall<SearchHotWord> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, SearchHotWord.class);
    }

    private Call<SearchHotWord> getCall(ReadContentService getCommonService) {
        return getCommonService.getSearchHot(JDAppBaseInfo.APP_DEFAULT_VALUE);
    }
}
