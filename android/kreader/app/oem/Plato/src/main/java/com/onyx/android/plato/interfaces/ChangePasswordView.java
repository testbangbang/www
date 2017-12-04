package com.onyx.android.plato.interfaces;

/**
 * Created by jackdeng on 2017/10/26.
 */

public interface ChangePasswordView {
    void onChangePasswordSucceed();
    void onChangePasswordFailed(String msg);
    void onChangePasswordError(Throwable throwable);
}
