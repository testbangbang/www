package com.onyx.edu.manager.manager;

import android.app.PendingIntent;
import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.request.cloud.ApplicationUpdateRequest;
import com.onyx.android.sdk.data.utils.NotificationItem;
import com.onyx.android.sdk.utils.PackageUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.AdminApplication;
import com.onyx.edu.manager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/2/13.
 */
public class AppUpdateManager {
    public static final String PHONE_APPLICATION_MODEL = "mobile";

    private static ApplicationUpdate getApplicationUpdate(Context context) {
        ApplicationUpdate appUpdate = new ApplicationUpdate();
        appUpdate.versionCode = PackageUtils.getAppVersionCode(context);
        appUpdate.versionName = PackageUtils.getAppVersionName(context);
        appUpdate.packageName = context.getPackageName();
        appUpdate.platform = PackageUtils.getAppPlatform(context);
        appUpdate.channel = PackageUtils.getAppChannel(context);
        appUpdate.type = PackageUtils.getAppType(context);
        appUpdate.model = PHONE_APPLICATION_MODEL;
        return appUpdate;
    }

    public static String getApkFilePath(Context context, ApplicationUpdate update) {
        return new File(context.getExternalCacheDir(), update.versionCode + ".apk").getAbsolutePath();
    }

    public static void checkUpdate(Context context, boolean autoDownload) {
        checkUpdate(context, autoDownload, null);
    }

    public static void checkUpdate(final Context context, final boolean autoDownload, final BaseCallback customCallBack) {
        List<ApplicationUpdate> queryList = new ArrayList<>();
        queryList.add(getApplicationUpdate(context));
        final ApplicationUpdateRequest singleUpdateRequest = new ApplicationUpdateRequest(queryList);
        AdminApplication.getUpdateCheckManager().submitRequest(context, singleUpdateRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(customCallBack, request, e);
                if (e == null && autoDownload) {
                    checkUpdatedFileDownload(context, singleUpdateRequest.getApplicationUpdate(), null);
                }
            }
        });
    }

    private static String getDownloadUrl(ApplicationUpdate update) {
        return update.getFirstDownloadUrl();
    }

    private static PendingIntent getInstallPendingIntent(Context context, File file) {
        return PendingIntent.getActivity(context, 0, PackageUtils.getInstallIntent(file),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static NotificationItem.NotificationBean buildDownloadNotificationBean(Context context, ApplicationUpdate update, File file) {
        NotificationItem.NotificationBean bean = new NotificationItem.NotificationBean();
        bean.title = PackageUtils.getAppDisplayName(context, context.getPackageName());
        bean.desc = update.versionName;
        bean.icon = R.mipmap.ic_launcher;
        bean.pendingIntent = getInstallPendingIntent(context, file);
        return bean;
    }

    public static boolean checkUpdatedFileDownload(Context context, ApplicationUpdate update, BaseCallback downloadCallback) {
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
                buildDownloadNotificationBean(context, update, file), downloadCallback);
        task.setForceReDownload(true);
        return OnyxDownloadManager.getInstance().startDownload(task) != 0;
    }
}
