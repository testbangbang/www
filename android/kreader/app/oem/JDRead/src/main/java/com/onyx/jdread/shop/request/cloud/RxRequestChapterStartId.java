package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.GetChapterGroupInfoRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetChapterStartIdResult;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import retrofit2.Call;

/**
 * Created by jackdeng on 2018/3/6.
 */

public class RxRequestChapterStartId extends RxBaseCloudRequest {
    private GetChapterGroupInfoRequestBean requestBean;
    private GetChapterStartIdResult resultBean;

    public GetChapterStartIdResult getResultBean() {
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
        Call<GetChapterStartIdResult> call = getCall(getCommonService);
        resultBean = done(call);
        checkResult();
    }

    private void checkResult() {
        if (resultBean != null && resultBean.result_code != 0) {
            ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
        }
    }

    private GetChapterStartIdResult done(Call<GetChapterStartIdResult> call) {
        EnhancedCall<GetChapterStartIdResult> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, GetChapterStartIdResult.class);
    }

    private Call<GetChapterStartIdResult> getCall(ReadContentService getCommonService) {
        return getCommonService.getChapterStartId(requestBean.bookId, requestBean.getBaseInfo().getRequestParamsMap());
    }
}
