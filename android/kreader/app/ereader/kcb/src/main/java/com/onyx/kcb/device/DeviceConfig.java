package com.onyx.kcb.device;

import android.content.Context;
import android.os.Build;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kcb.BuildConfig;
import com.onyx.kcb.R;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-11-24.
 */

public class DeviceConfig {

    private static DeviceConfig ourInstance;

    private static final boolean useDebugConfig = false;

    private String cloudContentHost;
    private String cloudContentApi;
    private String cloudContentImportJsonPath;
    private String leanCloudAppId;
    private String leanCloudClientKey;
    private boolean cloudIndexServerUse;
    private String cloudMainIndexServerHost;
    private String cloudMainIndexServerApi;
    private List<String> appFilters;
    private List<String> testApps;
    private QrCodeShowConfigBean qrCodeShowConfig;
    private InfoShowConfigBean infoShowConfig;
    private boolean supportColor;
    private boolean mediaScanSupport;
    private List<String> contentMenuItemList;
    private List<String> musicDir;
    private List<String> galleryDir;
    private boolean audioTag;
    private boolean supportScreenSaver;
    private boolean contentReadOnly;
    private Map<String, JSONArray> appIgnoreListMap;
    private Map<String, String> defaultCustomizedIconAppsMap;
    private Map<String, String> customizedIconAppsMap;

    private DeviceConfig() {
    }

    private DeviceConfig(Context context) {
        String content = readConfig(context);
        if (!StringUtils.isNullOrEmpty(content)) {
            ourInstance = JSON.parseObject(content, DeviceConfig.class);
        }
        if (ourInstance == null) {
            ourInstance = new DeviceConfig();
        }
    }

    static public DeviceConfig sharedInstance(Context context) {
        if (ourInstance == null) {
            new DeviceConfig(context);
        }
        return ourInstance;
    }

    private String readConfig(Context context) {
        String content = readFromDebugModel(context);

        if (StringUtils.isNullOrEmpty(content)) {
            content = readFromManufactureAndModel(context);
        }

        if (StringUtils.isNullOrEmpty(content)) {
            content = readFromModel(context);
        }

        if (StringUtils.isNullOrEmpty(content)) {
            content = readFromGeneralModel(context);
        }

        if (StringUtils.isNullOrEmpty(content)) {
            content = readFromBrand(context);
        }

        if (StringUtils.isNullOrEmpty(content)) {
            content = readFromDefaultOnyxConfig(context);
        }
        return content;
    }

    private String readFromBrand(Context context) {
        return contentFromRawResource(context, Build.BRAND);
    }

    private String readFromManufactureAndModel(Context context) {
        final String name = Build.MANUFACTURER + "_" + Build.MODEL;
        return contentFromRawResource(context, name);
    }

    private String readFromDebugModel(Context context) {
        if (BuildConfig.DEBUG && useDebugConfig) {
            return contentFromRawResource(context, "debug");
        }
        return null;
    }

    private String readFromModel(Context context) {
        final String name = Build.MODEL;
        return contentFromRawResource(context, name);
    }

    private String readFromGeneralModel(Context context) {
        final String model = Build.MODEL.toLowerCase();
        Field[] rawFields = R.raw.class.getFields();
        for (Field field : rawFields) {
            String fieldName = field.getName();
            if (model.startsWith(fieldName)) {
                return contentFromRawResource(context, fieldName);
            }
        }
        return null;
    }

    private String readFromDefaultOnyxConfig(Context context) {
        return contentFromRawResource(context, "onyx");
    }

    private String contentFromRawResource(Context context, String name) {
        String content = "";
        try {
            int res = context.getResources().getIdentifier(name.toLowerCase(), "raw", context.getPackageName());
            content = RawResourceUtil.contentOfRawResource(context, res);
        } catch (Exception e) {
            Debug.w(getClass(), e);
        }
        return content;
    }

    public String getCloudContentHost() {
        return cloudContentHost;
    }

    public void setCloudContentHost(String cloudContentHost) {
        this.cloudContentHost = cloudContentHost;
    }

    public String getCloudContentApi() {
        return cloudContentApi;
    }

    public void setCloudContentApi(String cloudContentApi) {
        this.cloudContentApi = cloudContentApi;
    }

    public String getCloudContentImportJsonPath() {
        return cloudContentImportJsonPath;
    }

    public void setCloudContentImportJsonPath(String cloudContentImportJsonPath) {
        this.cloudContentImportJsonPath = cloudContentImportJsonPath;
    }

    public String getLeanCloudAppId() {
        return leanCloudAppId;
    }

    public void setLeanCloudAppId(String leanCloudAppId) {
        this.leanCloudAppId = leanCloudAppId;
    }

    public String getLeanCloudClientKey() {
        return leanCloudClientKey;
    }

    public void setLeanCloudClientKey(String leanCloudClientKey) {
        this.leanCloudClientKey = leanCloudClientKey;
    }

    public boolean isCloudIndexServerUse() {
        return cloudIndexServerUse;
    }

    public void setCloudIndexServerUse(boolean cloudIndexServerUse) {
        this.cloudIndexServerUse = cloudIndexServerUse;
    }

    public String getCloudMainIndexServerHost() {
        return cloudMainIndexServerHost;
    }

    public void setCloudMainIndexServerHost(String cloudMainIndexServerHost) {
        this.cloudMainIndexServerHost = cloudMainIndexServerHost;
    }

    public String getCloudMainIndexServerApi() {
        return cloudMainIndexServerApi;
    }

