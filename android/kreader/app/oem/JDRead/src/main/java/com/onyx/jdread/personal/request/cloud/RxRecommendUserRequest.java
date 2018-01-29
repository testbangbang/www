package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.cloud.entity.jdbean.RecommendUserBean;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/25.
 */

public class RxRecommendUserRequest extends RxBaseCloudRequest {
    private JDAppBaseInfo baseInfo;
    private RecommendUserBean recommendUserBean;

    public RecommendUserBean getRecommendUserBean() {
        return recommendUserBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<RecommendUserBean> call = getCall(service);
        Response<RecommendUserBean> response = call.execute();
        if (response.isSuccessful()) {
            recommendUserBean = response.body();
            checkResult();
        }
        return this;
    }

    private void checkResult() {
        if (recommendUserBean != null && recommendUserBean.result_code != 0) {
            RequestFailedEvent.sendFailedMessage(recommendUserBean.message);
        }
    }

    private Call<RecommendUserBean> getCall(ReadContentService service) {
        return service.recommendUser(baseInfo.getRequestParamsMap());
    }

    public void setBaseInfo(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}
