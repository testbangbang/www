package com.onyx.jdread.common;

import android.os.Build;

import com.jingdong.app.reader.data.DrmTools;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 17/3/27.
 */

public class AppBaseInfo {
    public static final String BUILD_KEY = "build";
    private String build = "";
    public static final String OS_VERSION_KEY = "osVersion";
    private String osVersion;
    public static final String JD_SINGLE_TAG_KEY = "jds";
    private String JDSingleTag;
    public static final String CLIENT_VERSION_KEY = "clientVersion";
    private String clientVersion;
    public static final String SUB_UNION_ID_KEY = "subunionId";
    private String subUnionId;
    public static final String APP_ID_KEY = "appId";
    private String appId;
    public static final String OS_KEY = "os";
    private String os;
    public static final String CLIENT_KEY = "client";
    private String client;
    public static final String UUID_KEY = "uuid";
    private String uuid;
    public static final String UNION_ID_KEY = "unionId";
    private String unionId;
    public static final String MODEL_KEY = "model";
    private String model;
    public static final String BRAND_KEY = "brand";
    private String brand;
    public static final String SCREEN_KEY = "screen";
    private String screen;

    public static final String BODY_KEY = "body";
    public static final String PARTNER_ID_KEY = "partnerID";
    public static final String SUB_PARTNER_ID_KEY = "subPartnerID";
    public static final String JD_USER_NAME = "jd_user_name";
    public static final String GIFT_PACKS_ID = "giftPacksId";

    public static final String CLIENT_KEY_DEFAULT_VALUE = "android";
    public static final String OS_KEY_DEFAULT_VALUE = "android";
    public static final String APP_KEY_DEFAULT_VALUE = "1";
    private final Map<String, String> requestParamsMap;

    public AppBaseInfo() {
        osVersion = URLEncoder.encode(Build.VERSION.RELEASE);
        JDSingleTag = AppInformationUtils.getJDSingleTag();
        clientVersion = Constants.CLIENT_VERSION;
        subUnionId = AppInformationUtils.getPropertiesValue(AppInformationUtils.CPA_PROPERTIES);
        appId = APP_KEY_DEFAULT_VALUE;
        os = OS_KEY_DEFAULT_VALUE;
        client = CLIENT_KEY_DEFAULT_VALUE;
        uuid = DrmTools.hashDevicesInfor();
        unionId = AppInformationUtils.getPropertiesValue(AppInformationUtils.CPA_PROPERTIES);
        model = Build.MODEL;
        brand = Build.BRAND;
        screen = AppInformationUtils.getScreenSize();
        requestParamsMap = new HashMap<>();
        requestParamsMap.put(BUILD_KEY, build);
        requestParamsMap.put(OS_VERSION_KEY, osVersion);
        requestParamsMap.put(JD_SINGLE_TAG_KEY, JDSingleTag);
        requestParamsMap.put(CLIENT_VERSION_KEY, clientVersion);
        requestParamsMap.put(SUB_UNION_ID_KEY, subUnionId == null ? "" : subUnionId);
        requestParamsMap.put(APP_ID_KEY, appId);
        requestParamsMap.put(OS_KEY, os);
        requestParamsMap.put(CLIENT_KEY, client);
        requestParamsMap.put(UUID_KEY, uuid);
        requestParamsMap.put(UNION_ID_KEY, unionId == null ? "" : unionId);
        requestParamsMap.put(MODEL_KEY, model);
        requestParamsMap.put(BRAND_KEY, brand);
        requestParamsMap.put(SCREEN_KEY, screen);
    }

    public String getBuild() {
        return build;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getJDSingleTag() {
        return JDSingleTag;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public String getSubUnionId() {
        return subUnionId;
    }

    public String getAppId() {
        return appId;
    }

    public String getOs() {
        return os;
    }

    public String getClient() {
        return client;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUnionId() {
        return unionId;
    }

    public String getModel() {
        return model;
    }

    public String getBrand() {
        return brand;
    }

    public String getScreen() {
        return screen;
    }

    public Map<String, String> getRequestParamsMap() {
        return requestParamsMap;
    }
}