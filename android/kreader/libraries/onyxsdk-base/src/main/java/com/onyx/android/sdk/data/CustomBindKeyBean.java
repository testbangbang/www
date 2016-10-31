package com.onyx.android.sdk.data;

import android.view.KeyEvent;

/**
 * Created by solskjaer49 on 15/2/12 14:59.
 */
public class CustomBindKeyBean {

    public CustomBindKeyBean() {
    }

    public CustomBindKeyBean(String args, String action) {
        this.action = action;
        this.args = args;
    }

    private String action,args;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public static CustomBindKeyBean createKeyBean(String args, String action){
        return new CustomBindKeyBean(args, action);
    }

    public static CustomBindKeyBean createKeyBean(String args, int keyCode){
        return new CustomBindKeyBean(args, KeyEvent.keyCodeToString(keyCode));
    }
}
