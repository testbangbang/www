package com.onyx.jdread.personal.common;


import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.databinding.DialogUserLoginBinding;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.personal.action.UserInfoAction;
import com.onyx.jdread.personal.action.VerifyCheckInAction;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfo;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfoBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.VerifySignBean;
import com.onyx.jdread.personal.event.UserInfoEvent;
import com.onyx.jdread.personal.event.VerifySignEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.UserLoginViewModel;

/**
 * Created by jackdeng on 2017/12/26.
 */

public class LoginHelper {
    private static AlertDialog userLoginDialog;

    public static void getUserInfo(final PersonalDataBundle dataBundle) {
        UserInfoAction userInfoAction = new UserInfoAction();
        userInfoAction.execute(dataBundle, new RxCallback<UserInfoAction>() {
            @Override
            public void onNext(UserInfoAction userInfoAction) {
                UserInfoBean userInfoBean = userInfoAction.getUserInfoData();
                UserInfo data = userInfoBean.data;
                if (data != null) {
                    setUserInfo(data.yun_big_image_url, data.nickname);
                    dataBundle.getEventBus().post(new UserInfoEvent(data));
                    verifySign(dataBundle);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }

        });
    }

    public static void verifySign(final PersonalDataBundle dataBundle) {
        final VerifyCheckInAction action = new VerifyCheckInAction();
        action.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                VerifySignBean.DataBean data = action.getData();
                if (data != null) {
                    dataBundle.getEventBus().post(new VerifySignEvent(data.isTodaySign()));
                }
            }
        });
    }

    private static void setUserInfo(String imgUrl, String userName) {
        JDPreferenceManager.setStringValue(Constants.SP_KEY_USER_IMAGE_URL, imgUrl);
        JDPreferenceManager.setStringValue(Constants.SP_KEY_USER_NAME, userName);
    }

    public static String getImgUrl() {
        return JDPreferenceManager.getStringValue(Constants.SP_KEY_USER_IMAGE_URL, "");
    }

    public static String getUserName() {
        return JDPreferenceManager.getStringValue(Constants.SP_KEY_USER_NAME, "");
    }

    public static void showUserLoginDialog(final Activity activity, final UserLoginViewModel userLoginViewModel) {
        final DialogUserLoginBinding userLoginBinding = DialogUserLoginBinding.inflate(LayoutInflater.from(activity), null, false);
        userLoginBinding.setLoginViewModel(userLoginViewModel);
        userLoginViewModel.setContext(activity);
        EncryptHelper.getSaltValue(PersonalDataBundle.getInstance(), null);
        if (userLoginDialog == null) {
            final AlertDialog.Builder userLoginDialogBuild = new AlertDialog.Builder(activity);
            userLoginDialogBuild.setView(userLoginBinding.getRoot());
            userLoginDialogBuild.setCancelable(true);
            userLoginDialog = userLoginDialogBuild.create();
            boolean showPassword = JDPreferenceManager.getBooleanValue(Constants.SP_KEY_SHOW_PASSWORD, false);
            userLoginViewModel.isShowPassword.set(showPassword);
            userLoginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    userLoginViewModel.cleanInput();
                }
            });
            userLoginBinding.setListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissUserLoginDialog();
                }
            });
        }
        if (userLoginDialog != null) {
            userLoginDialog.show();
        }
    }

    public static void dismissUserLoginDialog() {
        if (userLoginDialog != null && userLoginDialog.isShowing()) {
            userLoginDialog.dismiss();
        }
    }
}
