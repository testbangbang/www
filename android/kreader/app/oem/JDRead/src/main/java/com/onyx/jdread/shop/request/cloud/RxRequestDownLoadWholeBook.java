package com.onyx.jdread.shop.request.cloud;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.common.EncryptHelper;
import com.onyx.jdread.shop.cloud.entity.DownLoadWholeBookRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.DownLoadWholeBookResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jackdeng on 2018-1-23.
 */

public class RxRequestDownLoadWholeBook extends RxBaseCloudRequest {
    private DownLoadWholeBookRequestBean requestBean;
    private DownLoadWholeBookResultBean resultBean;

    public DownLoadWholeBookResultBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(DownLoadWholeBookRequestBean requestBean) {
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
            resultBean = JSONObject.parseObject(decryptContent, DownLoadWholeBookResultBean.class);
            checkQuestResult();
        }
    }

    private void encryptParams() {
        String encryptKey = EncryptHelper.getEncryptKey(requestBean.saltValue);
        JDAppBaseInfo appBaseInfo = requestBean.getAppBaseInfo();
        String encryptParams = EncryptHelper.getEncryptParams(encryptKey, appBaseInfo.getRequestParams());
        appBaseInfo.clear();
        appBaseInfo.setEnc();
        appBaseInfo.addApp();
        appBaseInfo.setParams(encryptParams);
    }

    private void checkQuestResult() {
        if (resultBean != null && resultBean.data != null) {

        }
    }

    private Call<String> getCall(ReadContentService getCommonService) {
        return getCommonService.getDownLoadBookInfo(requestBean.bookId, requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
