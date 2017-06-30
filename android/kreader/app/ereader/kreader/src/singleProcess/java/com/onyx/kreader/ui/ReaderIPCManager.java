package com.onyx.kreader.ui;

import com.onyx.kreader.ui.events.ChangeOrientationEvent;

/**
 * Created by joy on 6/30/17.
 */

public class ReaderIPCManager {

    public static void onChangeOrientation(final ReaderActivity activity, final ChangeOrientationEvent event) {
    }

    public static void onOpenDocumentFailed(final ReaderActivity activity, final String path) {
    }

    public static void onFullScreenChanged(final ReaderActivity activity, final boolean fullScreen) {
        if (fullScreen) {
        } else {
        }
    }

    public static void onBackPressed(final ReaderActivity activity) {
    }

    public static void onShowTabHostWidget(final ReaderActivity activity) {
    }

}
