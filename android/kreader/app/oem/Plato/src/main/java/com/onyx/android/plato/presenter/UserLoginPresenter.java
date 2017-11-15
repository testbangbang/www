package com.onyx.android.plato.presenter;

import com.onyx.android.plato.cloud.bean.UserLoginRequestBean;
import com.onyx.android.plato.cloud.bean.UserLoginResultBean;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.data.UserLoginActivityData;
import com.onyx.android.plato.interfaces.UserLoginView;
import com.onyx.android.plato.requests.cloud.UserLoginRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;

/**
 * Created by jackdeng on 2017/10/23.
 */

public class UserLoginPresenter {
    private UserLoginActivityData loginData;
    private UserLoginView loginView;

    public UserLoginPresenter(UserLoginView loginView) {
        loginData = new UserLoginActivityData();
        this.loginView = loginView;
    }

    public void loginAccount(String account, String password) {
        UserLoginRequestBean requestBean = new UserLoginRequestBean();
        requestBean.account = account;
        requestBean.password = password;
        final UserLoginRequest rq = new UserLoginRequest(requestBean);
        loginData.userLogin(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UserLoginResultBean resultBean = rq.getLoginResultBean();
                if (resultBean == null) {
                    loginView.onLoginError(e);
                    return;
                }
                if (resultBean.code == CloudApiContext.HttpReusltCode.RESULT_CODE_SUCCESS){
                    loginView.onLoginSucced(resultBean.data);
                } else {
                    loginView.onLoginFailed(resultBean.code,resultBean.msg);
                }

            }
        });
    }
}
