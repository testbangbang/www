package com.onyx.android.dr.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hehai on 17-4-19.
 */

public class SystemUtils {
    public static void startSystemSettingActivity(Context context) {
        Intent mIntent = new Intent();
        ComponentName comp = new ComponentName("com.android.settings",
                "com.android.settings.Settings");
        mIntent.setComponent(comp);
        mIntent.setAction("android.intent.action.VIEW");
        context.startActivity(mIntent);
    }
}
