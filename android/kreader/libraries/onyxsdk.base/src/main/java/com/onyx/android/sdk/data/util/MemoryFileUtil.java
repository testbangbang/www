/**
 * 
 */
package com.onyx.android.sdk.data.util;

import java.io.FileDescriptor;
import java.lang.reflect.Method;

import android.os.Build;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * @author joy
 *
 */
public class MemoryFileUtil {
    private static final String TAG = "MemoryFileUtil";
    
    private static final int ICE_CREAM_SANDWICH = 14;
    
    private static final Method sMethodGetFileDescriptor;
    private static final Method sMethodGetParcelFileDescriptor;

    private static final Method sMethodParcelFileDescriptorDup;

    static {
        sMethodGetFileDescriptor = get("getFileDescriptor");

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            sMethodGetParcelFileDescriptor = get("getParcelFileDescriptor");
            sMethodParcelFileDescriptorDup = null;
        }
        else if (Build.VERSION.SDK_INT >= ICE_CREAM_SANDWICH) {
            sMethodParcelFileDescriptorDup = getParcelFileDescriptorDup();
            sMethodGetParcelFileDescriptor = null;
        } 
        else {
            sMethodGetParcelFileDescriptor = null;
            sMethodParcelFileDescriptorDup = null;
        }
    }

    public static ParcelFileDescriptor getParcelFileDescriptor(MemoryFile file) {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                return (ParcelFileDescriptor) sMethodGetParcelFileDescriptor.invoke(file);
            }
            else if (Build.VERSION.SDK_INT >= ICE_CREAM_SANDWICH) {
                return (ParcelFileDescriptor) sMethodParcelFileDescriptorDup.invoke(null, getFileDescriptor(file));
            }
            else {
                return null;
            }
        } catch (Throwable tr) {
            Log.e(TAG, "", tr);
        }
        
        return null;
    }

    public static FileDescriptor getFileDescriptor(MemoryFile file) {
        try {
            return (FileDescriptor) sMethodGetFileDescriptor.invoke(file);
        } catch (Throwable tr) {
            Log.e(TAG, "", tr);
        }
        
        return null;
    }

    private static Method get(String name) {
        try {
            return MemoryFile.class.getDeclaredMethod(name);
        } catch (NoSuchMethodException e) {
            Log.w(TAG, e);
        }
        
        return null;
    }

    private static Method getParcelFileDescriptorDup()
    {
        try {
            return ParcelFileDescriptor.class.getDeclaredMethod("dup", FileDescriptor.class);
        } catch (NoSuchMethodException e) {
            Log.w(TAG, e);
        }
        
        return null;
    }
}