package com.onyx.android.dr.bean;

/**
 * Created by zhouzhiming on 2017/8/30.
 */
public class CreateGroupResultBean {
    public String token;
    public long expires_in;

    public String getToken() {
        return token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }
}
