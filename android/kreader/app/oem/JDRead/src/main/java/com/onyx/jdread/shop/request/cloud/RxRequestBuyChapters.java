package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BuyChaptersRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BuyChaptersResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import retrofit2.Call;

/**
 * Created by jackdeng on 2018/3/8.
 */

public class RxRequestBuyChapters extends RxBaseCloudRequest {
    private BuyChaptersRequestBean requestBean;
    private BuyChaptersResultBean resultBean;

    public BuyChaptersResultBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(BuyChaptersRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        Call<BuyChaptersResultBean> call = getCall(getCommonService);
        resultBean = done(call);
        checkResult();
    }

    private void checkResult() {
        if (resultBean != null && resultBean.result_code != 0) {
            ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
        }
    }

    private BuyChaptersResultBean done(Call<BuyChaptersResultBean> call) {
        EnhancedCall<BuyChaptersResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BuyChaptersResultBean.class);
    }

    private Call<BuyChaptersResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.buyChapters(requestBean.bookId, requestBean.getBaseInfo().getRequestParamsMap());
    }
}
