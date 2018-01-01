package com.onyx.jdread.setting.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.utils.DeviceInfoUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.PackageUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.setting.request.RxFirmwareUpdateRequest;
import com.onyx.jdread.util.Utils;

import java.io.File;
import java.util.Locale;

/**
 * Created by li on 2017/12/25.
 */

public class UpdateUtil {
    public static final String SYSTEM_UPDATE_TAG = "update.zip";
    public static final String APK_UPDATE_TAG = "JDRead.apk";
    public static final String APK_PATH = "apk_path";
    public static final String VERSION_LAUNCHER = "JDR6.L.";
    public static final String VERSION_SYSTEM = "JDR6.S.";
    public static final String DOWNLOAD_UPDATE_CODE = "download_update_code";
    public static final int UPDATE_CODE = 1;
    public static final int DEFAULT_CODE = 0;

    public static String checkLocalPackage() {
        String path = null;
        if (FileUtils.fileExist(OTAManager.LOCAL_PATH_SDCARD)) {
            path = OTAManager.LOCAL_PATH_SDCARD;
        }

        if (FileUtils.fileExist(OTAManager.LOCAL_PATH_EXTSD)) {
            path = OTAManager.LOCAL_PATH_EXTSD;
        }
        return path;
    }

    public static File getUpdateZipFile() {
        return new File(OTAManager.LOCAL_PATH_SDCARD);
    }

    public static String getApkUpdateFile() {
        return Environment.getExternalStorageDirectory() + File.separator + APK_UPDATE_TAG;
    }

    public static boolean deleteUpdateZipFile() {
        return getUpdateZipFile().delete();
    }

    public static RxFirmwareUpdateRequest cloudFirmwareCheckRequest(Context context, CloudManager cloudManager) {
        Point point = DeviceInfoUtil.getScreenResolution(context);
        Firmware firmware = Firmware.currentFirmware();
        firmware.lang = Locale.getDefault().toString();
        firmware.widthPixels = point.x;
        firmware.heightPixels = point.y;
        Device device = Device.updateCurrentDeviceInfo(context);
        if(device != null) {
            firmware.deviceMAC = device.macAddress;
        }
        return new RxFirmwareUpdateRequest(cloudManager, firmware);
    }

    public static ApplicationUpdate getQueryAppUpdate() {
        ApplicationUpdate update = new ApplicationUpdate();
        update.packageName = JDReadApplication.getInstance().getPackageName();
        update.versionCode = PackageUtils.getAppVersionCode(JDReadApplication.getInstance());
        update.versionName = PackageUtils.getAppVersionName(JDReadApplication.getInstance());
        update.type = PackageUtils.getAppType(JDReadApplication.getInstance());
        update.channel = PackageUtils.getAppChannel(JDReadApplication.getInstance());
        update.platform = PackageUtils.getAppPlatform(JDReadApplication.getInstance());
        update.size = PackageUtils.getApkFileSize(JDReadApplication.getInstance(), JDReadApplication.getInstance().getPackageName());
        update.model = Build.MODEL;
        Device device = Device.updateCurrentDeviceInfo(JDReadApplication.getInstance());
        if (device != null) {
            update.macAddress = device.macAddress;
        }
        return update;
    }

    public static void startUpdateApkActivity(Context context, String path) {
        Intent intent = new Intent();
        String packageName = "com.onyx.android.update";
        String className = "com.onyx.android.update.MainActivity";
        if (!isAppInstalled(context, packageName)) {
            Utils.showMessage(context.getResources().getString(R.string.update_application_not_install));
        }
        intent.setClassName(packageName, className);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(APK_PATH, path);
        context.startActivity(intent);
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public static String getAPPVersionName(Context context) {
        String currentVersionCode = "";
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String versionName = info.versionName;
            currentVersionCode = VERSION_LAUNCHER + versionName.substring(versionName.lastIndexOf("-") + 1, versionName.length());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersionCode;
    }
}
