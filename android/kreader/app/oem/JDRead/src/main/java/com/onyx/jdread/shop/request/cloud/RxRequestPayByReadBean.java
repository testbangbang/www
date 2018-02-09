package com.onyx.jdread.shop.request.cloud;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.common.EncryptHelper;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.entity.PayCommonRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BaseResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jackdeng on 2018-1-23.
 */

public class RxRequestPayByReadBean extends RxBaseCloudRequest {
    private PayCommonRequestBean requestBean;
    private BaseResultBean resultBean;

    public BaseResultBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(PayCommonRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() throws IOException {
        encryptParams();
        ReadContentService getCommonService = CloudApiContext.getServiceForString(CloudApiContext.getJDBooxBaseUrl());
        Call<String> call = getCall(getCommonService);
        Response<String> response = call.execute();
        if (response.isSuccessful()) {
            String body = response.body();
            String decryptContent = EncryptHelper.getDecryptContent(body);
            resultBean = JSONObject.parseObject(decryptContent, BaseResultBean.class);
            checkQuestResult();
        }
    }

    private void encryptParams() {
        String encryptKey = EncryptHelper.getEncryptKey(requestBean.saltValue);
        JDAppBaseInfo appBaseInfo = requestBean.getBaseInfo();
        String encryptParams = EncryptHelper.getEncryptParams(encryptKey, appBaseInfo.getRequestParams());
        appBaseInfo.clear();
        appBaseInfo.setEnc();
        appBaseInfo.addApp();
        appBaseInfo.setParams(encryptParams);
    }

    private void checkQuestResult() {
        if (resultBean != null && resultBean.result_code != 0) {
            ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
        }
    }

    private Call<String> getCall(ReadContentService getCommonService) {
        return getCommonService.payByReadBean(requestBean.getBaseInfo().getRequestParamsMap());
    }
}
