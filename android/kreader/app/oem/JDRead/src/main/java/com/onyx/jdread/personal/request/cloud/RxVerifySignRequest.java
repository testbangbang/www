package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.entity.jdbean.VerifySignBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/22.
 */

public class RxVerifySignRequest extends RxBaseCloudRequest {
    private JDAppBaseInfo baseInfo;
    private VerifySignBean verifySignBean;

    public VerifySignBean getVerifySignBean() {
        return verifySignBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<VerifySignBean> call = getCall(service);
        Response<VerifySignBean> response = call.execute();
        if (response.isSuccessful()) {
            verifySignBean = response.body();
        }
        return this;
    }

    private Call<VerifySignBean> getCall(ReadContentService service) {
        return service.verifySign(baseInfo.getRequestParamsMap());
    }

    public void setBaseInfo(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}
