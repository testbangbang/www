package com.onyx.android.libsetting.manager;

import android.app.DownloadManager;

/**
 * Created with IntelliJ IDEA. User: john Date: 6/12/13 Time: 10:21 AM To change
 * this template use File | Settings | File Templates.
 */
public class OTAAdmin {

    private static final String TAG = OTAAdmin.class.getSimpleName();
    private static OTAAdmin instance;
    private OTAAdmin updateFirmware;
    private boolean inCheckingNewFirmware = false;
    static private boolean installationSaved = false;

    private DownloadManager downloadManager;

    // unit : MB
    static private final int MINIMUM_STORAGE_SIZE_REQUEST_FOR_UPGRADE = 250;
    static private final int MINIMUM_BATTERY_REQUEST_FOR_UPGRADE = 50;
    static private final int BATTERY_NO_CHARGING = 3;

    public static final String OTA_SERVICE_PACKAGE = "com.onyx.android.onyxotaservice";
    public static final String OTA_SERVICE_ACTIVITY = "com.onyx.android.onyxotaservice.OtaInfoActivity";

    public static final String OTA_SERVICE_PACKAGE_PATH_KEY = "updatePath";
//    public static final String LOCAL_PATH_SDCARD = DeviceInfo.currentDevice.getExternalStorageDirectory()
//            .getAbsolutePath() + "/update.zip";
//    public static final String LOCAL_PATH_EXTSD = DeviceInfo.currentDevice.getRemovableSDCardDirectory()
//            .getAbsolutePath() + "/update.zip";

    static public abstract class FirmwareCallback {
        abstract public void stateChanged(int state, long finished, long total, long percentage);

        public void checkLocalFirmwareFinished(final String path, boolean success) {
        }
    }

    static public OTAAdmin sharedInstance() {
        if (instance == null) {
            instance = new OTAAdmin();
        }
        return instance;
    }

}
