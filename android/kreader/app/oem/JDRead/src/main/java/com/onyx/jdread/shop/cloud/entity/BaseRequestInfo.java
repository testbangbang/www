package com.onyx.jdread.shop.cloud.entity;


import com.onyx.jdread.main.common.JDAppBaseInfo;

import java.util.Map;

/**
 * Created by huxiaomao on 2016/12/19.
 */

public class BaseRequestInfo {
    private JDAppBaseInfo appBaseInfo;
    private Map<String,String> queryArgsMap;

    public JDAppBaseInfo getAppBaseInfo() {
        return appBaseInfo;
    }

    public void setAppBaseInfo(JDAppBaseInfo appBaseInfo) {
        this.appBaseInfo = appBaseInfo;
    }

    public Map<String,String> getQueryArgsMap() {
        return queryArgsMap;
    }

    public void setQueryArgsMap(Map<String,String> queryArgsMap) {
        this.queryArgsMap = queryArgsMap;
    }
}