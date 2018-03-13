package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.GetChapterGroupInfoRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetChaptersContentResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import retrofit2.Call;

/**
 * Created by jackdeng on 2018/3/9.
 */

public class RxRequestGetChaptersContent extends RxBaseCloudRequest {
    private GetChapterGroupInfoRequestBean requestBean;
    private GetChaptersContentResultBean resultBean;

    public GetChaptersContentResultBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(GetChapterGroupInfoRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        Call<GetChaptersContentResultBean> call = getCall(getCommonService);
        resultBean = done(call);
        checkResult();
    }

    private void checkResult() {
        if (resultBean != null && resultBean.result_code != 0) {
            ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
        }
    }

    private GetChaptersContentResultBean done(Call<GetChaptersContentResultBean> call) {
        EnhancedCall<GetChaptersContentResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, GetChaptersContentResultBean.class);
    }

    private Call<GetChaptersContentResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getChaptersContent(requestBean.bookId, requestBean.getBaseInfo().getRequestParamsMap());
    }
}
