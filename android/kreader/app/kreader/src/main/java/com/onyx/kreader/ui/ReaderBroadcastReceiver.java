package com.onyx.kreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.QuitEvent;
import com.onyx.kreader.ui.events.ResizeReaderWindowEvent;

/**
 * Created by joy on 2/23/17.
 */

public class ReaderBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_CLOSE = "com.onyx.kreader.action.CLOSE";
    public static final String ACTION_RESIZE_WINDOW = "com.onyx.kreader.action.RESIZE_WINDOW";

    public static final String TAG_WINDOW_WIDTH = "com.onyx.kreader.WINDOW_WIDTH";
    public static final String TAG_WINDOW_HEIGHT = "com.onyx.kreader.WINDOW_HEIGHT";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "onReceive: " + intent);
        if (intent.getAction().equals(ACTION_CLOSE)) {
            ReaderDataHolder.getEventBus().post(new QuitEvent());
        } else if (intent.getAction().equals(ACTION_RESIZE_WINDOW)) {
            ReaderDataHolder.getEventBus().post(new ResizeReaderWindowEvent(
                    intent.getIntExtra(TAG_WINDOW_WIDTH, WindowManager.LayoutParams.MATCH_PARENT),
                    intent.getIntExtra(TAG_WINDOW_HEIGHT, WindowManager.LayoutParams.MATCH_PARENT)));
        }
    }
}
