package com.onyx.android.note.common.base;


import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2018/1/31.
 */

public abstract class BaseViewHandler {

    private EventBus eventBus;

    public BaseViewHandler(@NonNull EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void subscribe() {
        register();
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void unsubscribe() {
        unregister();
    }

    private void register() {
        eventBus.register(this);
    }

    private void unregister() {
        eventBus.unregister(this);
    }
}
