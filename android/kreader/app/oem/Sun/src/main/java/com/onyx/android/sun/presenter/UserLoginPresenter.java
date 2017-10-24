package com.onyx.android.sun.presenter;

import com.onyx.android.sun.cloud.bean.UserLoginRequestBean;
import com.onyx.android.sun.cloud.bean.UserLoginResultBean;
import com.onyx.android.sun.data.UserLoginActData;
import com.onyx.android.sun.interfaces.UserLoginView;
import com.onyx.android.sun.requests.cloud.UserLoginRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;

/**
 * Created by jackdeng on 2017/10/23.
 */

public class UserLoginPresenter {
    private UserLoginActData loginData;
    private UserLoginView loginView;

    public UserLoginPresenter(UserLoginView loginView) {
        loginData = new UserLoginActData();
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
                if (resultBean.code == 0){
                    loginView.onLoginSucced(resultBean.data);
                } else {
                    loginView.onLoginFailed(resultBean.code,resultBean.msg);
                }

            }
        });
    }
}
