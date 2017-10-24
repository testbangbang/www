package com.onyx.android.sun.interfaces;

import com.onyx.android.sun.cloud.bean.UserInfoBean;

/**
 * Created by jackdeng on 2017/10/23.
 */

public interface UserLoginView {
    void onLoginSucced(UserInfoBean userInfoBean);
    void onLoginFailed(int errorCode, String msg);
    void onLoginError(Throwable throwable);
}
