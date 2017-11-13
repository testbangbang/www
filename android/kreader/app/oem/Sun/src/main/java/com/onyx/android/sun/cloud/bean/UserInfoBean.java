package com.onyx.android.sun.cloud.bean;

import android.databinding.BaseObservable;

/**
 * Created by jackdeng on 2017/10/21.
 */

public class UserInfoBean  extends BaseObservable {
    public String account;
    public String name;
    public String phoneNumber;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
        notifyChange();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyChange();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyChange();
    }

}
