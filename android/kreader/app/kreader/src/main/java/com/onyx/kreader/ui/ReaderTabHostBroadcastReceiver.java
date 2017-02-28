package com.onyx.kreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;

/**
 * Created by joy on 2/23/17.
 */

public class ReaderTabHostBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_CHANGE_SCREEN_ORIENTATION = "com.onyx.kreader.action.CHANGE_SCREEN_ORIENTATION";

    public static final String TAG_SCREEN_ORIENTATION = "com.onyx.kreader.action.SCREEN_ORIENTATION";

    public static abstract class Callback {
        public abstract void onChangeOrientation(int orientation);
    }

    private static Callback callback;

    public static void setCallback(Callback callback) {
        ReaderTabHostBroadcastReceiver.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "onReceive: " + intent);
        if (intent.getAction().equals(ACTION_CHANGE_SCREEN_ORIENTATION)) {
            if (callback != null) {
                callback.onChangeOrientation(intent.getIntExtra(TAG_SCREEN_ORIENTATION,
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
            }
        }
    }
}
