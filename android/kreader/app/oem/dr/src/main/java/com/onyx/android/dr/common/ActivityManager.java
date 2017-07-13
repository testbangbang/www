package com.onyx.android.dr.common;

import android.content.Context;
import android.content.Intent;

import com.onyx.android.dr.activity.ApplicationsActivity;
import com.onyx.android.dr.activity.LoginActivity;
import com.onyx.android.dr.reader.data.OpenBookParam;
import com.onyx.android.dr.reader.utils.ReaderUtil;
import com.onyx.android.sdk.data.model.Metadata;

/**
 * Created by hehai on 17-6-29.
 */

public class ActivityManager {
    public static void startLoginActivity(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void openBook(Context context, Metadata metadata,String localPath){
        OpenBookParam openBookParam = new OpenBookParam();
        openBookParam.setBookName(metadata.getName());
        openBookParam.setLocalPath(localPath);
        ReaderUtil.openBook(context, openBookParam);
    }

    public static void startApplicationsActivity(Context context) {
        Intent intent = new Intent(context, ApplicationsActivity.class);
        context.startActivity(intent);
    }
}
