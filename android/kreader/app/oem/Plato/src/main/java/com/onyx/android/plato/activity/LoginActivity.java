package com.onyx.android.plato.activity;

import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;

import com.onyx.android.plato.cloud.bean.UserBean;
import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.plato.BR;
import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.UserLoginRequestBean;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.common.Constants;
import com.onyx.android.plato.common.ManagerActivityUtils;
import com.onyx.android.plato.databinding.ActivityUserLoginBinding;
import com.onyx.android.plato.interfaces.UserLoginView;
import com.onyx.android.plato.presenter.UserLoginPresenter;
import com.umeng.analytics.MobclickAgent;

import java.net.ConnectException;

/**
 * Created by jackdeng on 2017/10/23.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, UserLoginView {
    private ActivityUserLoginBinding loginDataBinding;
    private UserLoginPresenter userLoginPresenter;
    private UserLoginRequestBean userLoginRequestBean = new UserLoginRequestBean();
    private DialogLoading loginLoadingDialog;

    @Override
    protected void initData() {
        userLoginPresenter = new UserLoginPresenter(this);
        userLoginRequestBean.isKeepPassword = PreferenceManager.getBooleanValue(SunApplication.getInstance(), Constants.SP_KEY_ISKEEPPASSWORD, false);
        restoreUserInfo();
        loginLoadingDialog = new DialogLoading(LoginActivity.this, getString(R.string.login_activity_loading_tip), false);
        MobclickAgent.setDebugMode(true);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    private void restoreUserInfo() {
        if (userLoginRequestBean.isKeepPassword) {
            String account = PreferenceManager.getStringValue(SunApplication.getInstance(), Constants.SP_KEY_USER_ACCOUNT, "");
            String password = PreferenceManager.getStringValue(SunApplication.getInstance(), Constants.SP_KEY_USER_PASSWORD, "");
            if (!TextUtils.isEmpty(account)) {
                userLoginRequestBean.account = account;
            }
            if (!TextUtils.isEmpty(password)) {
                userLoginRequestBean.password = password;
            }
        }
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        loginDataBinding = (ActivityUserLoginBinding) binding;
        loginDataBinding.setListener(this);
        loginDataBinding.setVariable(BR.requestInfo, userLoginRequestBean);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getViewId() {
        return R.layout.activity_user_login;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_act_tv_startlogin:
                startLogin();
                break;
        }
    }

    private void startLogin() {
        if (checkLoginInfo()) {
            loginLoadingDialog.show();
            String md5Password = FileUtils.computeMD5(userLoginRequestBean.password).toUpperCase();
            userLoginPresenter.loginAccount(userLoginRequestBean.account, md5Password);
        }
    }

    private void saveUserInfo(UserBean userBean) {
        SunApplication.getInstance().setToken(userBean.token);
        PreferenceManager.setBooleanValue(SunApplication.getInstance(), Constants.SP_KEY_ISKEEPPASSWORD, userLoginRequestBean.isKeepPassword);
        PreferenceManager.setStringValue(SunApplication.getInstance(), Constants.SP_KEY_USER_PASSWORD, userLoginRequestBean.password);
        PreferenceManager.setStringValue(SunApplication.getInstance(), Constants.SP_KEY_USER_ACCOUNT, userLoginRequestBean.account);
    }

    private boolean checkLoginInfo() {
        if (TextUtils.isEmpty(userLoginRequestBean.account)) {
            CommonNotices.show(getString(R.string.account_format_error_tips));
            return false;
        } else if (TextUtils.isEmpty(userLoginRequestBean.password)) {
            CommonNotices.show(getString(R.string.password_format_error_tips));
            return false;
        }

        return true;
    }

    private void skipToMainActivity() {
        ManagerActivityUtils.startMainActivity(LoginActivity.this);
    }

    @Override
    public void onLoginSucceed(UserBean userBean) {
        dismissLoadDialog();
        saveUserInfo(userBean);
        skipToMainActivity();
        finish();
    }

    private void dismissLoadDialog() {
        if (null != loginLoadingDialog && loginLoadingDialog.isShowing()) {
            loginLoadingDialog.dismiss();
        }
    }

    @Override
    public void onLoginFailed(int errorCode, String msg) {
        dismissLoadDialog();
    }

    @Override
    public void onLoginError(String error) {
        dismissLoadDialog();
        CommonNotices.show(error);
    }

    @Override
    public void onLoginException(Throwable e) {
        dismissLoadDialog();
        if (e instanceof ConnectException) {
            CommonNotices.show(getString(R.string.common_tips_network_connection_exception));
        } else {
            CommonNotices.show(getString(R.string.common_tips_request_failed));
        }
    }
}

