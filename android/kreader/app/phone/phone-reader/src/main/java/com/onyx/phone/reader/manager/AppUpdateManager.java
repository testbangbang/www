package com.onyx.phone.reader.manager;

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
import com.onyx.phone.reader.R;
import com.onyx.phone.reader.ReaderApplication;

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

    private static String getApkFilePath(Context context, ApplicationUpdate update) {
        return new File(context.getExternalCacheDir(), update.versionCode + ".apk").getAbsolutePath();
    }

    public static void checkUpdate(Context context, boolean autoDownload) {
        checkUpdate(context, autoDownload, null);
    }

    public static void checkUpdate(final Context context, final boolean autoDownload, final BaseCallback customCallBack) {
        List<ApplicationUpdate> queryList = new ArrayList<>();
        queryList.add(getApplicationUpdate(context));
        final ApplicationUpdateRequest singleUpdateRequest = new ApplicationUpdateRequest(queryList);
        ReaderApplication.getCloudStore().submitRequest(context, singleUpdateRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    BaseCallback.invoke(customCallBack, request, e);
                    e.printStackTrace();
                    return;
                }
                if (autoDownload) {
                    checkUpdatedFileDownload(context, singleUpdateRequest.getApplicationUpdate());
                }
            }
        });
    }

    private static String getDownloadUrl(ApplicationUpdate update) {
        if (update.downloadUrlList == null || update.downloadUrlList.length <= 0) {
            return null;
        }
        return update.downloadUrlList[0];
    }

    private static PendingIntent getInstallPendingIntent(Context context, File file) {
        return PendingIntent.getActivity(context, 0, PackageUtils.installIntent(file),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static NotificationItem.NotificationBean buildDownloadNotificationBean(Context context, ApplicationUpdate update, File file) {
        NotificationItem.NotificationBean bean = new NotificationItem.NotificationBean();
        bean.title = PackageUtils.getAppDisplayName(context, context.getPackageName());
        bean.desc = update.versionName;
        bean.icon = R.mipmap.icon;
        bean.pendingIntent = getInstallPendingIntent(context, file);
        return bean;
    }

    private static boolean checkUpdatedFileDownload(Context context, ApplicationUpdate update) {
        if (update == null) {
            return false;
        }
        String filePath = getApkFilePath(context, update);
        File file = new File(filePath);
        if (file.exists()) {
            return false;
        }
        String url = getDownloadUrl(update);
        if (StringUtils.isNullOrEmpty(url)) {
            return false;
        }
        BaseDownloadTask task = OnyxDownloadManager.getInstance().downloadWithNotify(url, filePath, filePath,
                buildDownloadNotificationBean(context, update, file), null);
        task.setForceReDownload(true);
        return OnyxDownloadManager.getInstance().startDownload(task) == 0;
    }
}
