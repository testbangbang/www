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

    public final String getProductBaseUrl() {
        return getApiBase() + "book";
    }

    public final String getProductListUrl() {
        return getApiBase() + "book/list";
    }

    public final String getSearchUrl() {
        return getApiBase() + "book/search";
    }

    public final String getRecommendedUrl() {
        return getApiBase() + "book/list/recommeded";
    }

    public final String getRecentUrl() {
        return getApiBase() + "book/list/recent";
    }

    public final String getCategoryUrl() {
        return getApiBase() + "category";
    }

    public final String getDictionaryList() {
        return getApiBase() + "dictionary/list";
    }

    public final String getAccountSignInUrl() {
        return getApiBase() + "account/signin";
    }

    public final String getAccountSignUpUrl() {
        return getApiBase() + "account/signup";
    }

    public final String getAddDeviceUrl() {
        return getApiBase() + "account/devices";
    }

    public final String getCaptchaUrl() {
        return getApiBase() + "captcha";
    }

    public final String getAnnotationUrl() {
        return getApiBase() + "annotation";
    }

    public final String getBookmarkUrl() {
        return getApiBase() + "bookmark";
    }

    public final String getScribbleUrl() {
        return getApiBase() + "scribble";
    }

    public final String getFwUpdateUrl() {
        return getApiBase() + "firmware/update";
    }

    public final String getReadingUrl() {
        return getApiBase() + "rank/reading";
    }

    public final String getHardwareVerifyUrl() {
        return getApiBase() + "bl";
    }
    
    public final String getLocalSignUpUrl()
    {
    	return Constant.TEST_API_BASE + "account/signup";
    }

}
