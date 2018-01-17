package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfoBean;
import com.onyx.jdread.personal.common.EncryptHelper;
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
        baseInfo.setParams(encryptParams);
        baseInfo.setEnc();
        baseInfo.addApp();
        ReadContentService userInfoService = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<UserInfoBean> call = getCall(userInfoService);
        Response<UserInfoBean> response = call.execute();
        if (response.isSuccessful()) {
            userInfoBean = response.body();
            checkQuestResult();
        }

        return this;
    }

    private void checkQuestResult() {
        if (userInfoBean != null && !StringUtils.isNullOrEmpty(userInfoBean.resultCode)) {
            switch (userInfoBean.resultCode) {

            }
        }
    }

    private Call<UserInfoBean> getCall(ReadContentService userInfoService) {
        return userInfoService.getUserInfo(baseInfo.getRequestParamsMap());
    }

    public void setSaltValue(String saltValue) {
        this.saltValue = saltValue;
    }
}
