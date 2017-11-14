package com.onyx.android.plato.interfaces;

/**
 * Created by jackdeng on 2017/10/24.
 */

public interface UserLogoutView {
    void onLogoutSucced();
    void onLogoutFailed(int errorCode, String msg);
    void onLogoutError(Throwable throwable);
}
