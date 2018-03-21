package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BookRecommendListRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BaseResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.RecommendListResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.utils.ViewHelper;

import java.util.List;

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
        checkRequestResult();
    }

    private RecommendListResultBean done(Call<RecommendListResultBean> call) {
        EnhancedCall<RecommendListResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, RecommendListResultBean.class);
    }

    private void checkRequestResult() {
        if (recommendListResultBean != null) {
            if (BaseResultBean.checkSuccess(recommendListResultBean.result_code)) {
                if (recommendListResultBean.data != null) {
                    List<ResultBookBean> data = recommendListResultBean.data;
                    if (!CollectionUtils.isNullOrEmpty(data)) {
                        ViewHelper.saveBitmapCover(data, getAppContext());
                    }
                }
            } else {
                ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(recommendListResultBean.message));
            }
        }
    }

    private Call<RecommendListResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getRecommendList(requestBean.bookId, requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
