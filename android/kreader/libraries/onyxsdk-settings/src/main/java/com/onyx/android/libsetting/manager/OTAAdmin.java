package com.onyx.android.libsetting.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;

import com.onyx.android.libsetting.SettingManager;
import com.onyx.android.libsetting.action.CheckCloudFirmwareLegalityAction;
import com.onyx.android.libsetting.action.CheckLocalFirmwareLegalityAction;
import com.onyx.android.libsetting.util.DeviceInfoUtil;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.model.OTAFirmware;
import com.onyx.android.sdk.data.request.cloud.FirmwareUpdateRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by solskjaer49 on 2017/2/11 16:15.
 */

public class OTAAdmin {
    private static final String TAG = OTAAdmin.class.getSimpleName();
    private static OTAAdmin instance;

    private static final String OTA_SERVICE_PACKAGE = "com.onyx.android.onyxotaservice";
    private static final String OTA_SERVICE_ACTIVITY = "com.onyx.android.onyxotaservice.OtaInfoActivity";
    private static final String OTA_SERVICE_PACKAGE_PATH_KEY = "updatePath";

    private static final String UPDATE_FILE_NAME = "update.zip";
    public static final String LOCAL_PATH_SDCARD = DeviceInfoUtil.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator + UPDATE_FILE_NAME;
    public static final String LOCAL_PATH_EXTSD = DeviceInfoUtil.getRemovableSDCardDirectory()
            .getAbsolutePath() + File.separator + UPDATE_FILE_NAME;

    public interface FirmwareCheckCallback {
        void preCheck();

        void stateChanged(int state, long finished, long total, long percentage);

        /**
         * @param targetPath If not found any zip(success return false),targetPath is null.
         * @param success
         */
        void onPostCheck(final String targetPath, boolean success);
    }


    /**
     * TODO:should clean up all test resource before update,but wait further design with both new setting/launcher coop.
     * Temp empty implement here.
    */
    private void preFirmwareUpdate() {
//        ContentBrowserUtils.clearAllTestResource(OnyxOTAActivity.this, DeviceConfig.sharedInstance(this));
    }

    //TODO:maybe ota service pkg/activity should be custom?
    public void startFirmwareUpdate(Context context, String path) {
        preFirmwareUpdate();
        Intent i = new Intent();
        i.putExtra(OTA_SERVICE_PACKAGE_PATH_KEY, path);
        i.setClassName(OTA_SERVICE_PACKAGE, OTA_SERVICE_ACTIVITY);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    static public OTAAdmin sharedInstance() {
        if (instance == null) {
            instance = new OTAAdmin();
        }
        return instance;
    }

    public void checkLocalFirmware(Context context,final FirmwareCheckCallback callback) {
        List<String> pathList= new ArrayList<>();
        pathList.add(LOCAL_PATH_SDCARD);
        pathList.add(LOCAL_PATH_EXTSD);
        CheckLocalFirmwareLegalityAction action = new CheckLocalFirmwareLegalityAction(callback, pathList);
        action.execute(context, SettingManager.sharedInstance(), null);
    }

    public void checkCloudFirmware(Context context, final BaseCallback callback) {
        Point point = DeviceInfoUtil.getScreenResolution(context);
        Firmware firmware = Firmware.currentFirmware();
        firmware.lang = Locale.getDefault().toString();
        firmware.widthPixels = point.x;
        firmware.heightPixels = point.y;
        Device device = Device.updateCurrentDeviceInfo(context);
        if (device != null) {
            firmware.deviceMAC = device.macAddress;
        }
        CheckCloudFirmwareLegalityAction action = new CheckCloudFirmwareLegalityAction(firmware);
        action.execute(context, SettingManager.sharedInstance(), callback);
    }

    public OTAFirmware checkCloudOTAFirmware(Context context, BaseRequest request) {
        if (!(request instanceof FirmwareUpdateRequest)) {
            return null;
        }
        final FirmwareUpdateRequest updateRequest = (FirmwareUpdateRequest) request;
        Firmware firmware = updateRequest.getResultFirmware();
        if (firmware == null || CollectionUtils.isNullOrEmpty(firmware.downloadUrlList)) {
            return null;
        }
        final OTAFirmware otaFirmware = OTAFirmware.otaFirmware(firmware);
        if (otaFirmware == null || StringUtils.isNullOrEmpty(otaFirmware.url)) {
            return null;
        }
        return otaFirmware;
    }
}
