/**
 * 
 */
package com.onyx.android.sdk.data.compatability;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.onyx.android.sdk.data.OnyxDictionaryInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;


/**
 * @author joy
 *
 */
public class OnyxSysCenter
{
    @SuppressWarnings("unused")
    private static final String TAG = "OnyxSysCenter";
    
    public static final String PROVIDER_AUTHORITY = "com.onyx.android.sdk.OnyxSysProvider";
    
    private static final String KEY_DEFAULT_FONT_FAMILY = "sys.defaut_font_family";
    private static final String KEY_SCREEN_UPDATE_GC_INTERVAL = "sys.screen_update_gc_interval";
    private static final String KEY_SUSPEND_INTERVAL = "sys.suspend_interval";
    private static final String KEY_SHUTDOWN_INTERVAL = "sys.shutdown_interval";
    private static final String KEY_TIMEZONE = "sys.timezone";
    private static final String KEY_DICT = "sys.dict";
    private static final String KEY_DICT_RESOURCES_PATH = "sys.dict_resources_path";
    private static final String KEY_OPEN_LAST_READING_DOCUMENT = "sys.open_last_reading";
    private static final String KEY_BOOK_SCAN_INTERNAL_DIRECTORIES = "sys.book_scan_internal_directories";
    private static final String KEY_BOOK_SCAN_EXTSD_DIRECTORIES = "sys.book_scan_extsd_directories";
    private static final String KEY_STARTUP_CONFIGURATION_FLAG = "sys.startup_configuraton_flag";
    private static final String KEY_SCRIBBLE_THICKNESS = "sys.scribble_thickness";
    private static final String KEY_USER_MANUAL_DEPLOYED_FLAG = "sys.user_manual_deployed_flag";

    
    /**
     * return null when fail
     * @return
     */
    public static String getDefaultFontFamily(Context context)
    {
        return getStringValue(context, KEY_DEFAULT_FONT_FAMILY);
    }
    public static boolean setDefaultFontFamily(Context context, String fontFamily)
    {
        return setStringValue(context, KEY_DEFAULT_FONT_FAMILY, fontFamily);
    }
    
    /**
     * return -1 when fail
     * @return
     */
    public static int getScreenUpdateGCInterval(Context context)
    {
        return getIntValue(context, KEY_SCREEN_UPDATE_GC_INTERVAL);
    }
    
    /**
     * if getScreenUpdateGCInterval() failed, return defaultValue instead
     * 
     * @param defaultValue
     * @return
     */
    public static int getScreenUpdateGCInterval(Context context, int defaultValue)
    {
        int value = getScreenUpdateGCInterval(context);
        if (value == -1) {
            value = defaultValue;
        }
        
        return value;
    }
    
    public static boolean setScreenUpdateGCInterval(Context context, int interval)
    {
        return setIntValue(context, KEY_SCREEN_UPDATE_GC_INTERVAL, interval);
    }
    
    /**
     * return -1 when fail
     * @return
     */
    public static int getSuspendInterval(Context context)
    {
        return getIntValue(context, KEY_SUSPEND_INTERVAL);
    }
    public static boolean setSuspendInterval(Context context, int interval)
    {
        return setIntValue(context, KEY_SUSPEND_INTERVAL, interval);
    }
    
    /**
     * return -1 when fail
     * @return
     */
    public static int getShutdownInterval(Context context)
    {
        return getIntValue(context, KEY_SHUTDOWN_INTERVAL);
    }
    public static boolean setShutdownInterval(Context context, int interval)
    {
        return setIntValue(context, KEY_SHUTDOWN_INTERVAL, interval);
    }
    
    /**
     * return null when fail
     * @return
     */
    public static String getTimezone(Context context)
    {
        return getStringValue(context, KEY_TIMEZONE);
    }
    public static boolean setTimezone(Context context, String timezone)
    {

        return setStringValue(context, KEY_TIMEZONE, timezone);
    }
    
    /**
     * only return dicts which are available on the device
     * 
     * @param context
     * @return
     */
    public static OnyxDictionaryInfo[] getAvailableDictionaryList(Context context)
    {
        OnyxDictionaryInfo[] array_dict = OnyxDictionaryInfo.getDictionaryList();
        
        ArrayList<OnyxDictionaryInfo> dicts = new ArrayList<OnyxDictionaryInfo>();
        for (OnyxDictionaryInfo d : array_dict) {
            if (OnyxDictionaryInfo.isDictionaryAvailable(context, d)) {
                dicts.add(d);
            }
        }
        
        OnyxDictionaryInfo[] result = new OnyxDictionaryInfo[dicts.size()];
        return dicts.toArray(result);
    }
    
