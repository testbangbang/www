package com.onyx.android.sun.presenter;

import com.onyx.android.sun.cloud.bean.UserLoginRequestBean;
import com.onyx.android.sun.cloud.bean.UserLoginResultBean;
import com.onyx.android.sun.data.UserLoginActData;
import com.onyx.android.sun.interfaces.UserLoginView;
import com.onyx.android.sun.requests.UserLoginRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;

/**
 * Created by jackdeng on 2017/10/23.
 */

public class UserLoginPresenter {
    private UserLoginActData mLoginData;
    private UserLoginView mLoginView ;

    public UserLoginPresenter(UserLoginView loginView) {
        mLoginData = new UserLoginActData();
        mLoginView = loginView;
    }

    public void loginAccount(String account, String password) {
        UserLoginRequestBean requestBean = new UserLoginRequestBean();
        requestBean.account = account;
        requestBean.password = password;
        final UserLoginRequest rq = new UserLoginRequest(requestBean);
        mLoginData.userLogin(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UserLoginResultBean resultBean = rq.getLoginResultBean();
                if (resultBean == null) {
                    return;
                }
                if (resultBean.code == 0)
                    mLoginView.onLoginSucced(resultBean.data);
                else
                    mLoginView.onLoginFailed(resultBean.code,resultBean.msg);
            }
        });
    }
}
