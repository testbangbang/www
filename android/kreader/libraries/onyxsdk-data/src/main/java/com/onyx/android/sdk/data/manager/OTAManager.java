package com.onyx.android.sdk.data.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.request.cloud.FirmwareLocalCheckLegalityRequest;
import com.onyx.android.sdk.data.request.cloud.FirmwareUpdateRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.DeviceInfoUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by solskjaer49 on 2017/2/11 16:15.
 */

public class OTAManager {
    private static final String TAG = OTAManager.class.getSimpleName();
    private static OTAManager instance;
    private CloudStore cloudStore;

    private static final String OTA_SERVICE_PACKAGE = "com.onyx.android.onyxotaservice";
    private static final String OTA_SERVICE_ACTIVITY = "com.onyx.android.onyxotaservice.OtaInfoActivity";
    private static final String OTA_SERVICE_PACKAGE_PATH_KEY = "updatePath";

    public static final String LOCAL_PATH_SDCARD = getLocalSdcardPath();
    public static final String CLOUD_PATH_SDCARD = getCloudSdcardPath();
    public static final String LOCAL_PATH_EXTSD = getLocalExtsdPath();

    /**
     * Temp empty implement here.
    */
    private void preFirmwareUpdate() {

    }

    public void startFirmwareUpdate(Context context, String path) {
        preFirmwareUpdate();
        Intent i = new Intent();
        i.putExtra(OTA_SERVICE_PACKAGE_PATH_KEY, path);
        i.setClassName(OTA_SERVICE_PACKAGE, OTA_SERVICE_ACTIVITY);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    static public OTAManager sharedInstance() {
        if (instance == null) {
            instance = new OTAManager();
        }
        return instance;
    }

    public static FirmwareLocalCheckLegalityRequest localFirmwareCheckRequest(Context context) {
        List<String> pathList = new ArrayList<>();
        pathList.add(LOCAL_PATH_SDCARD);
        pathList.add(LOCAL_PATH_EXTSD);
        return new FirmwareLocalCheckLegalityRequest(pathList);
    }

    public static FirmwareUpdateRequest cloudFirmwareCheckRequest(Context context) {
        Point point = DeviceInfoUtil.getScreenResolution(context);
        Firmware firmware = Firmware.currentFirmware();
        firmware.lang = Locale.getDefault().toString();
        firmware.widthPixels = point.x;
        firmware.heightPixels = point.y;
        Device device = Device.updateCurrentDeviceInfo(context);
        if (device != null) {
            firmware.deviceMAC = device.macAddress;
        }
        return new FirmwareUpdateRequest(firmware);
    }

    public void submitRequest(final Context context, final BaseCloudRequest request, final BaseCallback callback) {
        getCloudStore().submitRequest(context, request, callback);
    }

    public CloudStore getCloudStore() {
        if (cloudStore == null) {
            cloudStore = new CloudStore().setCloudConf(CloudConf.create(Constant.ONYX_HOST_BASE,
                    Constant.ONYX_API_BASE,
                    Constant.DEFAULT_CLOUD_STORAGE));
        }
        return cloudStore;
    }

    public IndexService createIndexService(Context context) {
        IndexService authService = new IndexService();
        authService.mac = NetworkUtil.getMacAddress(context);
        return authService;
    }

    static private String getLocalSdcardPath() {
        return EnvironmentUtil.getExternalStorageDirectory() + File.separator +
                com.onyx.android.sdk.device.Device.currentDevice.getUpgradePackageName();
    }

    static private String getLocalExtsdPath() {
        return EnvironmentUtil.getRemovableSDCardDirectory() + File.separator +
                com.onyx.android.sdk.device.Device.currentDevice.getUpgradePackageName();
    }

    static private String getCloudSdcardPath() {
        return EnvironmentUtil.getExternalStorageDirectory() + File.separator +
                "cloud." + com.onyx.android.sdk.device.Device.currentDevice.getUpgradePackageName();
    }

    public static String getLocalUpdatePagePath() {
        if (FileUtils.fileExist(LOCAL_PATH_SDCARD)) {
            return LOCAL_PATH_SDCARD;
        }
        if (FileUtils.fileExist(LOCAL_PATH_EXTSD)) {
            return LOCAL_PATH_EXTSD;
        }
        return null;
    }
}
