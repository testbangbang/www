package com.onyx.android.plato.interfaces;

/**
 * Created by jackdeng on 2017/10/26.
 */

public interface ChangePasswordView {
    void onChangePasswordSucced();
    void onChangePasswordFailed(int errorCode, String msg);
    void onChangePasswordError(Throwable throwable);
}
