package com.onyx.android.sdk.data.model.common;

import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/9/21.
 */
public class AppPreference {

    private static Map<String, AppPreference> fileApplicationMap = new HashMap<>();

    // upper case
    public String fileExtension = null;
    public String appName = null;
    public String packageName = null;
    public String className = null;

    public static AppPreference create(String fileExtension, String appName, String packageName, String className) {
        AppPreference appPreference = new AppPreference();
        appPreference.appName = appName;
        appPreference.packageName = packageName;
        appPreference.className = className;
        appPreference.fileExtension = fileExtension;
        return appPreference;
    }

    public static Map<String, AppPreference> getFileAppPreferMap() {
        return fileApplicationMap;
    }

    public static List<AppPreference> getFileAppPreferList() {
        if (CollectionUtils.isNullOrEmpty(fileApplicationMap)) {
            return new ArrayList<>();
        }
        return Arrays.asList(AppPreference.getFileAppPreferMap().values().toArray(new AppPreference[0]));
    }

    public static void addAppPreferencesToMap(AppPreference app) {
        fileApplicationMap.put(app.fileExtension, app);
    }

    public static void addAppPreferencesToMap(List<AppPreference> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        for (AppPreference a : list) {
            addAppPreferencesToMap(a);
        }
    }
}
