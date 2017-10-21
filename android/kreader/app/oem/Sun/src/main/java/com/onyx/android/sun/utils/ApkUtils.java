package com.onyx.android.sun.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.data.request.cloud.ApplicationUpdateRequest;
import com.onyx.android.sdk.data.request.cloud.CloudFileDownloadRequest;
import com.onyx.android.sdk.data.request.cloud.FirmwareUpdateRequest;
import com.onyx.android.sdk.data.utils.DownloadUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.PackageUtils;
import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.event.ApkDownloadSucceedEvent;
import com.onyx.android.sun.event.HaveNewVersionApkEvent;
import com.onyx.android.sun.event.HaveNewVersionEvent;
import com.onyx.android.sun.event.HideLoadingProgressEvent;
import com.onyx.android.sun.event.StartDownloadingEvent;
import com.onyx.android.sun.event.UpdateDownloadSucceedEvent;
import com.onyx.android.sun.requests.local.RequestFirmwareLocalCheck;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;
import com.onyx.android.sun.view.CustomDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2016/12/1.
 */

public class ApkUtils {
    private static CustomDialog dialog;

    public static String getSoftwareBuildName() {
        PackageInfo packageInfo = getPackageInfo();
        String[] versions = null;
        String buildName = "";
        if (null == packageInfo) {
            buildName = "";
        } else if (!TextUtils.isEmpty(packageInfo.versionName)) {
            versions = packageInfo.versionName.split(" ");
        }
        if (versions != null && versions.length >= 2) {
            buildName = versions[1];
        }
        return buildName;
    }

