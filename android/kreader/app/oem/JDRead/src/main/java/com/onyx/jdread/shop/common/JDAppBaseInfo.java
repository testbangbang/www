package com.onyx.jdread.shop.common;

import android.os.Build;

import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.AppInformationUtils;
import com.onyx.jdread.main.common.Constants;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
    public static final String SN_KEY = "sn";
    private String sn = "";
    public static final String MAC_KEY = "mac";
    private String mac = "";

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
        uuid = Build.SERIAL;
        unionId = AppInformationUtils.getPropertiesValue(AppInformationUtils.CPA_PROPERTIES);
        model = Build.MODEL;
        brand = Build.BRAND;
        screen = AppInformationUtils.getScreenSize();
        app = APP_DEFAULT_VALUE;
        ip = AppInformationUtils.getIpAddress();
        enc = ENC_DEFAULT_VALUE;
        nt = NET_DEFAULT_VALUE;
        sp = SP_DEFAULT_VALUE;
        sn = Build.SERIAL;
        mac = NetworkUtil.getMacAddress(JDReadApplication.getInstance());
        time = String.valueOf(System.currentTimeMillis());
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
        requestParamsMap.put(TIME_KEY,time);
        requestParamsMap.put(APP_KEY, app);
        requestParamsMap.put(NET_KEY, nt);
        requestParamsMap.put(SP_KEY, sp);
    }

    public JDAppBaseInfo(Map<String, String> extraAddMap) {
        this();
        addRequestParams(extraAddMap);
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

    public void addApp() {
        requestParamsMap.put(APP_KEY, app);
    }

    public void removeApp() {
        requestParamsMap.remove(APP_KEY);
    }

    public void setEnc() {
        requestParamsMap.put(ENC_KEY, enc);
    }

    public void removeEnc() {
        requestParamsMap.remove(ENC_KEY);
    }

    public void setSn() {
        requestParamsMap.put(SN_KEY, sn);
    }

    public void setMac() {
        requestParamsMap.put(MAC_KEY, mac);
    }

    public String getRequestParams() {
        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(requestParamsMap.entrySet());
        Collections.sort(list, new RequestKeyComparator());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<String, String> stringStringEntry = list.get(i);
            String s = stringStringEntry.toString();
            if (!requestParamsMap.containsKey(APP_KEY)) {
                try {
                    String[] entry = s.split("=",2);
                    if (entry != null && entry.length > 1) {
                        String key = entry[0];
                        String value = entry[1];
                        value = URLEncoder.encode(value,"UTF-8");
                        s = key + "=" +value;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sb.append(s + "&");
        }
        String queryString = sb.deleteCharAt(sb.length() - 1).toString();
        return queryString;
    }

    public String getSignValue(String uri) {
        String queryString = CloudApiContext.JD_BOOK_BASE_URI + uri + getRequestParams();
        String saltString = JDAppBaseInfo.APP_DEFAULT_VALUE + getTime() + getUuid();
        String salt = FileUtils.computeMD5(saltString);
        String sign = FileUtils.computeMD5(salt + queryString);
        return sign;
    }

    public void clear() {
        if (requestParamsMap != null && requestParamsMap.size() > 0) {
            requestParamsMap.clear();
        }
    }

    public void addRequestParams(Map<String,String> requestParamsMap) {
        if (requestParamsMap != null) {
            this.requestParamsMap.putAll(requestParamsMap);
        }
    }

    public void setPageSize(String page, String pageSize) {
        String currentPage = "1";
        String currentPageSize = "20";
        if (StringUtils.isNotBlank(page) || StringUtils.isNotBlank(pageSize)) {
            currentPage = page;
            currentPageSize = pageSize;
        }
        Map<String, String> map = new HashMap<>();
        map.put("page", currentPage);
        map.put("page_size", currentPageSize);
        addRequestParams(map);
    }

    public void setDefaultPage() {
        Map<String, String> map = new HashMap<>();
        map.put("page", "1");
        map.put("page_size", "20");
        addRequestParams(map);
    }
}
