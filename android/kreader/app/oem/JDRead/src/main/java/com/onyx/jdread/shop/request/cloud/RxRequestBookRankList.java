package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BookRankListRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.RecommendListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import retrofit2.Call;

/**
 * Created by jackdeng on 2018/1/19.
 */

public class RxRequestBookRankList extends RxBaseCloudRequest {

    private BookRankListRequestBean requestBean;
    private RecommendListResultBean resultBean;

    public RecommendListResultBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(BookRankListRequestBean requestBean) {
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
        resultBean = done(call);
        checkResult();
    }

    private void checkResult() {
        if (resultBean != null && resultBean.result_code != 0) {
            ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
        }
    }

    private RecommendListResultBean done(Call<RecommendListResultBean> call) {
        EnhancedCall<RecommendListResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, RecommendListResultBean.class);
    }

    private Call<RecommendListResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getBookRankList(requestBean.getModuleType(), requestBean.getType(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