    /**
     * return null if not found
     * 
     * @return
     */
    public static OnyxDictionaryInfo getDictionary(Context context) {
        String dict_id = getStringValue(context, KEY_DICT);
        if (dict_id == null) {
            return firstAvailableDictionary(context);
        }
        OnyxDictionaryInfo info = OnyxDictionaryInfo.findDict(dict_id);
        if (info == null) {
            return firstAvailableDictionary(context);
        }
        return info;
    }

    public static OnyxDictionaryInfo firstAvailableDictionary(final Context context) {
        for(OnyxDictionaryInfo info : OnyxDictionaryInfo.getDictionaryList()) {
            if (OnyxDictionaryInfo.isDictionaryAvailable(context, info)) {
                return info;
            }
        }
        return null;
    }

    public static boolean setDictionary(Context context, OnyxDictionaryInfo dict)
    {
        return setStringValue(context, KEY_DICT, dict.id);
    }

    public static OnyxKeyValueItem getDictionaryResourcesPath(Context context)
    {
        OnyxKeyValueItem item = queryKeyValueItem(context, KEY_DICT_RESOURCES_PATH);
        if (item == null) {
            return null;
        }
        return item;
    }

    public static boolean setDictionaryResourcesPath(Context context, String path) {
        return setStringValue(context, KEY_DICT_RESOURCES_PATH, path);
    }

    public static boolean setFileType(Context context, String key, String value)
    {
        key = key.toLowerCase(Locale.getDefault());
        return setStringValue(context, key, value);
    }

    public static String getFileType(Context context, String key)
    {
        key = key.toLowerCase(Locale.getDefault());
        return getStringValue(context, key);
    }

    public static boolean setOpenLastReadDocument(Context context, boolean b) {
        return setStringValue(context, KEY_OPEN_LAST_READING_DOCUMENT, "" + b);
    }

    public static boolean getOpenLastReadDocument(Context context) {
        return Boolean.valueOf(getStringValue(context, KEY_OPEN_LAST_READING_DOCUMENT));
    }
    
