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
    public static final String ACTION_TAB_BACK_PRESSED = "com.onyx.kreader.action.TAB_BACK_PRESSED";
    public static final String ACTION_CHANGE_SCREEN_ORIENTATION = "com.onyx.kreader.action.CHANGE_SCREEN_ORIENTATION";
    public static final String ACTION_ENTER_FULL_SCREEN = "com.onyx.kreader.action.ENTER_FULL_SCREEN";
    public static final String ACTION_QUIT_FULL_SCREEN = "com.onyx.kreader.action.QUIT_FULL_SCREEN";

    public static final String TAG_SCREEN_ORIENTATION = "com.onyx.kreader.action.SCREEN_ORIENTATION";

    public static abstract class Callback {
        public abstract void onTabBackPressed();
        public abstract void onChangeOrientation(int orientation);
        public abstract void onEnterFullScreen();
        public abstract void onQuitFullScreen();
    }

    private static Callback callback;

    public static void setCallback(Callback callback) {
        ReaderTabHostBroadcastReceiver.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "onReceive: " + intent);
        if (callback == null) {
            return;
        }

        if (intent.getAction().equals(ACTION_TAB_BACK_PRESSED)) {
            callback.onTabBackPressed();
        } else if (intent.getAction().equals(ACTION_CHANGE_SCREEN_ORIENTATION)) {
            callback.onChangeOrientation(intent.getIntExtra(TAG_SCREEN_ORIENTATION,
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        } else if (intent.getAction().equals(ACTION_ENTER_FULL_SCREEN)) {
            callback.onEnterFullScreen();
        } else if (intent.getAction().equals(ACTION_QUIT_FULL_SCREEN)) {
            callback.onQuitFullScreen();
        }
    }
}
