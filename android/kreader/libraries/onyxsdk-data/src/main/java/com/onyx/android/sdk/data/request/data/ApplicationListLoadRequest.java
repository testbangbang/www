package com.onyx.android.sdk.data.request.data;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;

import com.onyx.android.sdk.data.AppDataInfo;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.ApplicationUtil;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/2/16.
 */
public class ApplicationListLoadRequest extends BaseDataRequest {

    private List<String> ignoreAppList;
    private List<String> testAppList;
    private Map<String, String> customizedIconAppsMap;
    private List<AppDataInfo> appInfoList = new ArrayList<>();

    private boolean testAppExist = false;

    public ApplicationListLoadRequest(List<String> ignoreAppList, List<String> testAppList, Map<String, String> customizedIconAppsMap) {
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
    public void execute(DataManager dataManager) throws Exception {
        processLoadApplication();
    }

    private void processLoadApplication() throws Exception {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<PackageInfo> allInstalledPackageList = getContext().getPackageManager().getInstalledPackages(0);
        List<ResolveInfo> apps = getContext().getPackageManager().queryIntentActivities(mainIntent, 0);

        AppDataInfo appInfo;
        for (PackageInfo packageInfo : allInstalledPackageList) {
            final String packageName = packageInfo.applicationInfo.packageName;
            if (CollectionUtils.safelyContains(ignoreAppList, packageName)) {
                continue;
            }
            if (CollectionUtils.safelyContains(testAppList, packageName)) {
                if (ApplicationUtil.testAppRecordExist(getContext(), packageName)) {
                    continue;
                }
                testAppExist = true;
                appInfo = ApplicationUtil.appDataFromPackageInfo(getContext(), packageInfo);
            } else {
                appInfo = ApplicationUtil.appDataFromPackageInfo(getContext(), apps, packageInfo);
            }
            ApplicationUtil.checkCustomIcon(getContext(), customizedIconAppsMap, appInfo);
            if (appInfo != null) {
                appInfoList.add(appInfo);
            }
        }
    }
}
