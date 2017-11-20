package com.onyx.edu.student.holder;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/4/15.
 */

public class BaseDataHolder {

    private EventBus eventBus = new EventBus();

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
