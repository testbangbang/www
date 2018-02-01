package com.onyx.android.note.common.base;

import android.databinding.BaseObservable;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2018/1/31.
 */

public class BaseViewModel extends BaseObservable {

    private EventBus eventBus;

    public BaseViewModel(@NonNull EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
