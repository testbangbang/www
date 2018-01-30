package com.onyx.jdread.personal.request.cloud;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetRechargePackageBean;
import com.onyx.jdread.personal.common.EncryptHelper;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/25.
 */

public class RxRechargePackageRequest extends RxBaseCloudRequest {
    private JDAppBaseInfo baseInfo;
    private String saltValue;
    private GetRechargePackageBean resultBean;

    public GetRechargePackageBean getResultBean() {
        return resultBean;
    }

    @Override
    public Object call() throws Exception {
        String encryptKey = EncryptHelper.getEncryptKey(saltValue);
        String encryptParams = EncryptHelper.getEncryptParams(encryptKey, baseInfo.getRequestParams());
        baseInfo.clear();
        baseInfo.setEnc();
        baseInfo.addApp();
        baseInfo.setParams(encryptParams);
        ReadContentService service = CloudApiContext.getServiceForString(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<String> call = getCall(service);
        Response<String> response = call.execute();
        if (response.isSuccessful()) {
            String body = response.body();
            String decryptContent = EncryptHelper.getDecryptContent(body);
            resultBean = JSON.parseObject(decryptContent, GetRechargePackageBean.class);
            checkResult();
        }
        return this;
    }

    private void checkResult() {
        if (resultBean != null && resultBean.result_code != 0) {
            PersonalDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
        }
    }

    private Call<String> getCall(ReadContentService service) {
        return service.getRechargePackage(baseInfo.getRequestParamsMap());
    }

    public void setBaseInfo(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public void setSaltValue(String saltValue) {
        this.saltValue = saltValue;
    }
}
