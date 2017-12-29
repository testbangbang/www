package com.onyx.jdread.personal.cloud.entity;


import com.onyx.jdread.common.AppBaseInfo;

/**
 * Created by 12 on 2017/3/31.
 */

public class UserInfoRequestBean {
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
