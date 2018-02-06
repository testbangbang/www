package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.jdbean.SearchHotWord;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import retrofit2.Call;

/**
 * Created by huxiaomao on 2016/12/19.
 */

public class RxRequestSearchHotWord extends RxBaseCloudRequest {
    private JDAppBaseInfo requestBean;
    private SearchHotWord searchHotWord;

    public void setRequestBean(JDAppBaseInfo requestBean) {
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
        ReadContentService getCommonService = CloudApiContext.getServiceNoCookie(CloudApiContext.getJDBooxBaseUrl());
        Call<SearchHotWord> call = getCall(getCommonService);
        searchHotWord = done(call);
        checkRequestResult();
    }

    private void checkRequestResult() {
        if (searchHotWord != null && searchHotWord.result_code != 0) {
            ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(searchHotWord.message));
        }
    }

    private SearchHotWord done(Call<SearchHotWord> call) {
        EnhancedCall<SearchHotWord> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, SearchHotWord.class);
    }

    private Call<SearchHotWord> getCall(ReadContentService getCommonService) {
        return getCommonService.getSearchHot(requestBean.getRequestParamsMap());
    }
}
