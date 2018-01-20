package com.onyx.jdread.shop.cloud.entity;

import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by li on 2018/1/19.
 */

public class UpdateCartRequestBean {
    private JDAppBaseInfo baseInfo;
    private RequestBody body;

    public JDAppBaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public void setBody(RequestBody body) {
        this.body = body;
    }

    public RequestBody getBody() {
        return body;
    }
}
