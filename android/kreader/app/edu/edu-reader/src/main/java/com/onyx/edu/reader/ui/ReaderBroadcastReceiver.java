package com.onyx.edu.reader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.onyx.android.sdk.utils.Debug;
import com.onyx.edu.reader.ui.events.DocumentActivatedEvent;
import com.onyx.edu.reader.ui.events.ForceCloseEvent;
import com.onyx.edu.reader.ui.events.MoveTaskToBackEvent;
import com.onyx.edu.reader.ui.events.ResizeReaderWindowEvent;
import com.onyx.edu.reader.ui.events.UpdateTabWidgetVisibilityEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by joy on 2/23/17.
 */

public class ReaderBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_CLOSE_READER = "com.onyx.kreader.action.CLOSE_READER";
    public static final String ACTION_MOVE_TASK_TO_BACK = "com.onyx.kreader.action.MOVE_TASK_TO_BACK";
    public static final String ACTION_RESIZE_WINDOW = "com.onyx.kreader.action.RESIZE_WINDOW";
    public static final String ACTION_DOCUMENT_ACTIVATED = "com.onyx.kreader.action.DOCUMENT_ACTIVATED";
    public static final String ACTION_UPDATE_TAB_WIDGET_VISIBILITY = "com.onyx.kreader.action.UPDATE_TAB_WIDGET_VISIBILITY";
    public static final String ACTION_ENABLE_DEBUG_LOG = "com.onyx.kreader.action.ENABLE_DEBUG_LOG";
    public static final String ACTION_DISABLE_DEBUG_LOG = "com.onyx.kreader.action.DISABLE_DEBUG_LOG";

    public static final String TAG_WINDOW_GRAVITY = "com.onyx.kreader.WINDOW_GRAVITY";
    public static final String TAG_WINDOW_WIDTH = "com.onyx.kreader.WINDOW_WIDTH";
    public static final String TAG_WINDOW_HEIGHT = "com.onyx.kreader.WINDOW_HEIGHT";
    public static final String TAG_DOCUMENT_PATH = "com.onyx.kreader.DOCUMENT_PATH";
    public static final String TAG_TAB_WIDGET_VISIBLE = "com.onyx.kreader.TAB_WIDGET_VISIBLE";
    public static final String TAG_ENABLE_DEBUG = "com.onyx.kreader.ENABLE_DEBUG";

    private static EventBus eventBus;

    public static void setEventBus(EventBus eventBus) {
        ReaderBroadcastReceiver.eventBus = eventBus;
    }

    public static void sendCloseReaderIntent(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_CLOSE_READER);
        sendBroadcast(context, intent);
    }

    public static void sendMoveTaskToBackIntent(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_MOVE_TASK_TO_BACK);
        sendBroadcast(context, intent);
    }

    public static void sendResizeReaderWindowIntent(Context context, Class clazz, int width, int height) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_RESIZE_WINDOW);
        intent.putExtra(TAG_WINDOW_WIDTH, width);
        intent.putExtra(TAG_WINDOW_HEIGHT, height);
        sendBroadcast(context, intent);
    }

    public static void sendDocumentActivatedIntent(Context context, Class clazz, String path) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_DOCUMENT_ACTIVATED);
        intent.putExtra(TAG_DOCUMENT_PATH, path);
        sendBroadcast(context, intent);
    }

    public static void sendUpdateTabWidgetVisibilityIntent(Context context, Class clazz, boolean visible) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_UPDATE_TAB_WIDGET_VISIBILITY);
        intent.putExtra(TAG_TAB_WIDGET_VISIBLE, visible);
        sendBroadcast(context, intent);
    }

    public static void sendEnableDebugLogIntent(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_ENABLE_DEBUG_LOG);
        sendBroadcast(context, intent);
    }

    public static void sendDisableDebugLogIntent(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_DISABLE_DEBUG_LOG);
        sendBroadcast(context, intent);
    }

    private static void sendBroadcast(Context context, Intent intent) {
        Debug.d(ReaderBroadcastReceiver.class, "sendBroadcast: " + intent);
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Debug.d(getClass(), "onReceive: " + intent);
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
                    intent.getIntExtra(TAG_WINDOW_GRAVITY, Gravity.BOTTOM),
                    intent.getIntExtra(TAG_WINDOW_WIDTH, WindowManager.LayoutParams.MATCH_PARENT),
                    intent.getIntExtra(TAG_WINDOW_HEIGHT, WindowManager.LayoutParams.MATCH_PARENT)));
        } else if (intent.getAction().equals(ACTION_DOCUMENT_ACTIVATED)) {
            eventBus.post(new DocumentActivatedEvent(intent.getStringExtra(TAG_DOCUMENT_PATH)));
        } else if (intent.getAction().equals(ACTION_UPDATE_TAB_WIDGET_VISIBILITY)) {
            boolean visible = intent.getBooleanExtra(ReaderBroadcastReceiver.TAG_TAB_WIDGET_VISIBLE, true);
            eventBus.post(new UpdateTabWidgetVisibilityEvent(visible));
        } else if (intent.getAction().equals(ACTION_ENABLE_DEBUG_LOG)) {
            Debug.setDebug(true);
        } else if (intent.getAction().equals(ACTION_DISABLE_DEBUG_LOG)) {
            Debug.setDebug(false);
        }
    }
}
