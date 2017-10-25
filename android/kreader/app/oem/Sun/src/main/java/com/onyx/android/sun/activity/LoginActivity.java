package com.onyx.android.sun.activity;

import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;

import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sun.BR;
import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.UserInfoBean;
import com.onyx.android.sun.cloud.bean.UserLoginRequestBean;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.common.ManagerActivityUtils;
import com.onyx.android.sun.databinding.ActivityUserLoginBinding;
import com.onyx.android.sun.interfaces.UserLoginView;
import com.onyx.android.sun.presenter.UserLoginPresenter;

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
        userLoginRequestBean.isKeepPassword = PreferenceManager.getBooleanValue(SunApplication.getInstance(),Constants.SP_KEY_ISKEEPPASSWORD, false);
        restoreUserInfo();
        loginLoadingDialog = new DialogLoading(LoginActivity.this,getString(R.string.login_activity_loading_tip),false);
    }

    private void restoreUserInfo() {
        if (userLoginRequestBean.isKeepPassword){
            String account = PreferenceManager.getStringValue(SunApplication.getInstance(), Constants.SP_KEY_USER_ACCOUNT, "");
            String password = PreferenceManager.getStringValue(SunApplication.getInstance(),Constants.SP_KEY_USER_PASSWORD,"");
            if (!TextUtils.isEmpty(account)){
                userLoginRequestBean.account = account;
            }
            if (!TextUtils.isEmpty(password)){
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
    protected void initListener() {

    }

    @Override
    protected int getViewId() {
        return R.layout.activity_user_login;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_act_tv_startlogin:
                startLogin();
                break;
        }
    }

    private void startLogin() {
        if (checkLoginInfo()){
            loginLoadingDialog.show();
            String md5Password = FileUtils.computeMD5(userLoginRequestBean.password);
            userLoginPresenter.loginAccount(userLoginRequestBean.account, md5Password);
        }
    }

    private void saveUserInfo(UserInfoBean userInfoBean) {
        PreferenceManager.setBooleanValue(SunApplication.getInstance(),Constants.SP_KEY_ISKEEPPASSWORD, userLoginRequestBean.isKeepPassword);
        if (userLoginRequestBean.isKeepPassword){
            PreferenceManager.setStringValue(SunApplication.getInstance(),Constants.SP_KEY_USER_PASSWORD, userLoginRequestBean.password);
        } else {
            PreferenceManager.setStringValue(SunApplication.getInstance(),Constants.SP_KEY_USER_PASSWORD, null);
        }

        PreferenceManager.setStringValue(SunApplication.getInstance(),Constants.SP_KEY_USER_ACCOUNT, userInfoBean.account);
        PreferenceManager.setStringValue(SunApplication.getInstance(),Constants.SP_KEY_USER_NAME, userInfoBean.name);
        PreferenceManager.setStringValue(SunApplication.getInstance(),Constants.SP_KEY_USER_PHONE_NUMBER, userInfoBean.phoneNumber);

    }

    private boolean checkLoginInfo() {
        if (TextUtils.isEmpty(userLoginRequestBean.account)){
            CommonNotices.show(getString(R.string.login_activity_tip_account_error));
            return false;
        } else if(TextUtils.isEmpty(userLoginRequestBean.password)){
            CommonNotices.show(getString(R.string.login_activity_tip_password_error));
            return false;
        }

        return true;
    }

    private void skipToMainActivity() {
        ManagerActivityUtils.startMainActivity(LoginActivity.this);
    }

    @Override
    public void onLoginSucced(UserInfoBean userInfoBean) {
        dissmisLoadDialog();
        skipToMainActivity();
        saveUserInfo(userInfoBean);
        finish();
    }

    private void dissmisLoadDialog() {
        if (null != loginLoadingDialog && loginLoadingDialog.isShowing()){
            loginLoadingDialog.dismiss();
        }
    }

    @Override
    public void onLoginFailed(int errorCode, String msg) {
        dissmisLoadDialog();
    }

    @Override
    public void onLoginError(Throwable throwable) {
        dissmisLoadDialog();
        if (null != throwable){
            if (throwable instanceof ConnectException){
                CommonNotices.show(getString(R.string.login_activity_network_connection_exception));
            } else {
                CommonNotices.show(getString(R.string.login_activity_request_failed));
            }
        }
    }
}

