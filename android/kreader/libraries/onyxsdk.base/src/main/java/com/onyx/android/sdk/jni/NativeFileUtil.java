/**
 * 
 */
package com.onyx.android.sdk.jni;

/**
 * @author joy
 *
 */
public class NativeFileUtil
{
    static {
        System.loadLibrary("onyxfileutil");
    }
    
    /**
     * the number of seconds since 00:00, Jan 1 1970 UTC
     * 
     * @param path
     * @return
     */
    public static native long getChangeTimestamp(String path);
}
