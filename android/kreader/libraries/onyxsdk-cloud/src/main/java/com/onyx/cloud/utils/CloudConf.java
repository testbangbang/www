package com.onyx.cloud.utils;

import com.onyx.cloud.Constant;

/**
 * Created by zhuzeng on 12/14/15.
 */
public class CloudConf {

    private String hostBase = Constant.CN_HOST_BASE;
    private String apiBase = Constant.CN_API_BASE;
    private String cloudStorage = Constant.DEFAULT_CLOUD_STORAGE;

    public CloudConf(final String host, final String api, final String cloud) {
        hostBase = host;
        apiBase = api;
        cloudStorage = cloud;
    }

    public final String getHostBase() {
        return hostBase;
    }

    public final String getApiBase() {
        return apiBase;
    }

    public final String getCloudStorage() {
        return cloudStorage;
    }



}
