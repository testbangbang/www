package com.onyx.android.eschool.utils;

import com.onyx.android.eschool.events.LoadFinishEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/4/15.
 */

public class EventUtils {

    public static void postLoadFinishEvent(EventBus eventBus) {
        post(eventBus, new LoadFinishEvent());
    }

    public static void post(EventBus eventBus, Object event) {
        eventBus.post(event);
    }
}
