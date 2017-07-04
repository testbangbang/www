package com.onyx.android.dr.presenter;

import com.onyx.android.dr.activity.LoginView;
import com.onyx.android.dr.data.LoginData;
import com.onyx.android.dr.request.cloud.LoginByAdminRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;

/**
 * Created by hehai on 17-6-30.
 */

public class LoginPresenter {
    private LoginView loginView;
    private LoginData loginData;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
        loginData = new LoginData();
    }

    public void login(String userName ,String password){
        BaseAuthAccount neoAccountBase = new BaseAuthAccount();
        neoAccountBase.username = userName;
        neoAccountBase.password = password;
        final LoginByAdminRequest req = new LoginByAdminRequest(neoAccountBase);
        loginData.login(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                loginView.setAccountInfo(req.getNeoAccount());
            }
        });
    }
}
