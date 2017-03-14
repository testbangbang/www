package com.onyx.kreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import com.onyx.kreader.ui.events.ForceCloseEvent;
import com.onyx.kreader.ui.events.MoveTaskToBackEvent;
import com.onyx.kreader.ui.events.ResizeReaderWindowEvent;
import com.onyx.kreader.ui.events.DocumentActivatedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by joy on 2/23/17.
 */

public class ReaderBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_CLOSE_READER = "com.onyx.kreader.action.CLOSE_READER";
    public static final String ACTION_MOVE_TASK_TO_BACK = "com.onyx.kreader.action.MOVE_TASK_TO_BACK";
    public static final String ACTION_RESIZE_WINDOW = "com.onyx.kreader.action.RESIZE_WINDOW";
    public static final String ACTION_DOCUMENT_ACTIVATED = "com.onyx.kreader.action.DOCUMENT_ACTIVATED";

    public static final String TAG_WINDOW_WIDTH = "com.onyx.kreader.WINDOW_WIDTH";
    public static final String TAG_WINDOW_HEIGHT = "com.onyx.kreader.WINDOW_HEIGHT";
    public static final String TAG_DOCUMENT_PATH = "com.onyx.kreader.DOCUMENT_PATH";

    private static EventBus eventBus;

    public static void setEventBus(EventBus eventBus) {
        ReaderBroadcastReceiver.eventBus = eventBus;
    }

    public static void sendCloseReaderIntent(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_CLOSE_READER);
        context.sendBroadcast(intent);
    }

    public static void sendMoveTaskToBackIntent(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_MOVE_TASK_TO_BACK);
        context.sendBroadcast(intent);
    }

    public static void sendResizeReaderWindowIntent(Context context, Class clazz, int width, int height) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_RESIZE_WINDOW);
        intent.putExtra(TAG_WINDOW_WIDTH, width);
        intent.putExtra(TAG_WINDOW_HEIGHT, height);
        context.sendBroadcast(intent);
    }

    public static void sendDocumentActivatedIntent(Context context, Class clazz, String path) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_DOCUMENT_ACTIVATED);
        intent.putExtra(TAG_DOCUMENT_PATH, path);
        context.sendBroadcast(intent);
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
        } else if (intent.getAction().equals(ACTION_MOVE_TASK_TO_BACK)) {
            eventBus.post(new MoveTaskToBackEvent());
        } else if (intent.getAction().equals(ACTION_RESIZE_WINDOW)) {
            eventBus.post(new ResizeReaderWindowEvent(
                    intent.getIntExtra(TAG_WINDOW_WIDTH, WindowManager.LayoutParams.MATCH_PARENT),
                    intent.getIntExtra(TAG_WINDOW_HEIGHT, WindowManager.LayoutParams.MATCH_PARENT)));
        } else if (intent.getAction().equals(ACTION_DOCUMENT_ACTIVATED)) {
            eventBus.post(new DocumentActivatedEvent(intent.getStringExtra(TAG_DOCUMENT_PATH)));
        }
    }
}
