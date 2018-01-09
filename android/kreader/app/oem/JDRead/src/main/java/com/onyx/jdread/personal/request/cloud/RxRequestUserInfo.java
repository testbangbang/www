package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.main.common.CloudApiContext;
import com.onyx.jdread.main.servie.ReadContentService;
import com.onyx.jdread.personal.cloud.entity.UserInfoRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfoBean;

import retrofit2.Call;
import retrofit2.Response;

public class RxRequestUserInfo extends RxBaseCloudRequest {

    private UserInfoRequestBean userInfoRequestBean;
    private UserInfoBean userInfoBean;

    public void setUserInfoRequestBean(UserInfoRequestBean userInfoRequestBean) {
        this.userInfoRequestBean = userInfoRequestBean;
    }

    public UserInfoBean getUserInfoBean() {
        return userInfoBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService userInfoService = CloudApiContext.getService(CloudApiContext.getJdBaseUrl());
        Call<UserInfoBean> call = getCall(userInfoService);
        Response<UserInfoBean> response = call.execute();
        if (response != null) {
            userInfoBean = response.body();
            checkQuestResult();
        }

        return this;
    }

    private void checkQuestResult() {
        if (userInfoBean != null && !StringUtils.isNullOrEmpty(userInfoBean.getCode())) {
            switch (userInfoBean.getCode()) {

            }
        }
    }

    private Call<UserInfoBean> getCall(ReadContentService userInfoService) {
        return userInfoService.getUserInfo(CloudApiContext.NewBookDetail.USER_BASIC_INFO,
                userInfoRequestBean.getBody(),
                userInfoRequestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
