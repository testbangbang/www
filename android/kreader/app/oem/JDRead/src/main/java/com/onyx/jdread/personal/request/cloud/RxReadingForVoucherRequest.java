package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadForVoucherBean;
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
    private ReadForVoucherBean resultBean;

    public ReadForVoucherBean getResultBean() {
        return resultBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<ReadForVoucherBean> call = getCall(service);
        Response<ReadForVoucherBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }
        return this;
    }

    private Call<ReadForVoucherBean> getCall(ReadContentService service) {
        return service.readForVoucher(baseInfo.getRequestParamsMap());
    }

    public void setBaseInfo(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}
