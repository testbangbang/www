package com.onyx.jdread.shop.cloud.entity;


import com.onyx.jdread.common.AppBaseInfo;

/**
 * Created by huxiaomao on 2016/12/19.
 */

public class BaseRequestBean {
    private AppBaseInfo appBaseInfo;
    private String body;

    public AppBaseInfo getAppBaseInfo() {
        return appBaseInfo;
    }

    public void setAppBaseInfo(AppBaseInfo appBaseInfo) {
        this.appBaseInfo = appBaseInfo;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}