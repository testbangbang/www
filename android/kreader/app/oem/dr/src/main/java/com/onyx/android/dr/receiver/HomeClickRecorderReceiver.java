
package com.onyx.android.dr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.common.ActivityManager;

/**
 * Created by zhouzhiming on 2017/8/12.
 */
public class HomeClickRecorderReceiver extends BroadcastReceiver {
    public static final String HOME_CLICK_ACTION = "com.android.systemui.HOME_BUTTON_CLICK";
    private static final String TAG = HomeClickRecorderReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, action);
        if (HOME_CLICK_ACTION.equals(action)) {
            ActivityManager.startMainActivity(DRApplication.getInstance());
         }
    }
}
