package com.onyx.android.note.handler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by lxm on 2018/2/2.
 */

public class HandlerManager {

    private EventBus eventBus;

    public HandlerManager(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

}
