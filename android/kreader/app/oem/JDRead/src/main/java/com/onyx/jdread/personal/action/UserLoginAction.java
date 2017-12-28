package com.onyx.jdread.personal.action;

import android.content.Context;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.ClientUtils;
import com.onyx.jdread.common.CommonUtils;
import com.onyx.jdread.common.Constants;
import com.onyx.jdread.common.ManagerActivityUtils;
import com.onyx.jdread.common.ToastUtil;
import com.onyx.jdread.personal.cloud.entity.jdbean.SyncLoginInfoBean;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.personal.event.UserLoginResultEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;

import org.greenrobot.eventbus.EventBus;

import jd.wjlogin_sdk.common.WJLoginHelper;
import jd.wjlogin_sdk.common.listener.OnLoginCallback;
import jd.wjlogin_sdk.model.FailResult;
import jd.wjlogin_sdk.model.JumpResult;
import jd.wjlogin_sdk.model.PicDataInfo;
import jd.wjlogin_sdk.util.MD5;

/**
 * Created by jackdeng on 2017/12/26.
 */

public class UserLoginAction extends BaseAction {
    private Context context;
    private String userName;
    private String password;

    public UserLoginAction(Context context, String userName, String password) {
        this.context = context;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public void execute(PersonalDataBundle dataBundle, RxCallback rxCallback) {
        checkLoginInfo(dataBundle);
    }

    private void checkLoginInfo(PersonalDataBundle dataBundle) {
        if (StringUtils.isNullOrEmpty(userName)) {
            ToastUtil.showToast(JDReadApplication.getInstance(), JDReadApplication.getInstance().getString(R.string.check_user_name));
            return;
        }
        if (password == null || password.length() < Constants.PASSWORD_MIN_LENGTH) {
            ToastUtil.showToast(JDReadApplication.getInstance(), JDReadApplication.getInstance().getString(R.string.check_password));
            return;
        }
        if (!CommonUtils.isNetworkConnected(context)) {
            ManagerActivityUtils.showWifiDialog(context);
            return;
        }
        userLogin(dataBundle, userName, MD5.encrypt32(password));
    }

    public void userLogin(final PersonalDataBundle dataBundle, final String userName, final String password) {
        final WJLoginHelper helper = ClientUtils.getWJLoginHelper();
        helper.JDLoginWithPassword(userName, password, null, true, new OnLoginCallback() {
            @Override
            public void onSuccess() {
                PreferenceManager.setStringValue(JDReadApplication.getInstance(), Constants.SP_KEY_ACCOUNT, userName);
                PreferenceManager.setStringValue(JDReadApplication.getInstance(), Constants.SP_KEY_PASSWORD, password);

                LoginHelper loginHelper = new LoginHelper();
                loginHelper.getUserInfo(helper.getPin(), dataBundle);

                UserSyncLoginInfoAction userSyncLoginInfoAction = new UserSyncLoginInfoAction();
                userSyncLoginInfoAction.execute(dataBundle, new RxCallback<UserSyncLoginInfoAction>() {
                    @Override
                    public void onNext(UserSyncLoginInfoAction userSyncLoginInfoAction) {
                        SyncLoginInfoBean syncLoginInfoBean = userSyncLoginInfoAction.getSyncLoginInfoBean();
                        if (syncLoginInfoBean != null) {
                            onSyncLoginInfo(dataBundle,syncLoginInfoBean);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }
                });
            }

            @Override
            public void onError(String s) {
                EventBus.getDefault().post(new UserLoginResultEvent(s));
            }

            @Override
            public void onFail(FailResult failResult, PicDataInfo picDataInfo) {
                dataBundle.getEventBus().post(new UserLoginResultEvent(failResult.getMessage()));
            }

            @Override
            public void onFail(FailResult failResult, JumpResult jumpResult, PicDataInfo picDataInfo) {
                dataBundle.getEventBus().post(new UserLoginResultEvent(failResult.getMessage()));
            }
        });
    }

    public void onSyncLoginInfo(PersonalDataBundle dataBundle, SyncLoginInfoBean syncLoginInfoBean) {
        if (Constants.LOGIN_CODE_SUCCESS.equals(syncLoginInfoBean.getCode())) {
            dataBundle.getEventBus().post(new UserLoginResultEvent(JDReadApplication.getInstance().getString(R.string.login_success)));
        } else {
            dataBundle.getEventBus().post(new UserLoginResultEvent(JDReadApplication.getInstance().getString(R.string.login_fail)));
        }
    }
}
