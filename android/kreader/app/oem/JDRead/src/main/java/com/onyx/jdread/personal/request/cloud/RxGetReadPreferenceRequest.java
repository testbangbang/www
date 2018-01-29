package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetReadPreferenceBean;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.entity.BaseShopRequestBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/22.
 */

public class RxGetReadPreferenceRequest extends RxBaseCloudRequest {
    private BaseShopRequestBean requestBean;
    private GetReadPreferenceBean resultBean;

    public GetReadPreferenceBean getResultBean() {
        return resultBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<GetReadPreferenceBean> call = getCall(service);
        Response<GetReadPreferenceBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
            checkResult();
        }
        return this;
    }

    private void checkResult() {
        if (resultBean != null && resultBean.result_code != 0) {
            RequestFailedEvent.sendFailedMessage(resultBean.message);
        }
    }

    private Call<GetReadPreferenceBean> getCall(ReadContentService service) {
        return service.getReadPreference(requestBean.getBaseInfo().getRequestParamsMap());
    }

    public void setRequestBean(BaseShopRequestBean requestBean) {
        this.requestBean = requestBean;
    }
}
