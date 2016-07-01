/**
 * 
 */
package com.onyx.android.sdk.utils;

import android.content.Intent;
import android.content.IntentFilter;

/**
 * @author joy
 *
 */
public class IntentFilterFactory
{
    public static final String ACTION_MEDIA_SCANNED = "com.onyx.android.intent.action.ACTION_MEDIA_SCANNED";
    
    public static final String ACTION_OPEN_FRONT_LIGHT = "OPEN_FRONT_LIGHT";
    public static final String ACTION_CLOSE_FRONT_LIGHT = "CLOSE_FRONT_LIGHT";
    public static final String INTENT_FRONT_LIGHT_VALUE = "FRONT_LIGHT_VALUE";

    public static final String ACTION_GET_APPLICATION_PREFERENCE = "com.onyx.android.sdk.data.IntentFactory.ACTION_GET_APPLICATION_PREFERENCE";
    public static final String ACTION_EXTRACT_METADATA = "com.onyx.android.sdk.data.IntentFactory.ACTION_EXTRACT_METADATA";
    public static final String ACTION_GET_TOC = "com.onyx.android.sdk.data.IntentFactory.ACTION_GET_TOC";
    
    private static final IntentFilter SDCARD_UNMOUNTED_FILTER;
    private static final IntentFilter MEDIA_MOUNTED_FILTER;
    private static final IntentFilter MEDIA_SCANNED_FILTER;
    private static final IntentFilter BOOT_COMPLETED_FILTER;
    private static final IntentFilter OPEN_CLOSE_FRONT_LIGHT_FILTER;
    
    private static IntentFilter mIntentFilter = null;
    
    static {
        SDCARD_UNMOUNTED_FILTER = new IntentFilter(); 
        SDCARD_UNMOUNTED_FILTER.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        SDCARD_UNMOUNTED_FILTER.addAction(Intent.ACTION_MEDIA_REMOVED);
        SDCARD_UNMOUNTED_FILTER.addAction(Intent.ACTION_MEDIA_SHARED);
        SDCARD_UNMOUNTED_FILTER.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
        SDCARD_UNMOUNTED_FILTER.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        SDCARD_UNMOUNTED_FILTER.addDataScheme("file"); 
        
        MEDIA_MOUNTED_FILTER = new IntentFilter();
        MEDIA_MOUNTED_FILTER.addAction(Intent.ACTION_MEDIA_MOUNTED);
        MEDIA_MOUNTED_FILTER.addDataScheme("file"); 
        
        MEDIA_SCANNED_FILTER = new IntentFilter();
        MEDIA_SCANNED_FILTER.addAction(ACTION_MEDIA_SCANNED);
        
        BOOT_COMPLETED_FILTER = new IntentFilter();
        BOOT_COMPLETED_FILTER.addAction(Intent.ACTION_BOOT_COMPLETED);
        
        OPEN_CLOSE_FRONT_LIGHT_FILTER = new IntentFilter();
        OPEN_CLOSE_FRONT_LIGHT_FILTER.addAction(ACTION_OPEN_FRONT_LIGHT);
        OPEN_CLOSE_FRONT_LIGHT_FILTER.addAction(ACTION_CLOSE_FRONT_LIGHT);
    }
    
    public static IntentFilter getSDCardUnmountedFilter()
    {
        return SDCARD_UNMOUNTED_FILTER;
    }
    
    public static IntentFilter getMediaMountedFilter()
    {
        return MEDIA_MOUNTED_FILTER;
    }
    
    public static IntentFilter getMediaScannedFilter()
    {
        return MEDIA_SCANNED_FILTER;
    }
    
    public static IntentFilter getBootCompletedFilter()
    {
        return BOOT_COMPLETED_FILTER;
    }

    public static IntentFilter getIntentFilterFrontPreferredApplications()
    {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ACTION_GET_APPLICATION_PREFERENCE);

        return mIntentFilter;
    }

    public static IntentFilter getOpenAndCloseFrontLightFilter()
    {
        return OPEN_CLOSE_FRONT_LIGHT_FILTER;
    }
}