    private static PackageInfo getPackageInfo() {
        try {
            Context context = SunApplication.getInstance().getBaseContext();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSoftwareVersionName() {
        PackageInfo packageInfo = getPackageInfo();
        String versionName = "";
        if (null == packageInfo) {
            versionName = "";
        } else if (!TextUtils.isEmpty(packageInfo.versionName)) {
            versionName = packageInfo.versionName;
        }
        return versionName;
    }

    public static void installApk(Context context, String path) {
        File apkFile = new File(path);
        if (!apkFile.exists()) {
            return;
        }
        SilentInstall.installApk(context, path);
    }

    // Should be in sub-thread
    public static void firmwareLocal() {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), new RequestFirmwareLocalCheck(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }

    public static void firmwareCloudCheck(final boolean isUserChecked) {
        FileUtils.deleteFile(getUpdateZipFile().getAbsolutePath());
        final FirmwareUpdateRequest req = OTAManager.cloudFirmwareCheckRequest(SunApplication.getInstance());
        OTAManager.sharedInstance().getCloudStore().submitRequest(SunApplication.getInstance(), req, new com.onyx.android.sdk.common.request.BaseCallback() {
            @Override
            public void done(com.onyx.android.sdk.common.request.BaseRequest request, Throwable e) {
                if (req.isResultFirmwareValid()) {
                    EventBus.getDefault().post(new HaveNewVersionEvent(req));
                } else if (isUserChecked) {
                    CommonNotices.show(SunApplication.getInstance().getString(R.string.without_new_version));
                }
            }
        });
    }

    public static void showLocalCheckDialog(final Context context, String message, final String url) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setTitle(context.getString(R.string.find_a_new_version));
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.start_updating), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                CommonNotices.show(context.getString(R.string.start_updating));
                downloadUpdate(url);
            }
        }).setNegativeButton(context.getString(R.string.cancel_updating), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CommonNotices.show(context.getString(R.string.cancel_updating));
                dialog.dismiss();
            }
        }).create().show();
    }

    public static File getUpdateZipFile() {
        return new File(OTAManager.LOCAL_PATH_SDCARD);
    }

    public static void updateApk(final boolean isUserChecked) {
        ApplicationUpdate appUpdate = getQueryAppUpdate(SunApplication.getInstance().getPackageName(), SunApplication.getInstance());
        List<ApplicationUpdate> list = new ArrayList<>();
        list.add(appUpdate);
        final ApplicationUpdateRequest applicationUpdateRequest = new ApplicationUpdateRequest(list);
        OTAManager.sharedInstance().getCloudStore().submitRequest(SunApplication.getInstance(), applicationUpdateRequest, new com.onyx.android.sdk.common.request.BaseCallback() {
            @Override
            public void done(com.onyx.android.sdk.common.request.BaseRequest request, Throwable e) {
                ApplicationUpdate applicationUpdate = applicationUpdateRequest.getApplicationUpdate();
                if (applicationUpdate != null) {
                    HaveNewVersionApkEvent haveNewVersionApkEvent = new HaveNewVersionApkEvent(applicationUpdate);
                    EventBus.getDefault().post(haveNewVersionApkEvent);
                } else if (isUserChecked) {
                    CommonNotices.show(SunApplication.getInstance().getString(R.string.without_new_version));
                }
            }
        });
    }

    private static ApplicationUpdate getQueryAppUpdate(String packageName, Context context) {
        ApplicationUpdate update = new ApplicationUpdate();
        update.packageName = packageName;
        update.versionCode = PackageUtils.getAppVersionCode(context);
        update.versionName = PackageUtils.getAppVersionName(context, packageName);
        update.type = PackageUtils.getAppType(context, packageName);
        update.channel = PackageUtils.getAppChannel(context, packageName);
        update.platform = PackageUtils.getAppPlatform(context, packageName);
        update.size = PackageUtils.getApkFileSize(context, packageName);
        update.model = Build.MODEL;
        Device device = Device.updateCurrentDeviceInfo(context);
        if (device != null) {
            update.macAddress = device.macAddress;
        }
        return update;
    }

    public static void showNewApkDialog(final Context context, String message, final String url) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(context);
        View inflate = View.inflate(context, R.layout.apk_update_view, null);
        TextView updateMsg = (TextView) inflate.findViewById(R.id.apk_update_message);
        final TextView progressBar = (TextView) inflate.findViewById(R.id.apk_progress);
        dialog = builder.setContentView(inflate).setTitle(context.getString(R.string.find_a_new_version))
                .setPositiveButton(context.getString(R.string.start_updating), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadAPK(url, progressBar);
                        CommonNotices.show(context.getString(R.string.start_updating));
                    }
                }).setNegativeButton(context.getString(R.string.cancel_updating), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommonNotices.show(context.getString(R.string.cancel_updating));
                        dialog.dismiss();
                    }
                }).create();
        updateMsg.setText(message);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.show();
    }

    private static void downloadAPK(String url, final TextView progressBar) {
        final DownloadUtils.DownloadCallback downloadCallback = new DownloadUtils.DownloadCallback() {
            @Override
            public void stateChanged(int state, long finished, long total, long precentage) {
                progressBar.setText(precentage + "%");
                if (precentage == 100 && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };
        FileUtils.deleteFile(Constants.APK_DOWNLOAD_PATH);
        CloudFileDownloadRequest downloadRequest = OnyxDownloadManager.getInstance().createDownloadRequest(url, Constants.APK_DOWNLOAD_PATH, Constants.APK_NAME);
        BaseDownloadTask download = OnyxDownloadManager.getInstance().download(downloadRequest, new com.onyx.android.sdk.common.request.BaseCallback() {
            @Override
            public void done(com.onyx.android.sdk.common.request.BaseRequest request, Throwable e) {
                if (e == null) {
                    EventBus.getDefault().post(new ApkDownloadSucceedEvent());
                }
            }

            @Override
            public void progress(com.onyx.android.sdk.common.request.BaseRequest request, ProgressInfo info) {
                if (downloadCallback != null) {
                    downloadCallback.stateChanged(0, info.soFarBytes, info.totalBytes, (long) info.progress);
                }
            }
        });
        OnyxDownloadManager.getInstance().startDownload(download);
    }

    private static void downloadUpdate(String url) {
        EventBus.getDefault().post(new StartDownloadingEvent());
        OnyxDownloadManager downloadManager = OnyxDownloadManager.getInstance();
        BaseDownloadTask download = downloadManager.download(SunApplication.getInstance(), url, ApkUtils.getUpdateZipFile().getAbsolutePath(), Constants.UPDATE_ZIP, new com.onyx.android.sdk.common.request.BaseCallback() {
            @Override
            public void done(com.onyx.android.sdk.common.request.BaseRequest request, Throwable e) {
                if (e == null) {
                    EventBus.getDefault().post(new UpdateDownloadSucceedEvent());
                    EventBus.getDefault().post(new HideLoadingProgressEvent());
                }
            }
        });
        downloadManager.startDownload(download);
    }
}
