package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;

import com.onyx.android.plato.cloud.bean.UserInfo;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.plato.BR;
import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.common.Constants;
import com.onyx.android.plato.common.ManagerActivityUtils;
import com.onyx.android.plato.databinding.FragmentUserCenterBinding;
import com.onyx.android.plato.event.OnBackPressEvent;
import com.onyx.android.plato.event.ToChangePasswordEvent;
import com.onyx.android.plato.interfaces.UserLogoutView;
import com.onyx.android.plato.presenter.UserCenterPresenter;

import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;

/**
 * Created by jackdeng on 2017/10/25.
 */

public class UserCenterFragment extends BaseFragment implements UserLogoutView, View.OnClickListener {
    private UserInfo userInfoBean = new UserInfo();
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
        EventBus.getDefault().post(new OnBackPressEvent(ChildViewID.FRAGMENT_USER_CENTER));
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
