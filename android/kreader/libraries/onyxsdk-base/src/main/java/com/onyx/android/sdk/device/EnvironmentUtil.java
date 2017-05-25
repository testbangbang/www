/**
 * 
 */
package com.onyx.android.sdk.device;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * duplication of android.os.Environment to provide device specific function
 * 
 * @author joy
 *
 */
public class EnvironmentUtil
{
    private static final String TAG = "EnvironmentUtil";
    
    public static final File EXTERNAL_STORAGE_ANDROID_DATA_DIRECTORY = new File(new File(getExternalStorageDirectory(),
            "Android"), "data");
    
    public static File getStorageRootDirectory()
    {
        return Device.currentDevice.getStorageRootDirectory();
    }
    
    /**
     * wrapper of android.os.Environment.getExternalStorageDirectory
     * 
     * @return
     */
    public static File getExternalStorageDirectory()
    {
        return Device.currentDevice.getExternalStorageDirectory();
    }
    
    /**
     * Returns the path for android-specific data on the SD card.
     */
    public static File getExternalStorageAndroidDataDir()
    {
        return EXTERNAL_STORAGE_ANDROID_DATA_DIRECTORY;
    }
    
    /**
     * Generates the raw path to an application's data
     */
    public static File getExternalStorageAppDataDirectory(String packageName) {
        return new File(EXTERNAL_STORAGE_ANDROID_DATA_DIRECTORY, packageName);
    }
    
    /**
     * Generates the path to an application's files.
     */
    public static File getExternalStorageAppFilesDirectory(String packageName) {
        return new File(new File(EXTERNAL_STORAGE_ANDROID_DATA_DIRECTORY,
                packageName), "files");
    }
    
    /**
     * Generates the path to an application's cache.
     */
    public static File getExternalStorageAppCacheDirectory(String packageName) {
        return new File(new File(EXTERNAL_STORAGE_ANDROID_DATA_DIRECTORY,
                packageName), "cache");
    }
    
    /**
     * directory of removable SD card, can be different from getExternalStorageDirectory() according to devices
     * 
     * @return
     */
    public static File getRemovableSDCardDirectory()
    {
        return Device.currentDevice.getRemovableSDCardDirectory();
    }
    
    public static boolean isFileOnRemovableSDCard(File file)
    {
        return Device.currentDevice.isFileOnRemovableSDCard(file);
    }
    
    /**
     * never return null;
     * 
     * @param context
     * @return
     */
    public static String getDeviceSerial(Context context)
    {
        UUID uuid = null;

        final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        // Use the Android ID unless it's broken, in which case fallback on deviceId,
        // unless it's not available, then fallback on a random number which we store
        // to a prefs file
        try {
            if (!"9774d56d682e549c".equals(androidId)) {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
            } else {
                final String deviceId = ((TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                uuid = deviceId!=null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
            }
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "exception", e);
            uuid = UUID.randomUUID();
        }
        
        return uuid.toString();
    }

    public static String getRemovableSDCardCid() {
        Reader reader = null;
        String devicePath = null;
        String path = "/sys/block/mmcblk0/device/type";
        String[] mmcSeries = new String[]{"mmcblk0", "mmcblk1", "mmcblk2"};
        for (String mmc : mmcSeries) {
            devicePath = getStorageDevice(path.replaceFirst(mmcSeries[0], mmc), "sd");
            if (StringUtils.isNotBlank(devicePath)) {
                break;
            }
        }
        if (StringUtils.isNullOrEmpty(devicePath)) {
            Log.w(TAG, "sdCard devicePath is null");
            return null;
        }
        String cid = null;
        try {
            reader = new FileReader(devicePath + "cid");
            cid = new BufferedReader(reader).readLine();
            Log.i(TAG, "SDCard cid:" + cid);
        } catch (Exception e) {
        } finally {
            FileUtils.closeQuietly(reader);
        }
        return cid;
    }

    public static String getStorageDevice(String devicePath, String deviceType) {
        Reader reader = null;
        try {
            reader = new FileReader(devicePath);
            if (new BufferedReader(reader).readLine().toLowerCase().contentEquals(deviceType)) {
                return devicePath.replaceAll("type", "");
            }
        } catch (Exception e) {
        } finally {
            FileUtils.closeQuietly(reader);
        }
        return null;
    }

    public static boolean isExternalStorageDirectory(final String path) {
        return path.equals(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath());
    }

    public static boolean isExternalStorageDirectory(Context context, final Intent intent) {
        String mountPath = FileUtils.getRealFilePathFromUri(context, intent.getData());
        return isExternalStorageDirectory(mountPath);
    }

    public static boolean isRemovableSDDirectory(final String path) {
        return path.equals(EnvironmentUtil.getRemovableSDCardDirectory().getAbsolutePath());
    }

    public static boolean isRemovableSDDirectory(Context context, final Intent intent) {
        String mountPath = FileUtils.getRealFilePathFromUri(context, intent.getData());
        return isRemovableSDDirectory(mountPath);
    }
}
