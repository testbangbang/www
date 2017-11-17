package com.onyx.android.sdk.data.rxrequest.data.fs;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;

import com.onyx.android.sdk.data.AppDataInfo;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.fs.BaseFSRequest;
import com.onyx.android.sdk.utils.ApplicationUtil;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/2/16.
 */
public class RxApplicationListLoadRequest extends RxBaseFSRequest {

    private List<String> ignoreAppList;
    private List<String> testAppList;
    private Map<String, String> customizedIconAppsMap;
    private List<AppDataInfo> appInfoList = new ArrayList<>();

    private boolean testAppExist = false;

    public RxApplicationListLoadRequest(DataManager dataManager,List<String> ignoreAppList, List<String> testAppList, Map<String, String> customizedIconAppsMap) {
        super(dataManager);
        this.ignoreAppList = ignoreAppList;
        this.testAppList = testAppList;
        this.customizedIconAppsMap = customizedIconAppsMap;
    }

    public List<AppDataInfo> getAppInfoList() {
        return appInfoList;
    }

    public boolean isTestAppExist() {
        return testAppExist;
    }

    @Override
    public RxApplicationListLoadRequest call() throws Exception {
        processLoadApplication();
        return this;
    }

    private void processLoadApplication() throws Exception {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<PackageInfo> allInstalledPackageList = getAppContext().getPackageManager().getInstalledPackages(0);
        List<ResolveInfo> apps = getAppContext().getPackageManager().queryIntentActivities(mainIntent, 0);

        AppDataInfo appInfo;
        for (PackageInfo packageInfo : allInstalledPackageList) {
            final String packageName = packageInfo.applicationInfo.packageName;
            if (CollectionUtils.safelyContains(ignoreAppList, packageName)) {
                continue;
            }
            if (CollectionUtils.safelyContains(testAppList, packageName)) {
                if (ApplicationUtil.testAppRecordExist(getAppContext(), packageName)) {
                    continue;
                }
                testAppExist = true;
                appInfo = ApplicationUtil.appDataFromPackageInfo(getAppContext(), packageInfo);
            } else {
                appInfo = ApplicationUtil.appDataFromPackageInfo(getAppContext(), apps, packageInfo);
            }
            ApplicationUtil.checkCustomIcon(getAppContext(), customizedIconAppsMap, appInfo);
            if (appInfo != null) {
                appInfoList.add(appInfo);
            }
        }
    }
}