    public static boolean setBookScanInternalDirectories(Context context, Collection<String> dirs)
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String d : dirs) {
            if (first) {
                first = false;
            }
            else {
                final String SEPARATOR = "|";
                sb.append(SEPARATOR);
            }
            sb.append(d);
        }

        return setStringValue(context, KEY_BOOK_SCAN_INTERNAL_DIRECTORIES, sb.toString());
    }
    
    /**
     * return null if failed, empty collection if no directory is specified.
     * 
     * @param context
     * @return
     */
    public static ArrayList<String> getBookScanInternalDirectories(Context context)
    {
        String str = getStringValue(context, KEY_BOOK_SCAN_INTERNAL_DIRECTORIES);
        if (str == null) {
            return null;
        }
        
        if (str.isEmpty()) {
            return new ArrayList<String>();
        }

        final String SEPARATOR = "\\|";
        String[] array = str.split(SEPARATOR);

        ArrayList<String> dirs = new ArrayList<String>();
        dirs.addAll(Arrays.asList(array));
        return dirs;
    }
    
    public static boolean setBookScanExtSDDirectories(Context context, Collection<String> dirs)
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String d : dirs) {
            if (first) {
                first = false;
            }
            else {
                final String SEPARATOR = "|";
                sb.append(SEPARATOR);
            }
            sb.append(d);
        }
        
        return setStringValue(context, KEY_BOOK_SCAN_EXTSD_DIRECTORIES, sb.toString());
    }
    
    /**
     * return null if failed, empty collection if no directory is specified.
     * 
     * @param context
     * @return
     */
    public static ArrayList<String> getBookScanExtSDDirectories(Context context)
    {
        String str = getStringValue(context, KEY_BOOK_SCAN_EXTSD_DIRECTORIES);
        if (str == null) {
            return null;
        }
        
        if (str.isEmpty()) {
            return new ArrayList<String>();
        }

        final String SEPARATOR = "\\|";
        String[] array = str.split(SEPARATOR);

        ArrayList<String> dirs = new ArrayList<String>();
        dirs.addAll(Arrays.asList(array));
        return dirs;
    }

    public static boolean setStartupConfigurationFlag(Context context, int flag) {
        return setIntValue(context, KEY_STARTUP_CONFIGURATION_FLAG, flag);
    }

    public static int getStartupConfigurationFlag(Context context) {
        return getIntValue(context, KEY_STARTUP_CONFIGURATION_FLAG);
    }

    public static boolean setUserManualDeployed(Context context, boolean deployed) {
        return setIntValue(context, KEY_USER_MANUAL_DEPLOYED_FLAG, deployed? 1 : 0);
    }

    public static int getUserManualDeployedFlag(Context context) {
        return getIntValue(context, KEY_USER_MANUAL_DEPLOYED_FLAG);
    }

    public static boolean setScribbleThickness(Context context, int thickness) {
        return setIntValue(context, KEY_SCRIBBLE_THICKNESS, thickness);
    }

    public static int getScribbleThickness(Context context, int defaultValue) {
        int value = getIntValue(context, KEY_SCRIBBLE_THICKNESS);
        if (value == -1) {
            value = defaultValue;
        }
        return value;
    }
    
    /**
     * return null when fail
     * @param key
     * @return
     */
    public static String getStringValue(Context context, String key)
    {
        OnyxKeyValueItem item = queryKeyValueItem(context, key);
        if (item == null) {
            return null;
        }
        
        return item.getValue();
    }

    public static boolean setStringValue(Context context, String key, String value)
    {
        OnyxKeyValueItem item = queryKeyValueItem(context, key);
        if (item == null) {
            item = new OnyxKeyValueItem();
            item.setKey(key);
            item.setValue(value);

            return insert(context, item);

        }
        else {
            String old = item.getValue();
            item.setValue(value);
            if (!update(context, item)) {
                item.setValue(old);
                return false;
            }
            return true;
        }
    }
    
    /**
     * return -1 when fail
     * @param key
     * @return
     */
    private static int getIntValue(Context context, String key)
    {
        String value = getStringValue(context, key);
        if (value == null) {
            return -1;
        }
        
        return Integer.parseInt(value);
    }
    private static boolean setIntValue(Context context, String key, int value)
    {
        return setStringValue(context, key, String.valueOf(value));
    }
    
    private static OnyxKeyValueItem queryKeyValueItem(Context context, String key)
    {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(OnyxKeyValueItem.CONTENT_URI, null,
                    OnyxKeyValueItem.Columns.KEY + "=?", new String[] { key }, null);
            if (c == null) {
                return null;
            }
            
            if (c.moveToFirst()) {
                return OnyxKeyValueItem.Columns.readColumnData(c);
            }
            
            return null;
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
    }
    
    private static boolean insert(Context context, OnyxKeyValueItem item)
    {
        Uri result = context.getContentResolver().insert(OnyxKeyValueItem.CONTENT_URI,
                OnyxKeyValueItem.Columns.createColumnData(item));
        if (result == null) {
            return false;
        }
        
        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }
        item.setId(Long.parseLong(id));
        
        return true;
    }
    private static boolean update(Context context, OnyxKeyValueItem item)
    {
        Uri row = Uri.withAppendedPath(OnyxKeyValueItem.CONTENT_URI, String.valueOf(item.getId()));
        int count = context.getContentResolver().update(row,
                OnyxKeyValueItem.Columns.createColumnData(item), null, null);
        if (count <= 0) {
            return false;
        }
        
        assert(count == 1);
        return true;
    }
    @SuppressWarnings("unused")
    private static boolean delete(Context context, OnyxKeyValueItem item)
    {
        Uri row = Uri.withAppendedPath(OnyxKeyValueItem.CONTENT_URI, String.valueOf(item.getId()));
        int count = context.getContentResolver().delete(row, null, null);
        if (count <= 0) {
            return false;
        }
        
        assert(count == 1);
        return true;
    }

    /*
     * When multiple applications call
     */
    public static String getStringValueFromDB(Context context, OnyxKeyValueItem item) {
        Uri row = Uri.withAppendedPath(OnyxKeyValueItem.CONTENT_URI, String.valueOf(item.getId()));
        Cursor cursor = context.getContentResolver().query(row, null, null, null, null);
        cursor.moveToFirst();
        String str = cursor.getString(0);
        cursor.close();
        return str;
    }
}
