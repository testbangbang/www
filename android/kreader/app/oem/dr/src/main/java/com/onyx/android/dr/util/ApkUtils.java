package com.onyx.android.dr.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.HaveNewVersionApkEvent;
import com.onyx.android.dr.event.HaveNewVersionEvent;
import com.onyx.android.dr.event.HideLoadingProgressEvent;
import com.onyx.android.dr.event.StartDownloadingEvent;
import com.onyx.android.dr.event.UpdateDownloadSucceedEvent;
import com.onyx.android.dr.reader.view.CustomDialog;
import com.onyx.android.dr.request.cloud.RequestFirmwareLocalCheck;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.data.request.cloud.ApplicationUpdateRequest;
import com.onyx.android.sdk.data.request.cloud.FirmwareUpdateRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.PackageUtils;

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
            Context context = DRApplication.getInstance().getBaseContext();
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

    public static void firmwareCloudCheck(final boolean isUserChecked) {
        FileUtils.deleteFile(getUpdateZipFile().getAbsolutePath());
        final FirmwareUpdateRequest req = OTAManager.cloudFirmwareCheckRequest(DRApplication.getInstance());
        OTAManager.sharedInstance().getCloudStore().submitRequest(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (req.isResultFirmwareValid()) {
                    EventBus.getDefault().post(new HaveNewVersionEvent(req));
                } else if (isUserChecked) {
                    CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.without_new_version));
                }
            }
        });
    }

    public static void showLocalCheckDialog(final Context context, String message, final String url) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setTitle(context.getString(R.string.find_a_new_version));
        builder.setMessage(message);
        CustomDialog dialog = builder.setPositiveButton(context.getString(R.string.start_updating), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                CommonNotices.showMessage(context, context.getString(R.string.start_updating));
                downloadUpdate(url);
            }
        }).setNegativeButton(context.getString(R.string.cancel_updating), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CommonNotices.showMessage(context, context.getString(R.string.cancel_updating));
                dialog.dismiss();
            }
        }).create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.show();
    }

    public static void firmwareLocal() {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), new RequestFirmwareLocalCheck(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }

    public static File getUpdateZipFile() {
        return new File(OTAManager.LOCAL_PATH_SDCARD);
    }

    public static void saveScreen(String sourcePicPathString, String targetPicPathString) {
        WindowManager wm = (WindowManager) DRApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        int fullScreenPhysicalHeight = wm.getDefaultDisplay().getHeight();
        int fullScreenPhysicalWidth = wm.getDefaultDisplay().getWidth();
        String targetFormat = ".png";
        String targetDir = Constants.STANDBY_PIC_DIRECTORY;
        Bitmap temp = BitmapFactory.decodeFile(sourcePicPathString).copy(Bitmap.Config.RGB_565, true);
        if (temp.getHeight() > temp.getWidth()) {
            temp = PicUtils.rotateBmp(temp, -90);
        }
        if ((temp.getWidth() != fullScreenPhysicalHeight) || temp.getHeight() != fullScreenPhysicalWidth) {
            temp = Bitmap.createScaledBitmap(temp, fullScreenPhysicalHeight, fullScreenPhysicalWidth, true);
        }
        temp = PicUtils.convertToBlackWhite(temp);
        if (targetFormat.equalsIgnoreCase(".bmp")) {
            PicUtils.saveBmp(temp, targetDir, targetPicPathString, true);
        } else if (targetFormat.equalsIgnoreCase(".png")) {
            PicUtils.savePng(temp, targetDir, targetPicPathString, true);
        }
    }

    public static void installApk(Context context, String path) {
        File apkFile = new File(path);
        if (!apkFile.exists()) {
            return;
        }
        SilentInstall.installApk(context,path);
    }

    public static void updateApk(final boolean isUserChecked) {
        ApplicationUpdate appUpdate = getQueryAppUpdate(DRApplication.getInstance().getPackageName(), DRApplication.getInstance());
        List<ApplicationUpdate> list = new ArrayList<>();
        list.add(appUpdate);
        final ApplicationUpdateRequest applicationUpdateRequest = new ApplicationUpdateRequest(list);
        OTAManager.sharedInstance().getCloudStore().submitRequest(DRApplication.getInstance(), applicationUpdateRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                ApplicationUpdate applicationUpdate = applicationUpdateRequest.getApplicationUpdate();
                if (applicationUpdate != null) {
                    HaveNewVersionApkEvent haveNewVersionApkEvent = new HaveNewVersionApkEvent(applicationUpdate);
                    EventBus.getDefault().post(haveNewVersionApkEvent);
                } else if (isUserChecked) {
                    CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.without_new_version));
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

    public static void showNewApkDialog(final Context context, final String message, final String url) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(context);
        View inflate = View.inflate(context, R.layout.apk_update_view, null);
        TextView updateMsg = (TextView) inflate.findViewById(R.id.apk_update_message);
        final TextView progressBar = (TextView) inflate.findViewById(R.id.apk_progress);
        dialog = builder.setContentView(inflate).setTitle(context.getString(R.string.find_a_new_version))
                .setPositiveButton(context.getString(R.string.start_updating), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadAPK(context, url, message);
                        CommonNotices.showMessage(context, context.getString(R.string.start_updating));
                        dialog.dismiss();
                    }
                }).setNegativeButton(context.getString(R.string.cancel_updating), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommonNotices.showMessage(context, context.getString(R.string.cancel_updating));
                        dialog.dismiss();
                    }
                }).create();
        updateMsg.setText(message);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = (int)context.getResources().getDimension(R.dimen.new_apk_dialog_height);
        params.width = (int)context.getResources().getDimension(R.dimen.new_apk_dialog_width);
        window.setAttributes(params);
        dialog.show();
    }

    private static void downloadAPK(Context context, String url, String message) {
        startInstallApkActivity(context, url, message);
    }

    private static void downloadUpdate(String url) {
        EventBus.getDefault().post(new StartDownloadingEvent());
        OnyxDownloadManager downloadManager = OnyxDownloadManager.getInstance();
        BaseDownloadTask download = downloadManager.download(DRApplication.getInstance(), url, ApkUtils.getUpdateZipFile().getAbsolutePath(), Constants.UPDATE_ZIP, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    EventBus.getDefault().post(new UpdateDownloadSucceedEvent());
                    EventBus.getDefault().post(new HideLoadingProgressEvent());
                }
            }
        });
        downloadManager.startDownload(download);
    }


    public static void startInstallApkActivity(Context context,String url,String message) {
        Intent intent = new Intent();
        String pakName = "com.onyx.android.update";
        if(!Utils.isAppInstalled(context,pakName)){
            CommonNotices.showMessage(context,context.getString(R.string.update_application_not_install));
            return;
        }
        String className = "com.onyx.android.update.MainActivity";
        intent.setClassName(pakName, className);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.DOWNLOAD_URL,url);
        intent.putExtra(Constants.UPDATE_MESSAGE,message);
        context.startActivity(intent);
    }
}
