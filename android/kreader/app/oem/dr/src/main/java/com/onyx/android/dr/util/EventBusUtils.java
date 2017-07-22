package com.onyx.android.dr.util;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhouzhiming on 2017/7/17.
 */
public class EventBusUtils {

    public static void registerEventBus(Context context) {
        if (!EventBus.getDefault().isRegistered(context)) {
            EventBus.getDefault().register(context);
        }
    }
}
