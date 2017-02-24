package com.onyx.kreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.QuitEvent;

/**
 * Created by joy on 2/23/17.
 */

public class ReaderCloseBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_CLOSE = "com.onyx.kreader.action.CLOSE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "onReceive: " + intent);
        ReaderDataHolder.getEventBus().post(new QuitEvent());
    }
}
