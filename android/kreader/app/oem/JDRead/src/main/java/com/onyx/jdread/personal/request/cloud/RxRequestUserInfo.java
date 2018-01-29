package com.onyx.jdread.personal.request.cloud;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfoBean;
import com.onyx.jdread.personal.common.EncryptHelper;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

public class RxRequestUserInfo extends RxBaseCloudRequest {
    private JDAppBaseInfo baseInfo;
    private UserInfoBean userInfoBean;
    private String saltValue;

    public void setUserInfoRequestBean(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public UserInfoBean getUserInfoBean() {
        return userInfoBean;
    }

    @Override
    public Object call() throws Exception {
        String encryptKey = EncryptHelper.getEncryptKey(saltValue);
        String encryptParams = EncryptHelper.getEncryptParams(encryptKey, baseInfo.getRequestParams());
        baseInfo.clear();
        baseInfo.setEnc();
        baseInfo.addApp();
        baseInfo.setParams(encryptParams);
        ReadContentService userInfoService = CloudApiContext.getServiceForString(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<String> call = getCall(userInfoService);
        Response<String> response = call.execute();
        if (response.isSuccessful()) {
            String body = response.body();
            String decryptContent = EncryptHelper.getDecryptContent(body);
            userInfoBean = JSONObject.parseObject(decryptContent, UserInfoBean.class);
            checkQuestResult();
        }

        return this;
    }

    private void checkQuestResult() {
        if (userInfoBean != null && userInfoBean.result_code != 0) {
            RequestFailedEvent.sendFailedMessage(userInfoBean.message);
        }
    }

    private Call<String> getCall(ReadContentService userInfoService) {
        return userInfoService.getUserInfo(baseInfo.getRequestParamsMap());
    }

    public void setSaltValue(String saltValue) {
        this.saltValue = saltValue;
    }
}
