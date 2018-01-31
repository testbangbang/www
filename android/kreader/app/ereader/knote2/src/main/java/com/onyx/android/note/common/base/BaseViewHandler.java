package com.onyx.android.note.common.base;


import android.support.annotation.NonNull;

import com.onyx.android.note.event.StubEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lxm on 2018/1/31.
 */

public abstract class BaseViewHandler {

    private EventBus eventBus;

    public BaseViewHandler(@NonNull EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void unregister() {
        eventBus.unregister(this);
    }

    @Subscribe
    public void onStubEvent(StubEvent event) {
    }
}
