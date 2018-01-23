package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/22.
 */

public class RxReadingForVoucherRequest extends RxBaseCloudRequest {
    private JDAppBaseInfo baseInfo;

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getServiceForString(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<String> call = getCall(service);
        Response<String> response = call.execute();
        if (response.isSuccessful()) {
            String body = response.body();
        }
        return this;
    }

    private Call<String> getCall(ReadContentService service) {
        return service.readForVoucher(baseInfo.getRequestParamsMap());
    }

    public void setBaseInfo(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}
