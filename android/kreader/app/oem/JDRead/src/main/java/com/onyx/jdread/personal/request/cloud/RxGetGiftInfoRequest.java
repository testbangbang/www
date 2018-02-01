package com.onyx.jdread.personal.request.cloud;


import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.entity.jdbean.GiftBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/23.
 */

public class RxGetGiftInfoRequest extends RxBaseCloudRequest {
    private JDAppBaseInfo baseInfo;
    private GiftBean giftBean;

    public GiftBean getGiftBean() {
        return giftBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<GiftBean> call = getCall(service);
        Response<GiftBean> response = call.execute();
        if (response.isSuccessful()) {
            giftBean = response.body();
        }
        return this;
    }

    private Call<GiftBean> getCall(ReadContentService service) {
        return service.getGiftInfo(baseInfo.getRequestParamsMap());
    }

    public void setBaseInfo(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}
