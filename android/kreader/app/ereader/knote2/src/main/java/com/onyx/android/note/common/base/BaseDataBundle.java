package com.onyx.android.note.common.base;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2018/1/31.
 */

public abstract class BaseDataBundle {

    private EventBus eventBus;

    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }
}
