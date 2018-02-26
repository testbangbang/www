package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.entity.jdbean.CheckGiftBean;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/2/26.
 */

public class RxCheckGiftRequest extends RxBaseCloudRequest {
    private JDAppBaseInfo baseInfo;
    private CheckGiftBean checkGiftBean;

    public void setBaseInfo(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public CheckGiftBean getCheckGiftBean() {
        return checkGiftBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<CheckGiftBean> call = getCall(service);
        Response<CheckGiftBean> response = call.execute();
        if (response.isSuccessful()) {
            checkGiftBean = response.body();
            checkResult();
        }
        return this;
    }

    private void checkResult() {
        if (checkGiftBean != null && checkGiftBean.result_code != 0) {
            PersonalDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(checkGiftBean.message));
        }
    }

    private Call<CheckGiftBean> getCall(ReadContentService service) {
        return service.checkGift(baseInfo.getRequestParamsMap());
    }
}
