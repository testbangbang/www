package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BookRecommendListRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.RecommendListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;

/**
 * Created by hehai on 17-3-30.
 */

public class RxRequestRecommendList extends RxBaseCloudRequest {
    private BookRecommendListRequestBean requestBean;
    private RecommendListResultBean recommendListResultBean;

    public RecommendListResultBean getRecommendListResultBean() {
        return recommendListResultBean;
    }

    public void setRequestBean(BookRecommendListRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getServiceNoCookie(CloudApiContext.getJDBooxBaseUrl());
        Call<RecommendListResultBean> call = getCall(getCommonService);
        recommendListResultBean = done(call);
        checkQuestResult();

    }

    private RecommendListResultBean done(Call<RecommendListResultBean> call) {
        EnhancedCall<RecommendListResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, RecommendListResultBean.class);
    }

    private void checkQuestResult() {
        if (recommendListResultBean != null && recommendListResultBean.data != null) {

        }
    }

    private Call<RecommendListResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getRecommendList(requestBean.bookId, requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
