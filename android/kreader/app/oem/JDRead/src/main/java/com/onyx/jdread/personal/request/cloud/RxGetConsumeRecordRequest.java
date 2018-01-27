package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.cloud.entity.jdbean.ConsumeRecordBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/26.
 */

public class RxGetConsumeRecordRequest extends RxBaseCloudRequest {
    private JDAppBaseInfo baseInfo;
    private ConsumeRecordBean consumeRecordBean;

    public ConsumeRecordBean getConsumeRecordBean() {
        return consumeRecordBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<ConsumeRecordBean> call = getCall(service);
        Response<ConsumeRecordBean> response = call.execute();
        if (response.isSuccessful()) {
            consumeRecordBean = response.body();
            checkResult();
        }
        return this;
    }

    private void checkResult() {
        if (consumeRecordBean != null && consumeRecordBean.getResult_code() != 0) {
            ToastUtil.showToast(consumeRecordBean.getMessage());
        }
    }

    private Call<ConsumeRecordBean> getCall(ReadContentService service) {
        return service.getConsumeRecord(baseInfo.getRequestParamsMap());
    }

    public void setBaseInfo(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}
