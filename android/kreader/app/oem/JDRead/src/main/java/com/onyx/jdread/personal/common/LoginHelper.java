package com.onyx.jdread.personal.common;


import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.common.Constants;
import com.onyx.jdread.databinding.DialogUserLoginBinding;
import com.onyx.jdread.personal.action.UserInfoAction;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfoBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.UserLoginViewModel;

/**
 * Created by jackdeng on 2017/12/26.
 */

public class LoginHelper {
    private UserInfoAction userInfoAction;
    private static AlertDialog userLoginDialog;

    public void getUserInfo(final String pin, PersonalDataBundle dataBundle) {
        userInfoAction = new UserInfoAction(pin);
        userInfoAction.execute(dataBundle, new RxCallback<UserInfoAction>() {
            @Override
            public void onNext(UserInfoAction userInfoAction) {
                UserInfoBean userInfoBean = userInfoAction.getUserInfoData();
                if (userInfoBean != null && userInfoBean.getList() != null && userInfoBean.getList().size() > 0) {
                    String nickName = userInfoBean.getList().get(0).getNickName();
                    String imgUrl = userInfoBean.getList().get(0).getYunBigImageUrl();
                    String pin = userInfoBean.getList().get(0).getPin();
                    setUserInfo(nickName, imgUrl, pin);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }

        });
    }

    private void setUserInfo(String nickName, String imgUrl, String userName) {
        PreferenceManager.setStringValue(JDReadApplication.getInstance(), Constants.SP_KEY_USER_NICK_NAME, nickName);
        PreferenceManager.setStringValue(JDReadApplication.getInstance(), Constants.SP_KEY_USER_IMAGE_URL, imgUrl);
        PreferenceManager.setStringValue(JDReadApplication.getInstance(), Constants.SP_KEY_USER_NAME, userName);
    }

    public static String getNickName() {
        return PreferenceManager.getStringValue(JDReadApplication.getInstance(), Constants.SP_KEY_USER_NICK_NAME, "");
    }

    public static String getImgUrl() {
        return PreferenceManager.getStringValue(JDReadApplication.getInstance(), Constants.SP_KEY_USER_IMAGE_URL, "");
    }

    public static String getUserName() {
        return PreferenceManager.getStringValue(JDReadApplication.getInstance(), Constants.SP_KEY_USER_NAME, "");
    }

    public static void showUserLoginDialog(final Activity activity, final UserLoginViewModel userLoginViewModel) {
        final DialogUserLoginBinding userLoginBinding = DialogUserLoginBinding.inflate(LayoutInflater.from(activity), null, false);
        userLoginBinding.setLoginViewModel(userLoginViewModel);
        if (userLoginDialog == null) {
            final AlertDialog.Builder userLoginDialogBuild = new AlertDialog.Builder(activity);
            userLoginDialogBuild.setView(userLoginBinding.getRoot());
            userLoginDialogBuild.setCancelable(true);
            userLoginDialog = userLoginDialogBuild.create();
            boolean showPassword = PreferenceManager.getBooleanValue(JDReadApplication.getInstance(), Constants.SP_KEY_SHOW_PASSWORD, false);
            userLoginViewModel.isShowPassword.set(showPassword);
            userLoginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    userLoginViewModel.cleanInput();
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
