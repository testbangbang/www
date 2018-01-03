package com.onyx.jdread.personal.cloud.entity;

import com.onyx.jdread.main.common.AppBaseInfo;

/**
 * Created by li on 2018/1/2.
 */

public class GetReadInfoRequestBean {
    private AppBaseInfo appBaseInfo;
    private String userName;

    public AppBaseInfo getAppBaseInfo() {
        return appBaseInfo;
    }

    public void setAppBaseInfo(AppBaseInfo appBaseInfo) {
        this.appBaseInfo = appBaseInfo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
