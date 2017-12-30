package com.onyx.jdread.personal.cloud.entity;

import com.onyx.jdread.main.common.AppBaseInfo;

/**
 * Created by li on 2017/12/30.
 */

public class GetOrderRequestBean {
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
