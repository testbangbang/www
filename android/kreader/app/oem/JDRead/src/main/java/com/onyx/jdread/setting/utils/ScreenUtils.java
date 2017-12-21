package com.onyx.jdread.setting.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by li on 2017/12/21.
 */

public class ScreenUtils {
    public static void toggleA2Mode(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.android.internal.policy.statusbar.ToggleA2");
        context.sendBroadcast(intent);
    }
}
