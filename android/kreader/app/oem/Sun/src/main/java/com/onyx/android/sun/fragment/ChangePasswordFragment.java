package com.onyx.android.sun.fragment;

import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;

import com.onyx.android.sun.R;
import com.onyx.android.sun.cloud.bean.ChangePasswordRequestBean;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.databinding.FragmentChangePasswordBinding;
import com.onyx.android.sun.event.ToUserCenterEvent;
import com.onyx.android.sun.interfaces.ChangePasswordView;
import com.onyx.android.sun.presenter.ChangePasswordPresenter;

import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;

/**
 * Created by jackdeng on 2017/10/26.
 */

public class ChangePasswordFragment extends BaseFragment implements ChangePasswordView, View.OnClickListener {

    private ChangePasswordPresenter changePasswordPresenter;
    private FragmentChangePasswordBinding changePasswordBinding;
    private ChangePasswordRequestBean changePasswordRequestBean = new ChangePasswordRequestBean();

    @Override
    protected void loadData() {

    }

    @Override
    protected void initView(ViewDataBinding binding) {
        changePasswordPresenter = new ChangePasswordPresenter(this);
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
    public void onChangePasswordSucced() {
        CommonNotices.show(getString(R.string.change_password_success_tips));
        EventBus.getDefault().post(new ToUserCenterEvent());
    }

    @Override
    public void onChangePasswordFailed(int errorCode, String msg) {
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
            changePasswordPresenter.changePassword(changePasswordRequestBean.account,changePasswordRequestBean.finalPassword);
        }
    }

    private boolean checkRequestInfo() {
        if (TextUtils.isEmpty(changePasswordRequestBean.account)){
            CommonNotices.show(getString(R.string.account_format_error_tips));
            return false;
        }
        if (TextUtils.isEmpty(changePasswordRequestBean.oldPassword)){
            CommonNotices.show(getString(R.string.password_format_error_tips));
            return false;
        }
        if (TextUtils.isEmpty(changePasswordRequestBean.newPpassword)){
            CommonNotices.show(getString(R.string.password_format_error_tips));
            return false;
        }
        if (TextUtils.isEmpty(changePasswordRequestBean.finalPassword)){
            CommonNotices.show(getString(R.string.password_format_error_tips));
            return false;
        }
        if (!TextUtils.equals(changePasswordRequestBean.finalPassword,changePasswordRequestBean.newPpassword)){
            CommonNotices.show(getString(R.string.change_password_passwords_match_error));
            return false;
        }


        return true;
    }

}
