package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.shop.cloud.api.GetRecommendListService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.RecommendListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        GetRecommendListService getCommonService = init(CloudApiContext.getJDBooxBaseUrl());
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

    private Call<RecommendListResultBean> getCall(GetRecommendListService getCommonService) {
        return getCommonService.getCategoryList(baseRequestBean.getAppBaseInfo().getRequestParamsMap(),
                CloudApiContext.RecommendList.BOOK_DETAIL_RECOMMEND_LIST_V2,
                baseRequestBean.getBody()
        );
    }

    private GetRecommendListService init(String URL) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(EnhancedCall.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GetRecommendListService.class);
    }
}