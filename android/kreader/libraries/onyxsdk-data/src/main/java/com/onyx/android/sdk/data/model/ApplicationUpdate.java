package com.onyx.android.sdk.data.model;

import android.content.Context;
import android.os.Build;

import com.onyx.android.sdk.data.converter.MapListStringConverter;
import com.onyx.android.sdk.data.db.OnyxCloudDatabase;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.PackageUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by suicheng on 2016/9/26.
 */
@Table(database = OnyxCloudDatabase.class)
public class ApplicationUpdate extends BaseData {

    @Column
    public String channel;
    @Column
    public String packageName;
    @Column
    public String model;

    public String[] downloadUrlList;
    @Column(typeConverter = MapListStringConverter.class)
    public Map<String, List<String>> changeLogs;
    @Column
    public String type;
    @Column
    public String versionName;
    @Column
    public int versionCode;
    @Column
    public int size;
    @Column
    public String macAddress;
    @Column
    public String platform;

    public String getFirstDownloadUrl() {
        if (downloadUrlList == null || downloadUrlList.length <= 0) {
            return null;
        }
        return downloadUrlList[0];
    }

    public List<String> getChangeLogList() {
        if (CollectionUtils.isNullOrEmpty(changeLogs)) {
            return new ArrayList<>();
        }
        List<String> changeLogList = changeLogs.get(Locale.getDefault().toString());
        if (CollectionUtils.isNullOrEmpty(changeLogList)) {
            String enUsKey = Locale.US.toString();
            changeLogList = changeLogs.get(enUsKey);
        }
        if (changeLogList == null) {
            changeLogList = new ArrayList<>();
        }
        return changeLogList;
    }

    public static ApplicationUpdate create(Context context) {
        ApplicationUpdate appUpdate = new ApplicationUpdate();
        appUpdate.versionCode = PackageUtils.getAppVersionCode(context);
        appUpdate.versionName = PackageUtils.getAppVersionName(context);
        appUpdate.packageName = context.getPackageName();
        appUpdate.platform = PackageUtils.getAppPlatform(context);
        appUpdate.channel = PackageUtils.getAppChannel(context);
        appUpdate.type = PackageUtils.getAppType(context);
        appUpdate.model = Build.MODEL;
        return appUpdate;
    }
}
