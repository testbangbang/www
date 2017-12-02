package com.onyx.kcb.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import com.onyx.kcb.event.OperationEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/9/11.
 */
public class OperationItem extends BaseObservable {
    private static final String TAG = OperationItem.class.getSimpleName();

    public ObservableInt visibility = new ObservableInt(View.VISIBLE);
    public ObservableField<String> text = new ObservableField<>();

    private EventBus eventBus;
    private OperationEvent event;

    public OperationItem(EventBus eventBus, OperationEvent event) {
        setEventBus(eventBus);
        this.event = event;
    }

    public OperationItem setVisibility(int visibility) {
        this.visibility.set(visibility);
        return this;
    }

    public OperationItem setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        return this;
    }

    public OperationItem setText(String text) {
        this.text.set(text);
        return this;
    }

    public void itemClicked() {
        eventBus.post(event);
    }
}