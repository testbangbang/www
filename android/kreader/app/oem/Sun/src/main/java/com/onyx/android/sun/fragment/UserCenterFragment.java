package com.onyx.android.sun.fragment;

import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;

import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sun.BR;
import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.UserInfoBean;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.databinding.ActivityUserCenterBinding;
import com.onyx.android.sun.event.ToMainFragmentEvent;
import com.onyx.android.sun.interfaces.UserLogoutView;
import com.onyx.android.sun.presenter.UserCenterPresenter;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/10/25.
 */

public class UserCenterFragment extends BaseFragment implements UserLogoutView, View.OnClickListener {

    private UserInfoBean userInfoBean = new UserInfoBean();
    private UserCenterPresenter userCenterPresenter;
    private ActivityUserCenterBinding userCenterBinding;

    @Override
    protected void loadData() {
        restoreUserInfo();
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        userCenterPresenter = new UserCenterPresenter(this);
        userCenterBinding = (ActivityUserCenterBinding) binding;
        userCenterBinding.setListener(this);
        userCenterBinding.setVariable(BR.userInfo,userInfoBean);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_user_center;
    }

    private void restoreUserInfo() {
        String account = PreferenceManager.getStringValue(SunApplication.getInstance(), Constants.SP_KEY_USER_ACCOUNT, "");
        String name = PreferenceManager.getStringValue(SunApplication.getInstance(),Constants.SP_KEY_USER_NAME,"");
        String phoneNumber = PreferenceManager.getStringValue(SunApplication.getInstance(),Constants.SP_KEY_USER_PHONE_NUMBER,"");
        if (!TextUtils.isEmpty(account)){
            userInfoBean.account = account;
        }
        if (!TextUtils.isEmpty(name)){
            userInfoBean.name = name;
        }
        if (!TextUtils.isEmpty(phoneNumber)){
            userInfoBean.phoneNumber = phoneNumber;
        }
    }

    @Override
    public void onLogoutSucced() {

    }

    @Override
    public void onLogoutFailed(int errorCode, String msg) {

    }

    @Override
    public void onLogoutError(Throwable throwable) {

    }

    @Override
    public boolean onKeyBack() {
        EventBus.getDefault().post(new ToMainFragmentEvent());
        return true;
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.ll_main_activity_user_center:
                onKeyBack();
                break;
        }*/
    }
}
