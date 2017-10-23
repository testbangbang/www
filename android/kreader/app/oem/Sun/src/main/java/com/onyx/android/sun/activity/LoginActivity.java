package com.onyx.android.sun.activity;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;

import com.onyx.android.sun.BR;
import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.UserInfoBean;
import com.onyx.android.sun.cloud.bean.UserLoginRequestBean;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.databinding.ActivityUserloginBinding;
import com.onyx.android.sun.interfaces.UserLoginView;
import com.onyx.android.sun.presenter.UserLoginPresenter;
import com.onyx.android.sun.utils.MD5Utils;
import com.onyx.android.sun.utils.SharedPreferencesUtil;

import static com.onyx.android.sun.utils.SharedPreferencesUtil.getValue;

/**
 * Created by jackdeng on 2017/10/23.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, UserLoginView {

    private ActivityUserloginBinding mDataBinding;
    private UserLoginPresenter mUserLoginPresenter;
    private UserLoginRequestBean mRequestBean = new UserLoginRequestBean();

    @Override
    protected void initData() {
        mUserLoginPresenter = new UserLoginPresenter(this);
        mRequestBean.isKeepPsd = getValue(Constants.SP_NAME_USERINFO, Constants.SP_KEY_ISKEEPPASSWORD, false);

        restoreUserInfo();
    }

    private void restoreUserInfo() {
        if (mRequestBean.isKeepPsd){
            String account = getValue(Constants.SP_NAME_USERINFO, Constants.SP_KEY_USER_ACCOUNT, "");
            String password = SharedPreferencesUtil.getValue(Constants.SP_NAME_USERINFO,Constants.SP_KEY_USER_PASSWORD,"");
            if (!TextUtils.isEmpty(account))
                mRequestBean.account = account;
            if (!TextUtils.isEmpty(password))
                mRequestBean.password = password;
        }
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        mDataBinding = (ActivityUserloginBinding) binding;
        mDataBinding.setListener(this);
        mDataBinding.setVariable(BR.requestInfo, mRequestBean);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getViewId() {
        return R.layout.activity_userlogin;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_act_tv_startlogin:
                if (checkLoginInfo())
                    mUserLoginPresenter.loginAccount(mRequestBean.account, MD5Utils.encode(mRequestBean.password));
                break;
        }
    }

    private void saveUserInfo() {
        SharedPreferencesUtil.putValue(Constants.SP_NAME_USERINFO,Constants.SP_KEY_ISKEEPPASSWORD,mRequestBean.isKeepPsd);
        if (mRequestBean.isKeepPsd){
            SharedPreferencesUtil.putValue(Constants.SP_NAME_USERINFO,Constants.SP_KEY_USER_ACCOUNT,mRequestBean.account);
            SharedPreferencesUtil.putValue(Constants.SP_NAME_USERINFO,Constants.SP_KEY_USER_PASSWORD,mRequestBean.password);
        } else {
            SharedPreferencesUtil.cleanValueByKey(Constants.SP_NAME_USERINFO,Constants.SP_KEY_USER_ACCOUNT);
            SharedPreferencesUtil.cleanValueByKey(Constants.SP_NAME_USERINFO,Constants.SP_KEY_USER_PASSWORD);
        }
    }

    private boolean checkLoginInfo() {
        if (TextUtils.isEmpty(mRequestBean.account)){
            CommonNotices.show(getString(R.string.login_act_tip_account_error));
            return false;
        } else if(TextUtils.isEmpty(mRequestBean.password)){
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
        skipToMainActivity();
        saveUserInfo();
        finish();
    }

    @Override
    public void onLoginFailed(int errorCode, String msg) {

    }
}

