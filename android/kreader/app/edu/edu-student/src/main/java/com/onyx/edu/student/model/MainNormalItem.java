package com.onyx.edu.student.model;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/10/25.
 */
public class MainNormalItem extends BaseObservable {
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> options = new ObservableField<>();
    public ObservableField<Integer> resId = new ObservableField<>();
    public ObservableField<Boolean> selection = new ObservableField<>(false);
    private Intent intent;
    private final EventBus eventBus;

    public MainNormalItem(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public MainNormalItem setTitle(String text) {
        this.title.set(text);
        return this;
    }

    public MainNormalItem setOptions(String text) {
        this.options.set(text);
        return this;
    }

    public MainNormalItem setResId(int resId) {
        this.resId.set(resId);
        return this;
    }

    public MainNormalItem setIntent(Intent intent) {
        this.intent = intent;
        return this;
    }

    public MainNormalItem setSelection(boolean select) {
        this.selection.set(select);
        return this;
    }

    public MainNormalItem switchSelection() {
        this.selection.set(!selection.get());
        return this;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public void itemClicked() {
        eventBus.post(this);
    }
}
