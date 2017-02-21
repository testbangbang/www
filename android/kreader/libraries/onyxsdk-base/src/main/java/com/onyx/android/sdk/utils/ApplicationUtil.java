package com.onyx.android.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.onyx.android.sdk.data.AppDataInfo;
import com.onyx.android.sdk.device.Device;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/2/16.
 */
public class ApplicationUtil {
    public static final String DONE_TAG = "done";
    public static final String DATA_KEEP = "/data/keep";

    private static boolean checkDataKeepRecord(Context context, final String packageName) {
        return StringUtils.isNotBlank(packageName) && new File(DATA_KEEP, packageName).exists();
    }

    private static boolean checkSystemConfigRecord(Context context, final String packageName) {
        return StringUtils.isNotBlank(packageName) && StringUtils.isNotBlank(Device.currentDevice().readSystemConfig(context, packageName));
    }

    public static boolean testAppRecordExist(Context context, final String packageName) {
        return checkDataKeepRecord(context, packageName) || checkSystemConfigRecord(context, packageName);
    }

    public static boolean setSystemVerifyFlagDone(Context context, String verifyFlag) {
        Device.currentDevice().saveSystemConfig(context, verifyFlag, DONE_TAG);
        return true;
    }

    public static boolean clearAllTestApps(Context context, List<String> testAppList) {
        if (testAppList == null) {
            return false;
        }

        for (String object : testAppList) {
            Device.currentDevice().saveSystemConfig(context, object, DONE_TAG);
        }
        return true;
    }

    public static boolean isSystemApp(String pkgName, PackageManager pkgManager) throws PackageManager.NameNotFoundException {
        return isSystemApp(pkgManager.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES));
    }

    public static boolean isSystemApp(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0;
    }

    static public AppDataInfo appDataFromApplicationInfo(final ActivityInfo activityInfo, final PackageInfo pkgInfo,
                                                         final PackageManager pkgManager) {
        if (activityInfo == null || pkgInfo == null) {
            return null;
        }

        Intent i = ActivityUtil.getLaunchIntentForPackage(pkgManager, activityInfo);
        if (i == null) {
            return null;
        }
        AppDataInfo appInfo = new AppDataInfo();
        appInfo.packageName = activityInfo.packageName;
        appInfo.activityClassName = activityInfo.name;
        appInfo.labelName = activityInfo.loadLabel(pkgManager).toString();
        appInfo.lastUpdatedTime = pkgInfo.lastUpdateTime;
        appInfo.isSystemApp = ApplicationUtil.isSystemApp(pkgInfo);
        appInfo.intent = i;
        appInfo.iconDrawable = activityInfo.loadIcon(pkgManager);
        return appInfo;
    }

    static public AppDataInfo appDataFromPackageInfo(final Context context, final PackageInfo packageInfo) {
        AppDataInfo appInfo = null;
        try {
            ActivityInfo[] activities = context.getPackageManager().getPackageInfo(packageInfo.packageName, PackageManager.GET_ACTIVITIES).activities;
            appInfo = appDataFromApplicationInfo(activities[0], packageInfo, context.getPackageManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appInfo;
    }

    static public AppDataInfo appDataFromPackageInfo(final Context context, final List<ResolveInfo> apps, final PackageInfo packageInfo) {
        AppDataInfo appInfo = null;
        for (ResolveInfo resolveInfo : apps) {
            if (packageInfo.packageName.equalsIgnoreCase(resolveInfo.activityInfo.packageName)) {
                appInfo = appDataFromApplicationInfo(resolveInfo.activityInfo, packageInfo, context.getPackageManager());
                break;
            }
        }
        return appInfo;
    }

    static public PackageInfo getPackageInfoFromPackageName(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static public ActivityInfo getActivityInfoFromPackageName(Context context, String packageName) {
        try {
            ActivityInfo[] activities = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES).activities;
            return activities[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static public void checkCustomIcon(final Context context, Map<String, String> customizedIconAppsMap, AppDataInfo appDataInfo) {
        if (customizedIconAppsMap == null || appDataInfo == null) {
            return;
        }
        if (CollectionUtils.safelyContains(customizedIconAppsMap.keySet(), appDataInfo.packageName)) {
            String iconResourceName = customizedIconAppsMap.get(appDataInfo.packageName);
            appDataInfo.iconDrawable = context.getResources().getDrawable(RawResourceUtil.getDrawableIdByName(context, iconResourceName));
        }
    }
}
