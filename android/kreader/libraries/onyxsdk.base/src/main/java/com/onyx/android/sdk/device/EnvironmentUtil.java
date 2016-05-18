/**
 * 
 */
package com.onyx.android.sdk.device;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

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
        return DeviceInfo.currentDevice.getStorageRootDirectory();
    }
    
    /**
     * wrapper of android.os.Environment.getExternalStorageDirectory
     * 
     * @return
     */
    public static File getExternalStorageDirectory()
    {
        return DeviceInfo.currentDevice.getExternalStorageDirectory();
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
        return DeviceInfo.currentDevice.getRemovableSDCardDirectory();
    }
    
    public static boolean isFileOnRemovableSDCard(File file)
    {
        return DeviceInfo.currentDevice.isFileOnRemovableSDCard(file);
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
}
