package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.GetChapterGroupInfoRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetChapterCatalogResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import retrofit2.Call;

/**
 * Created by jackdeng on 2018/3/9.
 */

public class RxRequestGetChapterCatalog extends RxBaseCloudRequest {
    private GetChapterGroupInfoRequestBean requestBean;
    private GetChapterCatalogResultBean resultBean;

    public GetChapterCatalogResultBean getResultBean() {
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
        Call<GetChapterCatalogResultBean> call = getCall(getCommonService);
        resultBean = done(call);
        checkResult();
    }

    private void checkResult() {
        if (resultBean != null && resultBean.result_code != 0) {
            ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
        }
    }

    private GetChapterCatalogResultBean done(Call<GetChapterCatalogResultBean> call) {
        EnhancedCall<GetChapterCatalogResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, GetChapterCatalogResultBean.class);
    }

    private Call<GetChapterCatalogResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getChapterCatalog(requestBean.bookId, requestBean.getBaseInfo().getRequestParamsMap());
    }
}
