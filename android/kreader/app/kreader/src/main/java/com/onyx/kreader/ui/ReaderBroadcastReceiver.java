package com.onyx.kreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import com.onyx.kreader.ui.events.ForceCloseEvent;
import com.onyx.kreader.ui.events.ResizeReaderWindowEvent;
import com.onyx.kreader.ui.events.DocumentActivatedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by joy on 2/23/17.
 */

public class ReaderBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_CLOSE_READER = "com.onyx.kreader.action.CLOSE_READER";
    public static final String ACTION_RESIZE_WINDOW = "com.onyx.kreader.action.RESIZE_WINDOW";
    public static final String ACTION_DOCUMENT_ACTIVATED = "com.onyx.kreader.action.DOCUMENT_ACTIVATED";

    public static final String TAG_WINDOW_WIDTH = "com.onyx.kreader.WINDOW_WIDTH";
    public static final String TAG_WINDOW_HEIGHT = "com.onyx.kreader.WINDOW_HEIGHT";
    public static final String TAG_DOCUMENT_PATH = "com.onyx.kreader.DOCUMENT_PATH";

    private static EventBus eventBus;

    public static void setEventBus(EventBus eventBus) {
        ReaderBroadcastReceiver.eventBus = eventBus;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "onReceive: " + intent);
        if (eventBus == null) {
            Log.e(getClass().getSimpleName(), "null bus");
            return;
        }
        if (intent.getAction().equals(ACTION_CLOSE_READER)) {
            eventBus.post(new ForceCloseEvent());
        } else if (intent.getAction().equals(ACTION_RESIZE_WINDOW)) {
            eventBus.post(new ResizeReaderWindowEvent(
                    intent.getIntExtra(TAG_WINDOW_WIDTH, WindowManager.LayoutParams.MATCH_PARENT),
                    intent.getIntExtra(TAG_WINDOW_HEIGHT, WindowManager.LayoutParams.MATCH_PARENT)));
        } else if (intent.getAction().equals(ACTION_DOCUMENT_ACTIVATED)) {
            eventBus.post(new DocumentActivatedEvent(intent.getStringExtra(TAG_DOCUMENT_PATH)));
        }
    }
}
