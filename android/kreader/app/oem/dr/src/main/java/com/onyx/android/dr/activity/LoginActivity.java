package com.onyx.android.dr.activity;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.presenter.LoginPresenter;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;

public class LoginActivity extends BaseActivity implements LoginView {
    private LoginPresenter loginPresenter;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        loginPresenter = new LoginPresenter(this);
        loginPresenter.login("", "");
    }

    @Override
    public void setAccountInfo(NeoAccountBase accountInfo) {
        if (accountInfo == null) {
            CommonNotices.showMessage(this,getString(R.string.username_or_password_error));
        }else {
            CommonNotices.showMessage(this,getString(R.string.login_succeed));
            // TODO: 17-6-30  
        }
    }
}
