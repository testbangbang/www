package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.entity.jdbean.SignForVoucherBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/22.
 */

public class RxSignForVoucherRequest extends RxBaseCloudRequest {
    private JDAppBaseInfo baseInfo;
    private SignForVoucherBean resultBean;

    public SignForVoucherBean getResultBean() {
        return resultBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<SignForVoucherBean> call = getCall(service);
        Response<SignForVoucherBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }
        return this;
    }

    private Call<SignForVoucherBean> getCall(ReadContentService service) {
        return service.signForVoucher(baseInfo.getRequestParamsMap());
    }

    public void setBaseInfo(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}
