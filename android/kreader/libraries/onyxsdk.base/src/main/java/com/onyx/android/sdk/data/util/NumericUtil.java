/**
 * 
 */
package com.onyx.android.sdk.data.util;

import android.os.Build;


/**
 * @author joy
 *
 */
public abstract class NumericUtil
{
    public static boolean equalsAlmost(double a, double b)
    {
        return Math.abs(a - b) <= 0.00001;
    }
    
    public static String toString(double d)
    {
        // work around for know issue:
        // http://code.google.com/p/android/issues/detail?id=14302
        if (Build.VERSION.SDK_INT == 9 || Build.VERSION.SDK_INT == 10) {
            return Float.toString((float)d);
        }
        else {
            return Double.toString(d);
        }
    }
}
