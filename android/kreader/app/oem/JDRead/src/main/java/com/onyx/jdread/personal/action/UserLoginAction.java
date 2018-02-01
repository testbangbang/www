package com.onyx.jdread.personal.action;

import android.content.Context;

import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ClientUtils;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.manager.ManagerActivityUtils;
import com.onyx.jdread.personal.cloud.entity.jdbean.SignForVoucherBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.SyncLoginInfoBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserLoginResultErrorBean;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.personal.event.UserLoginResultEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.util.TimeUtils;

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
    private boolean isAuto;
    private Context context;
    private String account;
    private String password;
    private RxCallback rxCallback;

    public UserLoginAction(Context context, String account, String password, boolean isAuto) {
        this.context = context;
        this.account = account;
        this.password = password;
        this.isAuto = isAuto;
    }

    @Override
    public void execute(PersonalDataBundle dataBundle, RxCallback rxCallback) {
        this.rxCallback = rxCallback;
        if (isAuto) {
            userLogin(dataBundle, account, password);
        } else {
            checkLoginInfo(dataBundle);
        }
    }

    private void checkLoginInfo(PersonalDataBundle dataBundle) {
        if (StringUtils.isNullOrEmpty(account)) {
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
        userLogin(dataBundle, account, MD5.encrypt32(password));
    }

    public void userLogin(final PersonalDataBundle dataBundle, final String account, final String password) {
        final WJLoginHelper helper = ClientUtils.getWJLoginHelper();
        helper.JDLoginWithPassword(account, password, null, true, new OnLoginCallback() {
            @Override
            public void onSuccess() {
                JDPreferenceManager.setStringValue(Constants.SP_KEY_ACCOUNT, account);
                JDPreferenceManager.setStringValue(Constants.SP_KEY_PASSWORD, password);
                syncServiceInfo(dataBundle);
            }

            @Override
            public void onError(String errorJson) {
                try {
                    UserLoginResultErrorBean resultErrorBean = JSONObjectParseUtils.parseObject(errorJson, UserLoginResultErrorBean.class);
                    if (resultErrorBean != null) {
                        dataBundle.getEventBus().post(new UserLoginResultEvent(resultErrorBean.errMsg));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    private void syncServiceInfo(final PersonalDataBundle dataBundle) {
        UserSyncLoginInfoAction userSyncLoginInfoAction = new UserSyncLoginInfoAction();
        userSyncLoginInfoAction.execute(dataBundle, new RxCallback<UserSyncLoginInfoAction>() {
            @Override
            public void onNext(UserSyncLoginInfoAction userSyncLoginInfoAction) {
                SyncLoginInfoBean syncLoginInfoBean = userSyncLoginInfoAction.getSyncLoginInfoBean();
                if (syncLoginInfoBean != null) {
                    onSyncLoginInfo(dataBundle, syncLoginInfoBean);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    private void onSyncLoginInfo(PersonalDataBundle dataBundle, SyncLoginInfoBean syncLoginInfoBean) {
        String code = syncLoginInfoBean.getCode();
        if (Constants.RESULT_CODE_SUCCESS.equals(code)) {
            LoginHelper.getUserInfo(dataBundle);
            autoSign();
            dataBundle.getEventBus().post(new UserLoginResultEvent(JDReadApplication.getInstance().getString(R.string.login_success)));
            if (rxCallback != null) {
                rxCallback.onNext(UserLoginAction.class);
            }
        } else {
            String errorMsg = ToastUtil.getErrorMsgByCode(code);
            dataBundle.getEventBus().post(new UserLoginResultEvent(errorMsg));
        }
    }

    private void autoSign() {
        String saveTime = PersonalDataBundle.getInstance().getCurrentDay();
        if (StringUtils.isNullOrEmpty(saveTime) || !TimeUtils.getCurrentDataInString().equals(saveTime)) {
            final SignForVoucherAction signForVoucherAction = new SignForVoucherAction();
            signForVoucherAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
                @Override
                public void onNext(Object o) {
                    SignForVoucherBean resultBean = signForVoucherAction.getResultBean();
                    if (resultBean.result_code == 0) {
                        PersonalDataBundle.getInstance().setCurrentDay(TimeUtils.getCurrentDataInString());
                    }
                }
            });
        }
    }
}
