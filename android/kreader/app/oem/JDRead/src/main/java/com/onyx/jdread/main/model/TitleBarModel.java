package com.onyx.jdread.main.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-12-27.
 */

public class TitleBarModel extends BaseObservable {
    public final ObservableField<String> title = new ObservableField<>();

    public final ObservableField<Object> backEvent = new ObservableField<>();

    public EventBus eventBus;

    public TitleBarModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void back() {
        eventBus.post(backEvent.get());
    }
}
