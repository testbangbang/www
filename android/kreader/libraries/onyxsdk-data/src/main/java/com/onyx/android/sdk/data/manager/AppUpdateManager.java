package com.onyx.android.sdk.data.manager;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.request.cloud.ApplicationUpdateRequest;
import com.onyx.android.sdk.data.utils.NotificationItem;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.PackageUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/2/13.
 */
public class AppUpdateManager {
    public static final String PHONE_APPLICATION_MODEL = "mobile";

    public static class AppUpdateConfig {
        public int notificationSmallIcon;
        public Bitmap notificationLargeIcon;
        public boolean autoDownload;
        public String model;

        public static AppUpdateConfig create(int smallIcon, boolean autoDownload) {
            AppUpdateConfig config = new AppUpdateConfig();
            config.notificationSmallIcon = smallIcon;
            config.autoDownload = autoDownload;
            return config;
        }
    }

    public static ApplicationUpdate getApplicationUpdate(Context context) {
        ApplicationUpdate appUpdate = ApplicationUpdate.create(context);
        appUpdate.model = PHONE_APPLICATION_MODEL;//default use mobile as model
        return appUpdate;
    }

    public static String getApkFilePath(Context context, ApplicationUpdate update) {
        String packageName = update.packageName.replaceAll("\\.", "_");
        return new File(context.getExternalCacheDir(), packageName + "_" + update.versionCode + ".apk").getAbsolutePath();
    }

    private static String getDownloadUrl(ApplicationUpdate update) {
        return update.getFirstDownloadUrl();
    }

    public static void checkUpdate(Context context, final CloudManager cloudManager, AppUpdateConfig config) {
        checkUpdate(context, cloudManager, config, null);
    }

    public static void checkUpdate(final Context context, final CloudManager cloudManager,
                                   final AppUpdateConfig config, final BaseCallback customCallBack) {
        List<ApplicationUpdate> queryList = new ArrayList<>();
        ApplicationUpdate update = getApplicationUpdate(context);
        if (StringUtils.isNotBlank(config.model)) {
            update.model = config.model;
        }
        queryList.add(update);
        checkUpdate(context, cloudManager, config, queryList, customCallBack);
    }

    public static void checkUpdate(final Context context, final CloudManager cloudManager,
                                   final AppUpdateConfig config, final List<ApplicationUpdate> queryList,
                                   final BaseCallback customCallBack) {
        final ApplicationUpdateRequest singleUpdateRequest = new ApplicationUpdateRequest(queryList);
        cloudManager.submitRequest(context, singleUpdateRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(customCallBack, request, e);
                if (e == null && config.autoDownload) {
                    checkUpdatedFileDownload(context, config, singleUpdateRequest.getUpdateList(), null);
                }
            }
        });
    }

    private static PendingIntent getInstallPendingIntent(Context context, File file) {
        return PendingIntent.getActivity(context, 0, PackageUtils.getInstallIntent(file),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static NotificationItem.NotificationBean buildDownloadNotificationBean(Context context, AppUpdateConfig config,
                                                                                   ApplicationUpdate update, File file) {
        NotificationItem.NotificationBean bean = new NotificationItem.NotificationBean();
        bean.title = PackageUtils.getAppDisplayName(context, context.getPackageName());
        bean.desc = update.versionName;
        bean.icon = config.notificationSmallIcon;
        bean.largeIcon = config.notificationLargeIcon;
        bean.pendingIntent = getInstallPendingIntent(context, file);
        return bean;
    }

    public static boolean checkUpdatedFileDownload(Context context, AppUpdateConfig config,
                                                   ApplicationUpdate update, BaseCallback downloadCallback) {
        if (update == null) {
            return false;
        }
        String filePath = getApkFilePath(context, update);
        File file = new File(filePath);
        String url = getDownloadUrl(update);
        if (StringUtils.isNullOrEmpty(url)) {
            return false;
        }
        BaseDownloadTask task = OnyxDownloadManager.getInstance().downloadWithNotify(url, filePath, filePath,
                buildDownloadNotificationBean(context, config, update, file), downloadCallback);
        task.setForceReDownload(true);
        return OnyxDownloadManager.getInstance().startDownload(task) != 0;
    }

    public static boolean checkUpdatedFileDownload(Context context, AppUpdateConfig config,
                                                   List<ApplicationUpdate> updatingList, BaseCallback downloadCallback) {
        if (CollectionUtils.isNullOrEmpty(updatingList)) {
            return false;
        }
        boolean result = true;
        for (ApplicationUpdate appUpdate : updatingList) {
            result &= checkUpdatedFileDownload(context, config, appUpdate, downloadCallback);
        }
        return result;
    }
}
