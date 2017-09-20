package com.onyx.android.dr.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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

    public static int getAPPVersionCode(Context ctx) {
        int currentVersionCode = 0;
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            String appVersionName = info.versionName;
            currentVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersionCode;
    }
}
