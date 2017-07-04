package com.onyx.android.dr.common;

import android.content.Context;
import android.content.Intent;

import com.onyx.android.dr.activity.LoginActivity;

/**
 * Created by hehai on 17-6-29.
 */

public class ActivityManager {
    public static void startLoginActivity(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
