package com.onyx.android.plato.cloud.bean;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;

/**
 * Created by jackdeng on 2017/10/26.
 */

public class ChangePasswordRequestBean extends BaseObservable{
    public String oldPassword;
    public String newPassword;
    public String finalPassword;
    public final ObservableBoolean isShowOldPassword = new ObservableBoolean();
    public final ObservableBoolean isShowNewPassword = new ObservableBoolean();
    public final ObservableBoolean isShowFinalPassword = new ObservableBoolean();

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
        notifyChange();
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
        notifyChange();
    }

    public String getFinalPassword() {
        return finalPassword;
    }

    public void setFinalPassword(String finalPassword) {
        this.finalPassword = finalPassword;
        notifyChange();
    }

    public void clear() {
        oldPassword = "";
        newPassword = "";
        finalPassword = "";
        notifyChange();
    }
}
