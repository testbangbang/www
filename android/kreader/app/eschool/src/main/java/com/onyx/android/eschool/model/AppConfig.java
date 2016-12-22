package com.onyx.android.eschool.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.JSON;
import com.onyx.android.eschool.R;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.RawResourceUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by suicheng on 2016/12/5.
 */

public class AppConfig {
    private static AppConfig globalInstance;
    private Map<String, Object> backend = new HashMap<>();

    public static final String HOME_LAYOUT = "home_layout";
    public static final String HOME_PIC_DISPLAY_FILE_PATH = "home_pic_display_file_path";
    public static final String HOME_VIDEO_DISPLAY_FILE_PATH = "home_video_display_file_path";
    public static final String TEACHING_MATERIAL_DOCUMENT_DISPLAY_FILE_PATH = "teaching_material_document_display_file_path";

    private AppConfig(Context context) {
        initAppConfig(context);
    }

    private void initAppConfig(Context context) {
        String content = RawResourceUtil.contentOfRawResource(context, R.raw.app_config);
        Map<String, Object> map = JSON.parseObject(content);
        if (CollectionUtils.isNullOrEmpty(map)) {
            return;
        }
        backend = map;
    }

    static public AppConfig sharedInstance(Context context) {
        if (globalInstance == null) {
            globalInstance = new AppConfig(context);
        }
        return globalInstance;
    }

    static public void forceUpdate(Context context) {
        globalInstance = new AppConfig(context);
    }

    private String getString(String key, String defaultValue) {
        if (backend.containsKey(key)) {
            return (String) backend.get(key);
        } else {
            return defaultValue;
        }
    }

    public boolean hasHomeLayout() {
        return backend.containsKey(HOME_LAYOUT);
    }

    public boolean isForDisplayHomeLayout() {
        return getString(HOME_LAYOUT, "").contains("display");
    }

    public String getHomeLayout() {
        return getString(HOME_LAYOUT, "");
    }

    public String getHomePicDisplayFilePath() {
        return getString(HOME_PIC_DISPLAY_FILE_PATH, "");
    }

    public String getHomeVideoDisplayFilePath() {
        return getString(HOME_VIDEO_DISPLAY_FILE_PATH, "");
    }

    public String getTeachingMaterialDocumentDisplayFilePath() {
        return getString(TEACHING_MATERIAL_DOCUMENT_DISPLAY_FILE_PATH, "");
    }

    public ComponentName getKreaderComponentName() {
        String packageName = "com.onyx.kreader";
        String className = packageName + ".ui.ReaderActivity";
        return new ComponentName(packageName, className);
    }

    public ComponentName getReaderComponentName(Context context) {
        ActivityInfo activityInfo = null;
        ComponentName componentName = getKreaderComponentName();
        try {
            activityInfo = context.getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (activityInfo == null) {
            return null;
        }
        return componentName;
    }
}
