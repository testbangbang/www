package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.ChangePasswordRequestBean;
import com.onyx.android.plato.cloud.bean.UserBean;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.common.Constants;
import com.onyx.android.plato.databinding.FragmentChangePasswordBinding;
import com.onyx.android.plato.event.ToUserCenterEvent;
import com.onyx.android.plato.interfaces.ChangePasswordView;
import com.onyx.android.plato.interfaces.UserLoginView;
import com.onyx.android.plato.presenter.ChangePasswordPresenter;
import com.onyx.android.plato.presenter.UserLoginPresenter;
import com.onyx.android.plato.utils.StringUtil;
import com.onyx.android.plato.utils.Utils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.PreferenceManager;

import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;

/**
 * Created by jackdeng on 2017/10/26.
 */

public class ChangePasswordFragment extends BaseFragment implements ChangePasswordView, View.OnClickListener, UserLoginView {
    private ChangePasswordPresenter changePasswordPresenter;
    private FragmentChangePasswordBinding changePasswordBinding;
    private ChangePasswordRequestBean changePasswordRequestBean = new ChangePasswordRequestBean();
    private UserLoginPresenter userLoginPresenter;
    private String newPassword;

    @Override
    protected void loadData() {
        if (changePasswordPresenter == null || userLoginPresenter == null) {
            changePasswordPresenter = new ChangePasswordPresenter(this);
            userLoginPresenter = new UserLoginPresenter(this);
        }
        changePasswordRequestBean.clear();
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        changePasswordBinding = (FragmentChangePasswordBinding) binding;
        changePasswordBinding.setRequestInfo(changePasswordRequestBean);
        changePasswordBinding.setListener(this);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_change_password;
    }

    @Override
    public void onChangePasswordSucceed() {
        String account = PreferenceManager.getStringValue(SunApplication.getInstance(), Constants.SP_KEY_USER_ACCOUNT, "");
        if (!StringUtil.isNullOrEmpty(account)) {
            userLoginPresenter.loginAccount(account, newPassword);
        }
        CommonNotices.show(getString(R.string.change_password_success_tips));
        EventBus.getDefault().post(new ToUserCenterEvent());
        Utils.hideSoftWindow(getActivity());
    }

    @Override
    public void onChangePasswordFailed(String msg) {
        if(!TextUtils.isEmpty(msg)){
            CommonNotices.show(msg);
        }
    }

    @Override
    public void onChangePasswordError(Throwable throwable) {
        if (null != throwable){
            if (throwable instanceof ConnectException){
                CommonNotices.show(getString(R.string.common_tips_network_connection_exception));
            } else {
                CommonNotices.show(getString(R.string.common_tips_request_failed));
            }
        }
    }

    @Override
    public boolean onKeyBack() {
        EventBus.getDefault().post(new ToUserCenterEvent());
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_change_password_fragment_confirm:
                changePassword();
                break;
        }
    }

    private void changePassword() {
        if (checkRequestInfo()){
            String oldPassword = FileUtils.computeMD5(changePasswordRequestBean.oldPassword).toUpperCase();
            newPassword = FileUtils.computeMD5(changePasswordRequestBean.newPassword).toUpperCase();
            changePasswordPresenter.modifyPassword(oldPassword, newPassword);
        }
    }

    private boolean checkRequestInfo() {
        if (TextUtils.isEmpty(changePasswordRequestBean.oldPassword)){
            CommonNotices.show(getString(R.string.password_format_error_tips));
            return false;
        }
        if (TextUtils.isEmpty(changePasswordRequestBean.newPassword)){
            CommonNotices.show(getString(R.string.password_format_error_tips));
            return false;
        }
        if (TextUtils.isEmpty(changePasswordRequestBean.finalPassword)){
            CommonNotices.show(getString(R.string.password_format_error_tips));
            return false;
        }
        if (changePasswordRequestBean.newPassword.length() < 6) {
            CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.password_length_less_than_normal));
            return false;
        }
        if (!TextUtils.equals(changePasswordRequestBean.finalPassword,changePasswordRequestBean.newPassword)){
            CommonNotices.show(getString(R.string.change_password_passwords_match_error));
            return false;
        }

        String oldPassword = PreferenceManager.getStringValue(SunApplication.getInstance(), Constants.SP_KEY_USER_PASSWORD, "");
        if (!changePasswordRequestBean.oldPassword.equals(oldPassword)) {
            CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.verify_original_password_error));
            return false;
        }
        return true;
    }

    @Override
    public void onLoginSucceed(UserBean userBean) {
        SunApplication.getInstance().setToken(userBean.token);
        PreferenceManager.setBooleanValue(SunApplication.getInstance(), Constants.SP_KEY_ISKEEPPASSWORD, false);
        PreferenceManager.setStringValue(SunApplication.getInstance(), Constants.SP_KEY_USER_PASSWORD, changePasswordRequestBean.newPassword);
    }

    @Override
    public void onLoginFailed(int errorCode, String msg) {

    }

    @Override
    public void onLoginError(String error) {
        CommonNotices.show(error);
    }

    @Override
    public void onLoginException(Throwable e) {
        if (e instanceof ConnectException) {
            CommonNotices.show(getString(R.string.common_tips_network_connection_exception));
        } else {
            CommonNotices.show(getString(R.string.common_tips_request_failed));
        }
    }
}
