package com.onyx.einfo.holder;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/4/15.
 */

public class BaseDataHolder {

    private Context context;
    private EventBus eventBus = new EventBus();

    public BaseDataHolder(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
