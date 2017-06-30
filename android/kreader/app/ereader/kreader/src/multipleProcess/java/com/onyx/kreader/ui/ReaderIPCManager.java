package com.onyx.kreader.ui;

import com.onyx.kreader.ui.events.ChangeOrientationEvent;

/**
 * Created by joy on 6/30/17.
 */

public class ReaderIPCManager {

    public static void onChangeOrientation(final ReaderActivity activity, final ChangeOrientationEvent event) {
        ReaderTabHostBroadcastReceiver.sendChangeOrientationIntent(activity, event.getOrientation());
    }

    public static void onOpenDocumentFailed(final ReaderActivity activity, final String path) {
        ReaderTabHostBroadcastReceiver.sendOpenDocumentFailedEvent(activity, path);
    }

    public static void onFullScreenChanged(final ReaderActivity activity, final boolean fullScreen) {
        if (fullScreen) {
            ReaderTabHostBroadcastReceiver.sendEnterFullScreenIntent(activity);
        } else {
            ReaderTabHostBroadcastReceiver.sendQuitFullScreenIntent(activity);
        }
    }

    public static void onBackPressed(final ReaderActivity activity) {
        ReaderTabHostBroadcastReceiver.sendTabBackPressedIntent(activity);
    }

    public static void onShowTabHostWidget(final ReaderActivity activity) {
        ReaderTabHostBroadcastReceiver.sendShowTabWidgetEvent(activity);
    }

}
