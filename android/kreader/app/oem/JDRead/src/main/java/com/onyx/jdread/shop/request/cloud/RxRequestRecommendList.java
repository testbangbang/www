package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.main.common.CloudApiContext;
import com.onyx.jdread.main.servie.ReadContentService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.RecommendListResultBean;

import retrofit2.Call;

/**
 * Created by hehai on 17-3-30.
 */

public class RxRequestRecommendList extends RxBaseCloudRequest {
    private BaseRequestBean baseRequestBean;
    private RecommendListResultBean recommendListResultBean;

    public RecommendListResultBean getRecommendListResultBean() {
        return recommendListResultBean;
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
        ReadContentService getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        Call<RecommendListResultBean> call = getCall(getCommonService);
        recommendListResultBean = done(call);
        checkQuestResult();

    }

    private RecommendListResultBean done(Call<RecommendListResultBean> call) {
        EnhancedCall<RecommendListResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, RecommendListResultBean.class);
    }

    private void checkQuestResult() {
        if (recommendListResultBean != null && StringUtils.isNullOrEmpty(recommendListResultBean.code)) {
            switch (recommendListResultBean.code) {
                default:
                    break;
            }
        }
    }

    private Call<RecommendListResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getRecommendList(baseRequestBean.getAppBaseInfo().getRequestParamsMap(),
                CloudApiContext.RecommendList.BOOK_DETAIL_RECOMMEND_LIST_V2,
                baseRequestBean.getBody()
        );
    }
}
