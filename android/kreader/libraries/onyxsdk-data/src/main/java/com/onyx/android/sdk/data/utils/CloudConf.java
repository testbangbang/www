package com.onyx.android.sdk.data.utils;


import com.onyx.android.sdk.data.Constant;

/**
 * Created by zhuzeng on 12/14/15.
 */
public class CloudConf {

    private String hostBase = Constant.CN_HOST_BASE;
    private String apiBase = Constant.CN_API_BASE;
    private String cloudStorage = Constant.DEFAULT_CLOUD_STORAGE;
    private String statistics = Constant.STATISTICS_API_BASE;

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

    public String getStatistics() {
        return statistics;
    }

    public static CloudConf create(final String host, final String api, final String cloud) {
        return new CloudConf(host, api, cloud);
    }
}
