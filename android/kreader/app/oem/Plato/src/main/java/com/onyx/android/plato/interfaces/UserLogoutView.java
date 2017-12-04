package com.onyx.android.plato.interfaces;

import com.onyx.android.plato.cloud.bean.UserInfoBean;

/**
 * Created by jackdeng on 2017/10/24.
 */

public interface UserLogoutView {
    void onLogoutSucceed();

    void onLogoutFailed(int errorCode, String msg);

    void onLogoutError(Throwable throwable);

    void setUserInfo(UserInfoBean data);
}
