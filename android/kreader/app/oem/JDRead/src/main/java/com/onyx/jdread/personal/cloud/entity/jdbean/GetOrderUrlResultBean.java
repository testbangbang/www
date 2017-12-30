package com.onyx.jdread.personal.cloud.entity.jdbean;

/**
 * Created by li on 2017/12/30.
 */

public class GetOrderUrlResultBean {
    private int code;
    private String tokenKey;
    private String url;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
