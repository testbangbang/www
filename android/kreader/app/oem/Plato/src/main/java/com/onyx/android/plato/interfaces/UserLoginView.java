package com.onyx.android.plato.interfaces;

import com.onyx.android.plato.cloud.bean.UserInfoBean;

/**
 * Created by jackdeng on 2017/10/23.
 */

public interface UserLoginView {
    void onLoginSucced(UserInfoBean userInfoBean);
    void onLoginFailed(int errorCode, String msg);
    void onLoginError(Throwable throwable);
}
