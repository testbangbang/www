package com.onyx.jdread.shop.common;

import android.os.Build;

import com.jingdong.app.reader.data.DrmTools;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.AppInformationUtils;
import com.onyx.jdread.main.common.Constants;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 17/3/27.
 */

public class JDAppBaseInfo {
    public static final String BUILD_KEY = "build";
    private String build = "";
    public static final String OS_VERSION_KEY = "ov";
    private String osVersion;
    public static final String CLIENT_VERSION_KEY = "cv";
    private String clientVersion;
    public static final String SUB_UNION_ID_KEY = "suid";
    private String subUnionId;
    public static final String OS_KEY = "os";
    private String os;
    public static final String CLIENT_KEY = "client";
    private String client;
    public static final String UUID_KEY = "uuid";
    private String uuid;
    public static final String UNION_ID_KEY = "uid";
    private String unionId;
    public static final String MODEL_KEY = "model";
    private String model;
    public static final String BRAND_KEY = "br";
    private String brand;
    public static final String SCREEN_KEY = "sc";
    private String screen;
    public static final String APP_KEY = "app";
    private String app = "";
    public static final String IP_KEY = "ip";
    private String ip = "";
    public static final String TIME_KEY = "tm";
    private String time = "";
    public static final String TID_KEY = "tid";
    private String tid = "";
    public static final String ENC_KEY = "enc";
    private String enc = "";
    public static final String PARAMS_KEY = "params";
    private String params = "";
    public static final String SIGN_KEY = "sign";
    private String sign = "";
    public static final String NET_KEY = "nt";
    private String nt = "";
    public static final String SP_KEY = "sp";
    private String sp = "";

    public static final String BODY_KEY = "body";
    public static final String CLIENT_KEY_DEFAULT_VALUE = "android";
    public static final String OS_KEY_DEFAULT_VALUE = "android";
    public static final String APP_DEFAULT_VALUE = "eink";
    public static final String NET_DEFAULT_VALUE = "wifi";
    public static final String SP_DEFAULT_VALUE = "";
    public static final String ENC_DEFAULT_VALUE = "1"; //1-AES，2-DES
    private final Map<String, String> requestParamsMap;

    public JDAppBaseInfo() {
        osVersion = URLEncoder.encode(Build.VERSION.RELEASE);
        clientVersion = Constants.CLIENT_VERSION;
        subUnionId = AppInformationUtils.getPropertiesValue(AppInformationUtils.CPA_PROPERTIES);
        os = OS_KEY_DEFAULT_VALUE;
        client = CLIENT_KEY_DEFAULT_VALUE;
        uuid = DrmTools.hashDevicesInfo(JDReadApplication.getInstance());
        unionId = AppInformationUtils.getPropertiesValue(AppInformationUtils.CPA_PROPERTIES);
        model = Build.MODEL;
        brand = Build.BRAND;
        screen = AppInformationUtils.getScreenSize();
        app = APP_DEFAULT_VALUE;
        ip = AppInformationUtils.getIpAddress();
        enc = ENC_DEFAULT_VALUE;
        nt = NET_DEFAULT_VALUE;
        sp = SP_DEFAULT_VALUE;
        requestParamsMap = new HashMap<>();
        requestParamsMap.put(BUILD_KEY, build);
        requestParamsMap.put(OS_VERSION_KEY, osVersion);
        requestParamsMap.put(CLIENT_VERSION_KEY, clientVersion);
        requestParamsMap.put(SUB_UNION_ID_KEY, subUnionId == null ? "" : subUnionId);
        requestParamsMap.put(OS_KEY, os);
        requestParamsMap.put(CLIENT_KEY, client);
        requestParamsMap.put(UUID_KEY, uuid);
        requestParamsMap.put(UNION_ID_KEY, unionId == null ? "" : unionId);
        requestParamsMap.put(MODEL_KEY, model);
        requestParamsMap.put(BRAND_KEY, brand);
        requestParamsMap.put(SCREEN_KEY, screen);
        requestParamsMap.put(IP_KEY, ip);
        requestParamsMap.put(APP_KEY, app);
        requestParamsMap.put(ENC_KEY, enc);
        requestParamsMap.put(NET_KEY, nt);
        requestParamsMap.put(SP_KEY, sp);
    }

    public String getBuild() {
        return build;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public String getSubUnionId() {
        return subUnionId;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        requestParamsMap.put(TIME_KEY,time);
        this.time = time;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        requestParamsMap.put(TID_KEY,tid);
        this.tid = tid;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
        requestParamsMap.put(PARAMS_KEY,params);
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        requestParamsMap.put(SIGN_KEY,sign);
        this.sign = sign;
    }
}