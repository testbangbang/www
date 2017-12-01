package com.onyx.android.plato.cloud.bean;

import android.databinding.BaseObservable;

/**
 * Created by li on 2017/12/1.
 */

public class UserInfo extends BaseObservable {
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
