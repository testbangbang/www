package com.onyx.android.sun.cloud.bean;

import android.databinding.BaseObservable;

/**
 * Created by jackdeng on 2017/10/21.
 */

public class UserLoginRequestBean extends BaseObservable{
    public String account;
    public String password;
    public boolean isKeepPassword;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
        notifyChange();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyChange();
    }

    public boolean getIsKeepPassword() {
        return isKeepPassword;
    }

    public void setIsKeepPassword(boolean isKeepPassword) {
        this.isKeepPassword = isKeepPassword;
        notifyChange();
    }
}
