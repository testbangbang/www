/**
 * 
 */
package com.onyx.android.sdk.ui.util;

import android.view.Window;
import android.view.WindowManager;

/**
 * @author joy
 *
 */
public abstract class WindowUtil
{
    public static boolean isFullScreen(Window window)
    {
        int v = window.getAttributes().flags;
        return (v & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
    }
}
