package com.onyx.android.dr.request.cloud;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.bean.SignUpInfo;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.v2.AuthToken;
import com.onyx.android.sdk.data.model.v2.SignUpBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.utils.StringUtils;

import retrofit2.Response;

/**
 * Created by hehai on 2017/6/30.
 */

public class SignUpRequest extends AutoNetWorkConnectionBaseCloudRequest {
    private SignUpInfo signUpInfo;
    private AuthToken authToken;

    public SignUpRequest(SignUpInfo signUpInfo) {
        this.signUpInfo = signUpInfo;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        signUp(parent);
    }

    private void signUp(CloudManager parent) throws Exception {
        SignUpBean signUpBean = new SignUpBean();
        signUpBean.name = signUpInfo.name;
        signUpBean.email = signUpInfo.email;
        signUpBean.password = signUpInfo.password;
        signUpBean.groupId = signUpInfo.groupId;
        signUpBean.info = JSON.toJSON(signUpInfo.info).toString();
        Response<AuthToken> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .signUp(signUpBean));
        if (response.isSuccessful()) {
            authToken = response.body();
            parent.setToken(authToken.token);
            updateTokenHeader(parent);
        }
    }

    private void updateTokenHeader(final CloudManager cloudManager) {
        if (StringUtils.isNotBlank(cloudManager.getToken())) {
            ServiceFactory.addRetrofitTokenHeader(cloudManager.getCloudConf().getApiBase(),
                    Constant.HEADER_AUTHORIZATION,
                    ContentService.CONTENT_AUTH_PREFIX + cloudManager.getToken());
        }
    }
}
