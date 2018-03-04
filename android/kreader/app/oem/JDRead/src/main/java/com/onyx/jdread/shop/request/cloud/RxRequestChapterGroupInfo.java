package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.GetChapterGroupInfoRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BatchDownloadResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import retrofit2.Call;

/**
 * Created by jackdeng on 2018/3/3.
 */

public class RxRequestChapterGroupInfo extends RxBaseCloudRequest {
    private GetChapterGroupInfoRequestBean requestBean;
    private BatchDownloadResultBean bookDetailResultBean;

    public BatchDownloadResultBean getResultBean() {
        return bookDetailResultBean;
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
        Call<BatchDownloadResultBean> call = getCall(getCommonService);
        bookDetailResultBean = done(call);
        checkResult();
    }

    private void checkResult() {
        if (bookDetailResultBean != null && bookDetailResultBean.result_code != 0) {
            ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(bookDetailResultBean.message));
        }
    }

    private BatchDownloadResultBean done(Call<BatchDownloadResultBean> call) {
        EnhancedCall<BatchDownloadResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BatchDownloadResultBean.class);
    }

    private Call<BatchDownloadResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getChapterGroupInfo(requestBean.bookId, requestBean.getBaseInfo().getRequestParamsMap());
    }
}
