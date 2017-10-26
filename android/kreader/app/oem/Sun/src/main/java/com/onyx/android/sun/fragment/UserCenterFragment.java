package com.onyx.android.sun.fragment;

import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;

import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sun.BR;
import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.UserInfoBean;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.common.ManagerActivityUtils;
import com.onyx.android.sun.databinding.FragmentUserCenterBinding;
import com.onyx.android.sun.event.ToChangePasswordEvent;
import com.onyx.android.sun.event.ToMainFragmentEvent;
import com.onyx.android.sun.interfaces.UserLogoutView;
import com.onyx.android.sun.presenter.UserCenterPresenter;

import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;

/**
 * Created by jackdeng on 2017/10/25.
 */

public class UserCenterFragment extends BaseFragment implements UserLogoutView, View.OnClickListener {

    private UserInfoBean userInfoBean = new UserInfoBean();
    private UserCenterPresenter userCenterPresenter;
    private FragmentUserCenterBinding userCenterBinding;
    private String account;

    @Override
    protected void loadData() {
        restoreUserInfo();
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        userCenterPresenter = new UserCenterPresenter(this);
        userCenterBinding = (FragmentUserCenterBinding) binding;
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
        account = PreferenceManager.getStringValue(SunApplication.getInstance(), Constants.SP_KEY_USER_ACCOUNT, "");
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
        if (null != getActivity()){
            ManagerActivityUtils.startLoginActivity(getActivity());
            getActivity().finish();
        }
    }

    @Override
    public void onLogoutFailed(int errorCode, String msg) {
        if(!TextUtils.isEmpty(msg)){
            CommonNotices.show(msg);
        }
    }

    @Override
    public void onLogoutError(Throwable throwable) {
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
        EventBus.getDefault().post(new ToMainFragmentEvent());
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user_center_fragment_phone_number_container:

                break;
            case R.id.tv_user_center_fragment_change_password:
                EventBus.getDefault().post(new ToChangePasswordEvent());
                break;
            case R.id.tv_user_center_fragment_logout:
                userCenterPresenter.logoutAccount(account);
                break;
        }
    }
}
