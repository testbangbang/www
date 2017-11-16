package com.onyx.android.plato.cloud.bean;

import android.databinding.BaseObservable;

/**
 * Created by jackdeng on 2017/10/26.
 */

public class ChangePasswordRequestBean extends BaseObservable{
    public String account;
    public String oldPassword;
    public String newPpassword;
    public String finalPassword;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
        notifyChange();
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
        notifyChange();
    }

    public String getNewPpassword() {
        return newPpassword;
    }

    public void setNewPpassword(String newPpassword) {
        this.newPpassword = newPpassword;
        notifyChange();
    }

    public String getFinalPassword() {
        return finalPassword;
    }

    public void setFinalPassword(String finalPassword) {
        this.finalPassword = finalPassword;
        notifyChange();
    }
}
