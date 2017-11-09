package com.onyx.android.sdk.data.rxrequest.data.fs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.AppDataInfo;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.common.AppPreference;
import com.onyx.android.sdk.data.provider.SystemConfigProvider;
import com.onyx.android.sdk.data.request.data.fs.BaseFSRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.ApplicationUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.onyx.android.sdk.data.provider.SystemConfigProvider.KEY_APP_PREFERENCE;

/**
 * Created by suicheng on 2017/9/21.
 */
public class RxFileOpenWithRequest extends RxBaseFSRequest {

    private File file;
    private String defaultMimeType;
    private Map<String, String> customizedIconAppsMap = new HashMap<>();
    private Map<String, JSONArray> appIgnoreListMap = new HashMap<>();
    private List<String> typeIgnoreList = new ArrayList<>();

    private List<AppDataInfo> appInfoList = new ArrayList<>();
    private int preferenceIndex = -1;

    public RxFileOpenWithRequest(DataManager dataManager,File file, Map<String, JSONArray> appIgnoreListMap,
                                 List<String> typeIgnoreList) {
        super(dataManager);
        this.file = file;
        this.appIgnoreListMap = appIgnoreListMap;
        this.typeIgnoreList = typeIgnoreList;
    }

    public void setDefaultMimeType(String type) {
        this.defaultMimeType = type;
    }

    public void setCustomizedIconAppsMap(Map<String, String> map) {
        this.customizedIconAppsMap = map;
    }

    public int getPreferenceIndex() {
        return preferenceIndex;
    }

    public List<AppDataInfo> getAppInfoList() {
        return appInfoList;
    }

    @Override
    public RxFileOpenWithRequest call() throws Exception {
        processGetAppListForSpecificFile(file);
        return this;
    }

    private void processGetAppListForSpecificFile(File file) {
        List<ResolveInfo> uniqueAppList = loadAppResolveList(file);
        if (CollectionUtils.isNullOrEmpty(uniqueAppList)) {
            return;
        }
        AppPreference appPreference = loadAppPreference(getAppContext(), file);
        for (ResolveInfo info : uniqueAppList) {
            AppDataInfo appInfo = getAppDataInfo(info);
            if (appInfo != null) {
                appInfoList.add(appInfo);
            }
            if (appPreference != null) {
                if (info.activityInfo.packageName.equalsIgnoreCase(appPreference.packageName)) {
                    preferenceIndex = CollectionUtils.getSize(appInfoList) - 1;
                }
            }
        }
    }

    private AppDataInfo getAppDataInfo(ResolveInfo info) {
        AppDataInfo appInfo = ApplicationUtil.appDataFromApplicationInfo(info.activityInfo,
                ApplicationUtil.getPackageInfoFromPackageName(getAppContext(), info.activityInfo.packageName),
                getAppContext().getPackageManager());
        if (appInfo != null) {
            appInfo.intent = ViewDocumentUtils.mimeTypeIntent(file);
            appInfo.intent.setComponent(new ComponentName(appInfo.packageName, appInfo.activityClassName));
            ApplicationUtil.checkCustomIcon(getAppContext(), customizedIconAppsMap, appInfo);
        }
        return appInfo;
    }

    private boolean isTypeIgnored(String mimeType) {
        return CollectionUtils.safelyReverseContains(typeIgnoreList, mimeType);
    }

    private boolean isAppIgnored(JSONArray appArray, String packageName) {
        if (appArray == null) {
            return false;
        }
        for (int i = 0; i < appArray.size(); i++) {
            if (appArray.getString(i).equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private JSONArray getAppIgnoreArray(File file) {
        String fileExt = FilenameUtils.getExtension(file.getName()).toLowerCase(Locale.getDefault());
        JSONArray jsonArray = null;
        if (!CollectionUtils.isNullOrEmpty(appIgnoreListMap)) {
            jsonArray = appIgnoreListMap.get(fileExt);
        }
        return jsonArray;
    }

    private List<ResolveInfo> loadResolveInfoList(File file) {
        Intent intent = ViewDocumentUtils.mimeTypeIntent(file);
        if (StringUtils.isNotBlank(defaultMimeType)) {
            intent.setDataAndType(intent.getData(), defaultMimeType);
        }
        if (isTypeIgnored(intent.getType())) {
            return null;
        }
        return getAppContext().getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
    }

    private List<ResolveInfo> loadAppResolveList(File file) {
        List<ResolveInfo> infoList = loadResolveInfoList(file);
        if (CollectionUtils.isNullOrEmpty(infoList)) {
            return infoList;
        }
        List<ResolveInfo> uniqueAppList = new ArrayList<>();
        Set<String> appDict = new HashSet<>();
        JSONArray jsonArray = getAppIgnoreArray(file);
        for (ResolveInfo info : infoList) {
            if (isAppIgnored(jsonArray, info.activityInfo.packageName)) {
                continue;
            }
            if (!appDict.contains(info.activityInfo.packageName)) {
                uniqueAppList.add(info);
                appDict.add(info.activityInfo.packageName);
            }
        }
        return uniqueAppList;
    }

    private AppPreference loadAppPreference(Context context, File file) {
        Map<String, AppPreference> appMap = AppPreference.getFileAppPreferMap();
        if (CollectionUtils.isNullOrEmpty(appMap)) {
            final String value = SystemConfigProvider.getStringValue(context, KEY_APP_PREFERENCE);
            List<AppPreference> list = JSONObjectParseUtils.parseObject(value, new TypeReference<List<AppPreference>>() {
            });
            if (!CollectionUtils.isNullOrEmpty(list)) {
                AppPreference.addAppPreferencesToMap(list);
            }
        }
        return appMap.get(FilenameUtils.getExtension(file.getName()));
    }
}
