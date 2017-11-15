package com.onyx.android.plato.bean;

import android.databinding.BaseObservable;

/**
 * Created by hehai on 17-9-29.
 */

public class User extends BaseObservable {
    private String userName;
    private String userID;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
