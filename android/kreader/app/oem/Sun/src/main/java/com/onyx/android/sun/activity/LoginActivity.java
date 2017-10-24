package com.onyx.android.sun.activity;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;

import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.android.sun.BR;
import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.UserInfoBean;
import com.onyx.android.sun.cloud.bean.UserLoginRequestBean;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.databinding.ActivityUserLoginBinding;
import com.onyx.android.sun.interfaces.UserLoginView;
import com.onyx.android.sun.presenter.UserLoginPresenter;
import com.onyx.android.sun.utils.MD5Utils;
import com.onyx.android.sun.utils.SharedPreferencesUtil;

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
        userLoginRequestBean.isKeepPassword = SharedPreferencesUtil.getValue(Constants.SP_NAME_USERINFO, Constants.SP_KEY_ISKEEPPASSWORD, false);

        restoreUserInfo();
        loginLoadingDialog = new DialogLoading(LoginActivity.this,getString(R.string.login_activity_loading_tip),false);
    }

    private void restoreUserInfo() {
        if (userLoginRequestBean.isKeepPassword){
            String account = SharedPreferencesUtil.getValue(Constants.SP_NAME_USERINFO, Constants.SP_KEY_USER_ACCOUNT, "");
            String password = SharedPreferencesUtil.getValue(Constants.SP_NAME_USERINFO,Constants.SP_KEY_USER_PASSWORD,"");
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
            userLoginPresenter.loginAccount(userLoginRequestBean.account, MD5Utils.encode(userLoginRequestBean.password));
        }
    }

    private void saveUserInfo() {
        SharedPreferencesUtil.putValue(Constants.SP_NAME_USERINFO,Constants.SP_KEY_ISKEEPPASSWORD, userLoginRequestBean.isKeepPassword);
        if (userLoginRequestBean.isKeepPassword){
            SharedPreferencesUtil.putValue(Constants.SP_NAME_USERINFO,Constants.SP_KEY_USER_ACCOUNT, userLoginRequestBean.account);
            SharedPreferencesUtil.putValue(Constants.SP_NAME_USERINFO,Constants.SP_KEY_USER_PASSWORD, userLoginRequestBean.password);
        } else {
            SharedPreferencesUtil.cleanValueByKey(Constants.SP_NAME_USERINFO,Constants.SP_KEY_USER_ACCOUNT);
            SharedPreferencesUtil.cleanValueByKey(Constants.SP_NAME_USERINFO,Constants.SP_KEY_USER_PASSWORD);
        }
    }

    private boolean checkLoginInfo() {
        if (TextUtils.isEmpty(userLoginRequestBean.account)){
            CommonNotices.show(getString(R.string.login_act_tip_account_error));
            return false;
        } else if(TextUtils.isEmpty(userLoginRequestBean.password)){
            CommonNotices.show(getString(R.string.login_act_tip_password_error));
            return false;
        }

        return true;
    }

    private void skipToMainActivity() {
        Intent intent = new Intent(SunApplication.getInstance(),MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLoginSucced(UserInfoBean userInfoBean) {
        dissmisLoadDialog();
        skipToMainActivity();
        saveUserInfo();
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
}

