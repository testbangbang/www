package com.onyx.android.plato.interfaces;

import com.onyx.android.plato.cloud.bean.UserBean;

/**
 * Created by jackdeng on 2017/10/23.
 */

public interface UserLoginView {
    void onLoginSucceed(UserBean userBean);

    void onLoginFailed(int errorCode, String msg);

    void onLoginError(String error);

    void onLoginException(Throwable e);
}
