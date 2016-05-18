/**
 * 
 */
package com.onyx.android.sdk.reader;

import java.io.File;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import com.onyx.android.sdk.data.util.ActivityUtil;

/**
 * @author joy
 *
 */
public class ReaderRemoteServiceUtil
{
    public static final ComponentName ONYX_RAEDER_COMPONENT = new ComponentName("com.onyx.android.reader", "com.onyx.android.reader.ViewerActivity"); 
    public static final String ACTION_START_READER_WITH_SERVICE = "com.onyx.android.sdk.reader.ReaderRemoteServiceUtil.StartReaderWithService";
    public static final String REMOTE_SERVICE_TAG = "com.onyx.android.sdk.reader.ReaderRemoteServiceUtil.RemoteService";
    
    public static boolean startReaderWithService(Activity remoteHost, ComponentName remoteService, File file)
    {
        Intent i = new Intent();
        i.setAction(ACTION_START_READER_WITH_SERVICE);
        i.setComponent(ONYX_RAEDER_COMPONENT);
        i.putExtra(REMOTE_SERVICE_TAG, remoteService);
        i.setData(Uri.fromFile(file));
        
        return ActivityUtil.startActivitySafely(remoteHost, i);
    }

}
