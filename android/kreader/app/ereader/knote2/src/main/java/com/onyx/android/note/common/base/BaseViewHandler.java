package com.onyx.android.note.common.base;

import android.support.annotation.CallSuper;

import com.onyx.android.note.event.StubEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lxm on 2018/2/26.
 */

public class BaseViewHandler {

    private EventBus eventBus;

    public BaseViewHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    @CallSuper
    public void subscribe() {
        getEventBus().register(this);
    }

    @CallSuper
    public void unSubscribe() {
        getEventBus().unregister(this);
    }

    @Subscribe
    public void StubEvent(StubEvent event) {}
}