    public void setCloudMainIndexServerApi(String cloudMainIndexServerApi) {
        this.cloudMainIndexServerApi = cloudMainIndexServerApi;
    }

    public List<String> getAppFilters() {
        return appFilters;
    }

    public void setAppFilters(List<String> appFilters) {
        this.appFilters = appFilters;
    }

    public List<String> getTestApps() {
        return testApps;
    }

    public void setTestApps(List<String> testApps) {
        this.testApps = testApps;
    }

    public QrCodeShowConfigBean getQrCodeShowConfig() {
        return qrCodeShowConfig;
    }

    public void setQrCodeShowConfig(QrCodeShowConfigBean qrCodeShowConfig) {
        this.qrCodeShowConfig = qrCodeShowConfig;
    }

    public InfoShowConfigBean getInfoShowConfig() {
        return infoShowConfig;
    }

    public void setInfoShowConfig(InfoShowConfigBean infoShowConfig) {
        this.infoShowConfig = infoShowConfig;
    }

    public boolean isSupportColor() {
        return supportColor;
    }

    public void setSupportColor(boolean supportColor) {
        this.supportColor = supportColor;
    }

    public boolean isMediaScanSupport() {
        return mediaScanSupport;
    }

    public void setMediaScanSupport(boolean mediaScanSupport) {
        this.mediaScanSupport = mediaScanSupport;
    }

    public List<String> getContentMenuItemList() {
        return contentMenuItemList;
    }

    public void setContentMenuItemList(List<String> contentMenuItemList) {
        this.contentMenuItemList = contentMenuItemList;
    }

    public List<String> getMusicDir() {
        return musicDir;
    }

    public void setMusicDir(List<String> musicDir) {
        this.musicDir = musicDir;
    }

    public List<String> getGalleryDir() {
        return galleryDir;
    }

    public void setGalleryDir(List<String> galleryDir) {
        this.galleryDir = galleryDir;
    }

    public static class QrCodeShowConfigBean {

        private int orientation;
        private int startX;
        private int startY;

        public int getOrientation() {
            return orientation;
        }

        public void setOrientation(int orientation) {
            this.orientation = orientation;
        }

        public int getStartX() {
            return startX;
        }

        public void setStartX(int startX) {
            this.startX = startX;
        }

        public int getStartY() {
            return startY;
        }

        public void setStartY(int startY) {
            this.startY = startY;
        }
    }

    public static class InfoShowConfigBean {

        private int orientation;
        private int startX;
        private int startY;

        public int getOrientation() {
            return orientation;
        }

        public void setOrientation(int orientation) {
            this.orientation = orientation;
        }

        public int getStartX() {
            return startX;
        }

        public void setStartX(int startX) {
            this.startX = startX;
        }

        public int getStartY() {
            return startY;
        }

        public void setStartY(int startY) {
            this.startY = startY;
        }
    }

    public final boolean hasAudio() {
        return audioTag;
    }

    public final boolean isContentReadOnly() {
        return contentReadOnly;
    }

    public boolean supportScreenSaver() {
        return supportScreenSaver;
    }

    public Map<String, JSONArray> getAppsIgnoreListMap() {
        if (appIgnoreListMap != null && !appIgnoreListMap.isEmpty()) {
            return appIgnoreListMap;
        }
        return new HashMap<>();
    }

    public Map<String, String> getCustomizedIconApps() {
        if (defaultCustomizedIconAppsMap == null) {
            defaultCustomizedIconAppsMap = new HashMap<>();
            defaultCustomizedIconAppsMap.put("com.android.vending", "app_play");
            defaultCustomizedIconAppsMap.put("com.android.browser", "app_browser");
            defaultCustomizedIconAppsMap.put("com.android.calendar", "app_calendar");
            defaultCustomizedIconAppsMap.put("com.android.calculator2", "app_calculator");
            defaultCustomizedIconAppsMap.put("com.android.deskclock", "app_deskclock");
            defaultCustomizedIconAppsMap.put("com.onyx.android.dict", "app_dict");
            defaultCustomizedIconAppsMap.put("com.onyx.dict", "app_dict");
            defaultCustomizedIconAppsMap.put("com.android.providers.downloads.ui", "app_download");
            defaultCustomizedIconAppsMap.put("com.android.email", "app_email");
            defaultCustomizedIconAppsMap.put("com.android.gallery", "app_gallery");
            defaultCustomizedIconAppsMap.put("com.android.music", "app_music");
            defaultCustomizedIconAppsMap.put("com.onyx.android.scribbler", "app_scribbler");
            defaultCustomizedIconAppsMap.put("com.neverland.oreader", "app_oreader");
            defaultCustomizedIconAppsMap.put("com.android.quicksearchbox", "app_search");
            defaultCustomizedIconAppsMap.put("com.android.settings", "app_setting");
            defaultCustomizedIconAppsMap.put("com.android.soundrecorder", "app_recorder");
            defaultCustomizedIconAppsMap.put("com.google.android.gms", "app_google_setting");
            defaultCustomizedIconAppsMap.put("com.onyx.android.note", "app_note");
            defaultCustomizedIconAppsMap.put("com.onyx.calculator", "app_calculator");
        }
        if (customizedIconAppsMap != null && !customizedIconAppsMap.isEmpty() ) {
            defaultCustomizedIconAppsMap.putAll(customizedIconAppsMap);
        }
        return defaultCustomizedIconAppsMap;
    }
}
