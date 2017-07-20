package com.onyx.android.dr.reader.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by hehai on 17-6-14.
 */

public class ScreenUtil {

    public static void screenshot(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SCREENSHOT");
        context.sendBroadcast(intent);
    }

    public static void toggleA2Mode(Context context){
        Intent intent = new Intent();
        intent.setAction("com.android.internal.policy.statusbar.ToggleA2");
        context.sendBroadcast(intent);
    }
}
